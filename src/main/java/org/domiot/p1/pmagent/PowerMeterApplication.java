package org.domiot.p1.pmagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.extern.slf4j.Slf4j;

import org.domiot.p1.pmagent.config.DeviceConfig;
import org.domiot.p1.pmagent.config.MqttConfig;
import org.domiot.p1.pmagent.config.PowerMeterConfig;
import org.domiot.p1.pmagent.config.SerialPortConfig;
import org.domiot.p1.pmagent.mapper.DeviceMapper;
import org.domiot.p1.pmagent.mqtt.MqttService;
import org.domiot.p1.pmagent.p1.P1Reader;
import org.domiot.p1.pmagent.runtime.RuntimeFactory;
import org.domiot.p1.sensor.SensorValueSender;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;

/**
 * The service reads datagrams from the P1 serial interface<br> It sends a series of SensorValue objects to an MQTT broker<br> The serial port reader is
 * separated from the sending thread and coupled via a queue.<br> The capacity of the queue is QUEUE_SIZE / (VALUE_LOOP * NR_VALUES_PER_LOOP *
 * LOOPS_IN_MINUTE)<br> which is 25 minutes with 7 new measurements in a 10 seconds value loop.<br> So there is at least half an hour time when the MQTT
 * connection is dropped for re-establishing.
 */
@Slf4j
public class PowerMeterApplication {
    private static final String SERIAL_CMD = "cu -l %s -s %d";
    private static final int WAIT_FOR_SERIAL_DATA = 500;

    public static void main(String[] args)
            throws Exception {
        InputStream is = PowerMeterApplication.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(is);
        Attributes mainAttrs = manifest.getMainAttributes();
        String title = mainAttrs.getValue("Implementation-Title");
        String version = mainAttrs.getValue("Implementation-Version");
        String classifier = mainAttrs.getValue("Implementation-Classifier");

        showBanner();
        log.info("Starting {}: {}-{}", title, version, classifier);
        Runtime.getRuntime().addShutdownHook(new PowerMeterShutdownHook());
        if (args.length < 1) {
            showUsage(version, classifier);
            return;
        }
        new PowerMeterApplication().run(args[0]);
    }

    private static void showBanner() {
        String text = new Scanner(Objects.requireNonNull(PowerMeterApplication.class.getResourceAsStream("/banner.txt")),
                StandardCharsets.UTF_8)
                .useDelimiter("\\A").next();
        log.info("\n{}", text);
    }

    private static void showUsage(String version, String classifier) {
        System.out.println("Missing configuration file!");
        System.out.println("Usage:");
        System.out.println("java -jar PowerMeter-" + version + "-" + classifier + " config.yml");
    }

    public void run(String configFileName)
            throws IOException, InterruptedException, MqttException {

        DeviceConfig deviceConfig = PowerMeterConfig.loadConfigurationFromFile(configFileName);

        DeviceDto device = DeviceMapper.map(deviceConfig);

        log.info("Configuration loaded: {}", deviceConfig);
        BlockingQueue<SensorValueDto> queue = new ArrayBlockingQueue<>(deviceConfig.getInternalQueueSize());
        SynchronousQueue<DeviceDto> initialConfigQueue = new SynchronousQueue<>();

        MqttConfig mqttConfig = deviceConfig.getMqttBroker();
        MqttService mqttService = new MqttService(mqttConfig, initialConfigQueue);

        SerialPortConfig serialPortConfig = deviceConfig.getSerialPort();
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
            log.error(e.getMessage());
            System.exit(-1);
        }
        BufferedReader p1Reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        RuntimeFactory.addRuntimeInfo(device);
        try {
            if (!mqttService.getMqttClient().isConnected()) {
                mqttService.connectToBroker();
            }
        } catch (MqttException e) {
            System.exit(-1);
        }

        Thread mqttThread = new Thread(new SensorValueSender(queue, mqttService, device));
        mqttThread.start();

        if (!PowerMeterConfig.isAllSensorsHaveIds()) {
            // Send device config once
            mqttService.registerDevice(device);
        }


        P1Reader serialPortReader = new P1Reader(queue, deviceConfig.getSerialPort().getP1Key(), device, p1Reader);
        Thread serialReaderThread = new Thread(serialPortReader);
        DeviceDto updatedDevice = null;
        if (!PowerMeterConfig.isAllSensorsHaveIds()) {
            // Wait for config to be returned, which contains the sensorIds
            updatedDevice = initialConfigQueue.take();
        }
        device = updatedDevice;
        PowerMeterConfig.saveConfigurationToFile(PowerMeterConfig.CONFIG_FILENAME, deviceConfig, device, true);

        serialReaderThread.start();

        mqttThread.join();
        serialReaderThread.join();
    }
}
