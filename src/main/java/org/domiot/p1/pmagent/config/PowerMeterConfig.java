package org.domiot.p1.pmagent.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.model.SensorType;
import org.yaml.snakeyaml.DumperOptions;
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
    private static DeviceConfig deviceConfig;

    private PowerMeterConfig() {
    }

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

        deviceConfig = yaml.load(inputStream);
        return deviceConfig;
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
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Force block style
        options.setPrettyFlow(true); // Optional: Makes the YAML more human-readable

        yaml.dump(deviceConfig, writer);
    }

    /**
     * Update sensorIds in deviceConfig and save configuration file
     *
     * @param configFilename The config file name
     * @param deviceConfig   The device configuration
     * @param device         THe updated device with sensorIds added
     * @param backupExisting Make a backup of the old configuration file
     */
    public static void saveConfigurationToFile(String configFilename, DeviceConfig deviceConfig, DeviceDto device, boolean backupExisting) throws IOException {
        deviceConfig.getSensorConfigs().forEach(sensorConfig -> {
            Optional<SensorDto> sensorDtoOptional = device.getSensors()
                    .stream()
                    .filter(sensorDto -> sensorDto.getSensorType().equals(sensorConfig.getSensorType()))
                    .findFirst();
            sensorDtoOptional.ifPresent(sensorDto -> sensorConfig.setSensorId(sensorDto.getSensorId()));
        });
        saveConfigurationToFile(configFilename, deviceConfig, backupExisting);
    }

    /**
     * Validates that no sensorId is set to initial 0
     *
     * @return true: All sensors have Ids, false: one or more sensors are in initial state (sensorId is 0)
     */
    public static boolean isAllSensorsHaveIds() {
        Optional<SensorConfig> optionalSensorConfig = deviceConfig.getSensorConfigs()
                .stream()
                .filter(sensorConfig -> sensorConfig.getSensorId() == 0)
                .findFirst();
        return optionalSensorConfig.isEmpty();
    }
}
