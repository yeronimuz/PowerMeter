package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.iot.datatypes.entities.SensorType;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PMAgentConfig
{

   @JsonProperty
   private long repeatValuesAfter;

   @JsonProperty
   private int internalQueueSize;

   @JsonProperty
   private SensorConfig sensorConfig = new SensorConfig();

   @JsonProperty
   private SerialPortConfig serialPortConfig = new SerialPortConfig();

   @JsonProperty
   private MqttConfig mqttConfig = new MqttConfig();


   public long getRepeatValuesAfter()
   {
      return repeatValuesAfter;
   }


   public void setRepeatValuesAfter(long repeatValuesAfter)
   {
      this.repeatValuesAfter = repeatValuesAfter;
   }


   public int getInternalQueueSize()
   {
      return internalQueueSize;
   }


   public void setInternalQueueSize(int internalQueueSize)
   {
      this.internalQueueSize = internalQueueSize;
   }


   /**
    * Get serialPort.
    *
    * @return the serialPort
    */
   public SerialPortConfig getSerialPortConfig()
   {
      return serialPortConfig;
   }


   /**
    * Set serialPort.
    *
    * @param serialPort the serialPort to set
    */
   public void setSerialPort(SerialPortConfig serialPort)
   {
      this.serialPortConfig = serialPort;
   }


   /**
    * Set sensorConfig.
    *
    * @param sensorConfig the sensorConfig to set
    */
   public void setSensorConfig(SensorConfig sensorConfig)
   {
      this.sensorConfig = sensorConfig;
   }


   /**
    * Get mqttConfig.
    *
    * @return the mqttConfig
    */
   public MqttConfig getMqttConfig()
   {
      return mqttConfig;
   }


   /**
    * Set mqttConfig.
    *
    * @param mqttConfig the mqttConfig to set
    */
   public void setMqttConfig(MqttConfig mqttConfig)
   {
      this.mqttConfig = mqttConfig;
   }


   /**
    * Get sensorConfig.
    *
    * @return the sensorConfig
    */
   public SensorConfig getSensorConfig()
   {
      return sensorConfig;
   }


   /**
    * Set serialPortConfig.
    *
    * @param serialPortConfig the serialPortConfig to set
    */
   public void setSerialPortConfig(SerialPortConfig serialPortConfig)
   {
      this.serialPortConfig = serialPortConfig;
   }


   public static PMAgentConfig loadConfigurationFromFile(String configFileName)
      throws IOException
   {
      Constructor constructor = new Constructor(PMAgentConfig.class);
      TypeDescription pmAgentConfigTypeDescription = new TypeDescription(PMAgentConfig.class);
      pmAgentConfigTypeDescription.addPropertyParameters("repeatValuesAfter", PMAgentConfig.class);
      pmAgentConfigTypeDescription.addPropertyParameters("internalQueueSize", PMAgentConfig.class);
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
      mqttConfigTypeDescription.addPropertyParameters("clientName", String.class);
      TypeDescription mqttTopicConfigTypeDescription = new TypeDescription(MqttTopicConfig.class);
      mqttTopicConfigTypeDescription.addPropertyParameters("topic", String.class);
      mqttTopicConfigTypeDescription.addPropertyParameters("type", TopicType.class);
      TypeDescription serialPortConfigTypeDescription = new TypeDescription(SerialPortConfig.class);
      serialPortConfigTypeDescription.addPropertyParameters("p1Key", String.class);
      serialPortConfigTypeDescription.addPropertyParameters("uart", String.class);
      serialPortConfigTypeDescription.addPropertyParameters("baudRate", String.class);
      constructor.addTypeDescription(pmAgentConfigTypeDescription);
      constructor.addTypeDescription(sensorConfigTypeDescription);
      constructor.addTypeDescription(mqttConfigTypeDescription);
      constructor.addTypeDescription(mqttTopicConfigTypeDescription);
      constructor.addTypeDescription(serialPortConfigTypeDescription);

      Yaml yaml = new Yaml(constructor);
      InputStream inputStream = Files.newInputStream(Paths.get(configFileName));

      PMAgentConfig pmAgentConfig = (PMAgentConfig)yaml.load(inputStream);
      return pmAgentConfig;
   }
}
