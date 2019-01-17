package com.lankheet.pmagent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.utils.NetUtils;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It sends a series of SensorValue objects to an MQTT broker<br>
 */
public class PowerMeterAgent {
    private static final int QUEUE_SIZE = 1000;
    private static final Logger LOG = LoggerFactory.getLogger(PowerMeterAgent.class);


    public static void main(String[] args) throws Exception {
        InputStream is = PowerMeterAgent.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(is);
        Attributes mainAttrs = manifest.getMainAttributes();
        String title = mainAttrs.getValue("Implementation-Title");
        String version = mainAttrs.getValue("Implementation-Version");
        String classifier = mainAttrs.getValue("Implementation-Classifier");

        showBanner();
        LOG.info("Starting " + title + ": " + version + "-" + classifier);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOG.warn("Shutdown Hook is cleaning up!");
            }
        });
        if (args.length < 1) {
            showUsage(version, classifier);
            return;
        }
        new PowerMeterAgent().run(args[0]);
    }

    private static void showBanner() throws URISyntaxException, IOException {
        String text = new Scanner(PowerMeterAgent.class.getResourceAsStream("/banner.txt"), "UTF-8").useDelimiter("\\A").next();
        System.out.println(text);
    }

    private static void showUsage(String version, String classifier) {
        System.out.println("Missing configuration file!");
        System.out.println("Usage:");
        System.out.println("java -jar lnb-powermeter-" + version + "-" + classifier + " config.yml");;
    }

    public void run(String configFileName) throws Exception {
        BlockingQueue<SensorValue> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        PMAgentConfig configuration = PMAgentConfig.loadConfigurationFromFile(configFileName);
        LOG.info("Configuration: " + configuration.toString());

        MqttConfig mqttConfig = configuration.getMqttConfig();
        SensorValueSender sensorValueSender = new SensorValueSender(queue, mqttConfig);
        final String nic = configuration.getSensorConfig().getNic();
        SensorNode sensorNode = new SensorNode(NetUtils.getMacAddress(nic), SensorType.POWER_METER.getId());

        SerialPortReader serialPortReader =
                new SerialPortReader(queue, configuration.getSerialPortConfig(), sensorNode);

        new Thread(sensorValueSender).start();
        new Thread(serialPortReader).start();
    }
}
