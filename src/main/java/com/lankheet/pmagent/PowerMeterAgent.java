package com.lankheet.pmagent;

import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.config.SerialPortConfig;
import com.lankheet.pmagent.mqtt.MqttService;
import com.lankheet.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import com.lankheet.pmagent.mapper.SensorMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.SensorValue;
import org.lankheet.domiot.utils.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * The service reads datagrams from the P1 serial interface<br> It sends a series of SensorValue objects to an MQTT broker<br> The serial port reader is
 * separated from the sending thread and coupled via a queue.<br> The capacity of the queue is QUEUE_SIZE / (VALUE_LOOP * NR_VALUES_PER_LOOP *
 * LOOPS_IN_MINUTE)<br> which is 25 minutes with 7 new measurements in a 10 seconds value loop.<br> So there is at least half an hour time when the MQTT
 * connection is dropped for re-establishing.
 */
@Slf4j
public class PowerMeterAgent {
    private static final String SERIAL_CMD = "cu -l %s -s %d";
    private static final int WAIT_FOR_SERIAL_DATA = 500;

    public static void main(String[] args)
            throws Exception {
        InputStream is = PowerMeterAgent.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(is);
        Attributes mainAttrs = manifest.getMainAttributes();
        String title = mainAttrs.getValue("Implementation-Title");
        String version = mainAttrs.getValue("Implementation-Version");
        String classifier = mainAttrs.getValue("Implementation-Classifier");

        showBanner();
        log.info("Starting {}: {}-{}", title, version, classifier);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.warn("Shutdown Hook is cleaning up!")));
        if (args.length < 1) {
            showUsage(version, classifier);
            return;
        }
        new PowerMeterAgent().run(args[0]);
    }

    private static void showBanner() {
        String text = new Scanner(Objects.requireNonNull(PowerMeterAgent.class.getResourceAsStream("/banner.txt")), "UTF-8").useDelimiter("\\A").next();
        log.info(text);
    }

    private static void showUsage(String version, String classifier) {
        // TODO: Create console logger and log via this logger
        System.out.println("Missing configuration file!");
        System.out.println("Usage:");
        System.out.println("java -jar lnb-powermeter-" + version + "-" + classifier + " config.yml");
    }

    public void run(String configFileName)
            throws IOException, InterruptedException, MqttException {
        PMAgentConfig configuration = PMAgentConfig.loadConfigurationFromFile(configFileName);
        log.info("Configuration: {}", configuration);
        BlockingQueue<SensorValue> queue = new ArrayBlockingQueue<>(configuration.getInternalQueueSize());

        MqttConfig mqttConfig = configuration.getMqttBroker();
        Thread mqttThread = new Thread(new SensorValueSender(queue, mqttConfig));
        mqttThread.start();

        SerialPortConfig serialPortConfig = configuration.getSerialPort();
        String port = serialPortConfig.getUart();
        int baudRate = serialPortConfig.getBaudRate();
        Process process = null;

        try {
            String command = String.format(SERIAL_CMD, port, baudRate);
            log.info(command);
            process = Runtime.getRuntime().exec(command);
            Thread.sleep(WAIT_FOR_SERIAL_DATA);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot open serial port {}", port);
            System.exit(-1);
        }
        BufferedReader p1Reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        Device device = new Device()
                .macAddress(NetUtils.getMacAddress(configuration.getNic()))
                .sensors(configuration.getSensorConfigs().stream().map(SensorMapper::map).toList());
        // Send device config once
        registerDevice(mqttConfig, device);

        P1Reader serialPortReader = new P1Reader(queue, configuration.getSerialPort().getP1Key(), device, p1Reader);
        Thread serialReaderThread = new Thread(serialPortReader);
        serialReaderThread.start();

        mqttThread.join();
        serialReaderThread.join();
    }

    private void registerDevice(MqttConfig mqttConfig, Device device) throws MqttException {
        MqttService mqttService = new MqttService(mqttConfig);
        MqttClient mqttClient = mqttService.connectToBroker();
        MqttMessage message = new MqttMessage();
        message.setPayload(JsonUtil.toJson(device).getBytes());
        mqttClient.publish("config", message);
    }
}
