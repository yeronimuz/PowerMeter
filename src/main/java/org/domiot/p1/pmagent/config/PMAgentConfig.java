package org.domiot.p1.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lankheet.domiot.model.Sensor;
import org.lankheet.domiot.model.SensorType;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Configuration object for PowerMeterAgent
 */
@Data
public class PMAgentConfig {
    @JsonProperty
    private DeviceConfig deviceConfig;

    public static DeviceConfig loadConfigurationFromFile(String configFileName)
            throws IOException {
        Constructor constructor = new Constructor(DeviceConfig.class, new LoaderOptions());
        TypeDescription deviceConfigTypeDescription = new TypeDescription(DeviceConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("internalQueueSize", DeviceConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("nic", String.class);
        deviceConfigTypeDescription.addPropertyParameters("sensorConfigs", SensorConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("mqttBroker", MqttConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("serialPort", SerialPortConfig.class);

        TypeDescription sensorConfigTypeDescription = new TypeDescription(Sensor.class);
        sensorConfigTypeDescription.addPropertyParameters("sensorType", SensorType.class);
        sensorConfigTypeDescription.addPropertyParameters("description", String.class);
        sensorConfigTypeDescription.addPropertyParameters("mqttTopic", MqttTopicConfig.class);

        TypeDescription mqttTopicConfigTypeDescription = new TypeDescription(MqttTopicConfig.class);
        mqttTopicConfigTypeDescription.addPropertyParameters("topic", String.class);
        mqttTopicConfigTypeDescription.addPropertyParameters("topicType", String.class);

        TypeDescription mqttConfigTypeDescription = new TypeDescription(MqttConfig.class);
        mqttConfigTypeDescription.addPropertyParameters("subscriptions", String.class);
        mqttConfigTypeDescription.addPropertyParameters("url", String.class);
        mqttConfigTypeDescription.addPropertyParameters("userName", String.class);
        mqttConfigTypeDescription.addPropertyParameters("password", String.class);
        mqttConfigTypeDescription.addPropertyParameters("clientName", String.class);

        TypeDescription serialPortConfigTypeDescription = new TypeDescription(SerialPortConfig.class);
        serialPortConfigTypeDescription.addPropertyParameters("p1Key", String.class);
        serialPortConfigTypeDescription.addPropertyParameters("uart", String.class);
        serialPortConfigTypeDescription.addPropertyParameters("baudRate", String.class);

        constructor.addTypeDescription(deviceConfigTypeDescription);
        constructor.addTypeDescription(sensorConfigTypeDescription);
        constructor.addTypeDescription(mqttConfigTypeDescription);
        constructor.addTypeDescription(serialPortConfigTypeDescription);
        constructor.addTypeDescription(mqttTopicConfigTypeDescription);

        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

        return yaml.load(inputStream);
    }
}
