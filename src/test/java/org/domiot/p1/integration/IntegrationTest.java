// File: IntegrationTest.java
package org.domiot.p1.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

import org.domiot.p1.pmagent.PowerMeterApplication;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

/**
 * This unit-integrationTest is a complete round-trip test. The following flows are tested:
 * <li>Registering device by the PowerMeter app</li>
 * <li>Listening and recording sensor values</li>
 * <BR>
 * The unit test:
 * <li>takes care of sending the config to the PowerMeter</li>
 * <li>Sends P1 data to a file descriptor that acts as serial port</li>
 */
@Slf4j
@Testcontainers
class IntegrationTest {

    static ToStringConsumer mosquittoLogConsumer = new ToStringConsumer();
    static Path pipePath;
    static int msgCount;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        Thread powerMeterThread = new Thread(() -> {
            try {
                String configFileName = "src/test/resources/power-meter.yml";
                String copyConfigFileName = "build/tmp/test/power-meter.yml";
                // Copy config file, because it will be overwritten by PowerMeter
                Files.copy(Files.newInputStream(Paths.get(configFileName)),
                        Paths.get(copyConfigFileName), StandardCopyOption.REPLACE_EXISTING);
                PowerMeterApplication app = new PowerMeterApplication();
                app.runPowerMeter(copyConfigFileName);
            } catch (Exception e) {
                log.error("PowerMeter failed to start", e);
            }
        }, "PowerMeter-thread");
        powerMeterThread.start();

        // Create named pipe for serial input
        pipePath = Paths.get("/tmp/fake-serial-pipe");
        Files.deleteIfExists(pipePath);
        Process mkfifo = new ProcessBuilder("mkfifo", "-m", "0666", pipePath.toString())
                .start();
        int result = mkfifo.waitFor();
        log.info("mkfifo {}", (result == 0) ? "completed" : "FAILED");
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(pipePath);
    }

    /*
      docker run -it --rm -p 1883:1883 -v src/test/resources/mosquitto:/mosquitto/config eclipse-mosquitto
    */
    @Container
    static GenericContainer<?> mosquitto = new GenericContainer<>("eclipse-mosquitto:2.0")
            .withExposedPorts(1883)
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("mosquitto/mosquitto.conf"),
                    "/mosquitto/config/mosquitto.conf")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("mosquitto/passwords.txt"),
                    "/mosquitto/config/passwords.txt")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(10)))
            .withLogConsumer(mosquittoLogConsumer)
            .withCreateContainerCmdModifier(cmd -> {
                Objects.requireNonNull(cmd.getHostConfig()).withPortBindings(
                        new com.github.dockerjava.api.model.PortBinding(
                                com.github.dockerjava.api.model.Ports.Binding.bindPort(1883),
                                new com.github.dockerjava.api.model.ExposedPort(1883)
                        )
                );
            });

    @Test
    void integrationTestWithNamedPipe() throws Exception {
        try {
            mosquitto.start();
        } catch (Exception e) {
            System.err.println("Mosquitto container failed to start:");
            System.err.println(mosquittoLogConsumer.toUtf8String());
            throw e;
        }
        mosquitto.setLogConsumers(Collections.singletonList(outputFrame -> System.out.print(outputFrame.getUtf8String())));

        // Set up MQTT client to verify sensor message
        try (MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId())) {
            CountDownLatch sensorLatch;
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setUserName("johndoe");
            options.setPassword("noaccess".toCharArray());
            client.connect(options);
            log.info("client connected");

            CountDownLatch registerLatch = new CountDownLatch(1);

            // Subscribe to device registering and get the MAC address
            // PowerMeter will only accept config of it's own device (identified by MAC address)
            // Therefore, we must rewrite the MAC address in the config with value of the register.
            final String[] deviceMacAddress = new String[1];
            client.subscribe("register", (topic, msg) -> {
                String registerDevice = new String(msg.getPayload());
                deviceMacAddress[0] = extractMacAddressFromDeviceJson(registerDevice);
                log.info("Received register data: {}", registerDevice);
                registerLatch.countDown();
            });
            log.info("Subscribed to register");

            boolean received = registerLatch.await(60, TimeUnit.SECONDS);
            assertTrue(received, "Device register not received in time");

            String deviceConfigJson = Files.readString(
                    Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("config_device.json")).toURI()));
            deviceConfigJson = updateMacAddressFromDeviceJson(deviceConfigJson, deviceMacAddress[0]);
            // Return config
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(deviceConfigJson.getBytes());

            client.publish("config", mqttMessage);

            // Assert sensor message received
            sensorLatch = new CountDownLatch(7);

            client.subscribe("sensor/#", (topic, msg) -> {
                log.info("Received sensor data: {}", new String(msg.getPayload()));
                String msgReceived = msg.getPayload().toString();
                log.info("Received sensor data: {}", msgReceived);
                msgCount++;
                sensorLatch.countDown();
            });
            log.info("Subscribed to sensor values");
            sendSerialData();
            received = sensorLatch.await(10, TimeUnit.SECONDS);
            assertTrue(received, "Sensor value not received in time");
            assertEquals(7, msgCount);
            client.disconnect();
        }
    }

    private String extractMacAddressFromDeviceJson(String registerDevice) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(registerDevice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Navigate to the field
        return root.path("macAddress").asText();
    }

    private String updateMacAddressFromDeviceJson(String configDeviceJson, String macAddress) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(configDeviceJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (rootNode instanceof ObjectNode) {
            ((ObjectNode) rootNode).put("macAddress", macAddress);
        }
        return rootNode.toString();
    }

    private void sendSerialData() {
        // Simulate serial data writer in a background thread
        new Thread(() -> {
            try (BufferedWriter writer = Files.newBufferedWriter(pipePath);
                 InputStream is = getClass().getClassLoader().getResourceAsStream("p1v2.log")) {
                assert is != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("ser+++> {}", line);
                        writer.write(line);
                        writer.newLine();
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }).start();

        log.info("Serial data sent");
    }
}
