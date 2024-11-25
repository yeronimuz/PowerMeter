package org.domiot.p1.pmagent.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import org.lankheet.domiot.model.SensorType;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Configuration object for PowerMeterAgent
 */
@Data
public class PowerMeterConfig {
    public static final String CONFIG_FILENAME = "power-meter.yml";
    @JsonProperty
    private DeviceConfig deviceConfig;

    /**
     * Load config yaml
     *
     * @param configFileName The file containing the config parameters
     * @return A configured Device
     * @throws IOException Unable to load file
     */
    public static DeviceConfig loadConfigurationFromFile(String configFileName)
            throws IOException {
        Constructor constructor = new Constructor(DeviceConfig.class, new LoaderOptions());
        TypeDescription deviceConfigTypeDescription = new TypeDescription(DeviceConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("deviceParameters", ConfigParameter.class);
        deviceConfigTypeDescription.addPropertyParameters("sensorConfigs", SensorConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("mqttBroker", MqttConfig.class);
        deviceConfigTypeDescription.addPropertyParameters("serialPort", SerialPortConfig.class);

        TypeDescription parameterConfigTypeDescription = new TypeDescription(ConfigParameter.class);
        parameterConfigTypeDescription.addPropertyParameters("name", String.class);
        parameterConfigTypeDescription.addPropertyParameters("type", String.class);
        parameterConfigTypeDescription.addPropertyParameters("value", String.class);
        parameterConfigTypeDescription.addPropertyParameters("readonly", Boolean.class);

        TypeDescription sensorConfigTypeDescription = new TypeDescription(SensorConfig.class);
        sensorConfigTypeDescription.addPropertyParameters("sensorId", Integer.class);
        sensorConfigTypeDescription.addPropertyParameters("sensorType", SensorType.class);
        sensorConfigTypeDescription.addPropertyParameters("description", String.class);
        sensorConfigTypeDescription.addPropertyParameters("mqttTopicConfig", MqttTopicConfig.class);
        sensorConfigTypeDescription.addPropertyParameters("parameters", ConfigParameter.class);

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
        constructor.addTypeDescription(parameterConfigTypeDescription);

        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

        return yaml.load(inputStream);
    }

    /**
     * Save the current configuration to file for the next reload of configuration
     *
     * @param configFileName The filename to use
     * @param deviceConfig   The device configuration to use
     * @param backupExisting Make a backup of the old configuration file
     * @throws IOException The backup file or destination file could not be written
     */
    public static void saveConfigurationToFile(String configFileName, DeviceConfig deviceConfig, boolean backupExisting) throws IOException {
        if (backupExisting) {
            Files.copy(Paths.get(CONFIG_FILENAME), Paths.get(CONFIG_FILENAME + ".org"));
        }
        PrintWriter writer = new PrintWriter(new File("./" + configFileName));
        Yaml yaml = new Yaml();
        yaml.dump(deviceConfig, writer);
    }
}
