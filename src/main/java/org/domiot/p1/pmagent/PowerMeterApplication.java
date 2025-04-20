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
import java.util.concurrent.CompletableFuture;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.extern.slf4j.Slf4j;

import org.domiot.p1.pmagent.config.DeviceConfig;
import org.domiot.p1.pmagent.config.MqttConfig;
import org.domiot.p1.pmagent.config.PowerMeterConfig;
import org.domiot.p1.pmagent.config.SerialPortConfig;
import org.domiot.p1.pmagent.mapper.DeviceMapper;
import org.domiot.p1.pmagent.mqtt.MqttConfigListener;
import org.domiot.p1.pmagent.mqtt.MqttService;
import org.domiot.p1.pmagent.p1.P1Reader;
import org.domiot.p1.pmagent.runtime.RuntimeFactory;
import org.domiot.p1.sensor.SensorValueSender;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;

/**
 * P1 Powermeter application.
 */
@Slf4j
public class PowerMeterApplication implements MqttConfigListener {
    private static final String SERIAL_CMD = "cu -l %s -s %d";
    private static final int WAIT_FOR_SERIAL_DATA = 500;
    private final CompletableFuture<DeviceDto> configFuture = new CompletableFuture<>();

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
        new PowerMeterApplication().runPowerMeter(args[0]);
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

    public void runPowerMeter(String configFileName)
            throws Exception {

        DeviceConfig deviceConfig = PowerMeterConfig.loadConfigurationFromFile(configFileName);

        DeviceDto deviceDto = DeviceMapper.map(deviceConfig);

        log.info("Configuration loaded: {}", deviceConfig);
        BlockingQueue<SensorValueDto> queue = new ArrayBlockingQueue<>(deviceConfig.getInternalQueueSize());

        MqttConfig mqttConfig = deviceConfig.getMqttBroker();
        MqttService mqttService = new MqttService(mqttConfig);

        mqttService.addConfigUpdateListener(this);

        SerialPortConfig serialPortConfig = deviceConfig.getSerialPort();
        String port = serialPortConfig.getUart();
        int baudRate = serialPortConfig.getBaudRate();
        Process process = null;

        try {
            String command = String.format(SERIAL_CMD, port, baudRate);
            log.info(command);
            process = Runtime.getRuntime().exec(command);
            log.info("Waiting for serial data");
            Thread.sleep(WAIT_FOR_SERIAL_DATA);
        } catch (IOException | InterruptedException e) {
            log.error("Cannot open serial port {}", port);
            log.error(e.getMessage());
            System.exit(-1);
        }
        BufferedReader p1Reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        RuntimeFactory.addRuntimeInfo(deviceDto);

        DeviceDto deviceDtoFromBackend = null;
        if (!PowerMeterConfig.isAllSensorsHaveIds(deviceConfig)) {
            // Send device config once
            mqttService.registerDevice(deviceDto);
            log.info("Registered device, waiting for config");
            deviceDtoFromBackend = configFuture.get();
            log.debug("Device config updated: {}", deviceDto);
        }

        if (!mqttService.getMqttClient().isConnected()) {
            log.info("Connecting to MQTT broker");
            mqttService.connectToBroker();
        }

        if (deviceDtoFromBackend != null && deviceDtoFromBackend.getMacAddress().equals(deviceDto.getMacAddress())) {
            deviceDto = deviceDtoFromBackend;
            PowerMeterConfig.saveConfigurationToFile(PowerMeterConfig.CONFIG_FILENAME, deviceConfig, deviceDto, true);
        }

        Thread mqttThread = new Thread(new SensorValueSender(queue, mqttService, deviceDto));

        P1Reader serialPortReader = new P1Reader(queue, deviceConfig.getSerialPort().getP1Key(), deviceDto, p1Reader);
        Thread serialReaderThread = new Thread(serialPortReader);

        mqttThread.start();
        serialReaderThread.start();

        mqttThread.join();
        serialReaderThread.join();
    }

    @Override
    public void onUpdateDevice(DeviceDto deviceDto) {
        log.info("Config received, updating device");
        configFuture.complete(deviceDto);
    }
}
