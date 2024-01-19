package com.lankheet.pmagent.config;

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
import java.util.List;

/**
 * Configuration object for PowerMeterAgent
 */
@Data
public class PMAgentConfig
{
   @JsonProperty
   private long repeatValuesAfter;

   @JsonProperty
   private int internalQueueSize;

   @JsonProperty
   private String nic;

   @JsonProperty
   private SerialPortConfig serialPort = new SerialPortConfig();

   @JsonProperty
   private MqttConfig mqttBroker = new MqttConfig();

   @JsonProperty
   private List<SensorConfig> sensorConfigs;


   public static PMAgentConfig loadConfigurationFromFile(String configFileName)
      throws IOException
   {
      Constructor constructor = new Constructor(PMAgentConfig.class, new LoaderOptions());
      TypeDescription pmAgentConfigTypeDescription = new TypeDescription(PMAgentConfig.class);
      constructor.addTypeDescription(pmAgentConfigTypeDescription);
      pmAgentConfigTypeDescription.addPropertyParameters("repeatValuesAfter", PMAgentConfig.class);
      pmAgentConfigTypeDescription.addPropertyParameters("internalQueueSize", PMAgentConfig.class);
      pmAgentConfigTypeDescription.addPropertyParameters("nic", String.class);
      pmAgentConfigTypeDescription.addPropertyParameters("sensorConfigs", SensorConfig.class);
      pmAgentConfigTypeDescription.addPropertyParameters("mqttBroker", MqttConfig.class);

      TypeDescription sensorConfigTypeDescription = new TypeDescription(Sensor.class);
      sensorConfigTypeDescription.addPropertyParameters("sensorType", SensorType.class);
      sensorConfigTypeDescription.addPropertyParameters("mqttTopic", String.class);

      TypeDescription mqttConfigTypeDescription = new TypeDescription(MqttConfig.class);
      mqttConfigTypeDescription.addPropertyParameters("subscriptions", String.class);
      mqttConfigTypeDescription.addPropertyParameters("url", String.class);
      mqttConfigTypeDescription.addPropertyParameters("userName", String.class);
      mqttConfigTypeDescription.addPropertyParameters("password", String.class);
      mqttConfigTypeDescription.addPropertyParameters("clientName", String.class);

      TypeDescription mqttTopicConfigTypeDescription = new TypeDescription(MqttTopicConfig.class);
      mqttTopicConfigTypeDescription.addPropertyParameters("topic", String.class);
      mqttTopicConfigTypeDescription.addPropertyParameters("type", TopicType.class);

      TypeDescription serialPortConfigTypeDescription = new TypeDescription(SerialPortConfig.class);
      serialPortConfigTypeDescription.addPropertyParameters("p1Key", String.class);
      serialPortConfigTypeDescription.addPropertyParameters("uart", String.class);
      serialPortConfigTypeDescription.addPropertyParameters("baudRate", String.class);

      constructor.addTypeDescription(sensorConfigTypeDescription);
      constructor.addTypeDescription(pmAgentConfigTypeDescription);
      constructor.addTypeDescription(mqttConfigTypeDescription);
      constructor.addTypeDescription(mqttTopicConfigTypeDescription);
      constructor.addTypeDescription(serialPortConfigTypeDescription);

      Yaml yaml = new Yaml(constructor);
      InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

      PMAgentConfig pmAgentConfig = yaml.load(inputStream);
      return pmAgentConfig;
   }
}
