package com.lankheet.pmagent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.config.SensorConfig;
import com.lankheet.pmagent.config.SerialPortConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.NetUtils;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It sends a series of SensorValue objects to an MQTT broker<br>
 */
public class PowerMeterAgent {
    private static final Logger LOG = LoggerFactory.getLogger(PowerMeterAgent.class);


    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOG.warn("Shutdown Hook is cleaning up!");
            }
        });
        new PowerMeterAgent().run(args[0]);
    }

    public void run(String configFileName) throws Exception {
        BlockingQueue<SensorValue> queue = new ArrayBlockingQueue<>(1000);
        PMAgentConfig configuration = loadConfigurationFromFile(configFileName);

        MqttConfig mqttConfig = configuration.getMqttConfig();
        SensorValueSender sensorValueSender = new SensorValueSender(queue, mqttConfig);
        final String nic = configuration.getSensorConfig().getNic();
        SensorNode sensorNode = new SensorNode(NetUtils.getMacAddress(nic), SensorType.POWER_METER.getId());

        SerialPortReader serialPortReader =
                new SerialPortReader(queue, configuration.getSerialPortConfig(), sensorNode);

        new Thread(sensorValueSender).start();
        new Thread(serialPortReader).start();
    }

    protected PMAgentConfig loadConfigurationFromFile(String configFileName) throws IOException {
        Constructor constructor = new Constructor(PMAgentConfig.class);
        TypeDescription pmAgentConfigTypeDescription = new TypeDescription(PMAgentConfig.class);
        pmAgentConfigTypeDescription.addPropertyParameters("sensorConfig", SensorConfig.class);
        pmAgentConfigTypeDescription.addPropertyParameters("mqttConfig", MqttConfig.class);
        TypeDescription sensorConfigTypeDescription = new TypeDescription(SensorConfig.class);
        sensorConfigTypeDescription.addPropertyParameters("nic", String.class);
        sensorConfigTypeDescription.addPropertyParameters("sensorTypes", SensorType.class);
        TypeDescription mqttConfigTypeDescription = new TypeDescription(MqttConfig.class);
        mqttConfigTypeDescription.addPropertyParameters("topics", MqttTopicConfig.class);
        mqttConfigTypeDescription.addPropertyParameters("url", String.class);
        mqttConfigTypeDescription.addPropertyParameters("userName", String.class);
        mqttConfigTypeDescription.addPropertyParameters("password", String.class);
        TypeDescription mqttTopicConfigTypeDescription = new TypeDescription(MqttTopicConfig.class);
        mqttTopicConfigTypeDescription.addPropertyParameters("topic", String.class);
        mqttTopicConfigTypeDescription.addPropertyParameters("type", TopicType.class);
        TypeDescription serialPortConfigTypeDescription = new TypeDescription(SerialPortConfig.class);
        constructor.addTypeDescription(pmAgentConfigTypeDescription);
        constructor.addTypeDescription(sensorConfigTypeDescription);
        constructor.addTypeDescription(mqttConfigTypeDescription);
        constructor.addTypeDescription(mqttTopicConfigTypeDescription);
        constructor.addTypeDescription(serialPortConfigTypeDescription);

        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

        PMAgentConfig pmAgentConfig = (PMAgentConfig) yaml.load(inputStream);
        return pmAgentConfig;
    }
}
