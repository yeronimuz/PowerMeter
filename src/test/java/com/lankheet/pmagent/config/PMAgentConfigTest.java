package com.lankheet.pmagent.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.model.SensorType;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PMAgentConfigTest
{
   private static DeviceConfig deviceConfig;

   @BeforeAll
   static void setup() throws IOException {
      deviceConfig = PMAgentConfig.loadConfigurationFromFile("src/test/resources/application.yml");
   }

   @Test
   void testPmAgentParameters() throws IOException {
      assertEquals(3600000L, deviceConfig.getRepeatValuesAfter());
      assertEquals(10000, deviceConfig.getInternalQueueSize());
   }


   @Test
   void testConfigSerial()
   {
      SerialPortConfig serialConfig = deviceConfig.getSerialPort();
      assertNotNull(serialConfig);
      assertEquals(115200, serialConfig.getBaudRate());
      assertEquals("/dev/ttyUSB0", serialConfig.getUart());
   }


   @Test
   void testConfigMqtt()
   {
      MqttConfig mqttConfig = deviceConfig.getMqttBroker();
      assertNotNull(mqttConfig);
      assertEquals("PM_unique_client_name", mqttConfig.getClientName());
      assertEquals("johndoe", mqttConfig.getUserName());
      assertEquals(2, mqttConfig.getSubscriptions().size());
      assertEquals("p1", mqttConfig.getSubscriptions().get(0).toLowerCase());
     assertEquals("domiot", mqttConfig.getSubscriptions().get(1).toLowerCase());
   }


   @Test
   void testSensorConfig()
   {
      List<SensorConfig> sensorConfigs = deviceConfig.getSensorConfigs();
      assertEquals(7, sensorConfigs.size());

      SensorConfig sensorConfig = getSensorConfig(sensorConfigs, SensorType.POWER_PT1);
      assertNotNull(sensorConfig);
      assertEquals(SensorType.POWER_PT1, sensorConfig.getSensorType());
      assertEquals("/pb13/p1/power/pt1", sensorConfig.getMqttTopicConfig().getTopic());
      assertEquals("power", sensorConfig.getMqttTopicConfig().getTopicType());
      assertEquals("pb13:p1:pt1", sensorConfig.getName());
      assertEquals("Produced power T1", sensorConfig.getDescription());

      sensorConfig = getSensorConfig(sensorConfigs, SensorType.POWER_PT2);
      assertNotNull(sensorConfig);
      assertEquals(SensorType.POWER_PT2, sensorConfig.getSensorType());
      assertEquals("/pb13/p1/power/pt2", sensorConfig.getMqttTopicConfig().getTopic());
      assertEquals("pb13:p1:pt2", sensorConfig.getName());
      assertEquals("Produced power T2", sensorConfig.getDescription());

      sensorConfig = getSensorConfig(sensorConfigs, SensorType.POWER_CT1);
      assertNotNull(sensorConfig);
      assertEquals(SensorType.POWER_CT1, sensorConfig.getSensorType());
      assertEquals("/pb13/p1/power/ct1", sensorConfig.getMqttTopicConfig().getTopic());
      assertEquals("pb13:p1:ct1", sensorConfig.getName());
      assertEquals("Consumed power T1", sensorConfig.getDescription());

      sensorConfig = getSensorConfig(sensorConfigs, SensorType.POWER_AC);
      assertNotNull(sensorConfig);
      assertEquals(SensorType.POWER_AC, sensorConfig.getSensorType());
      assertEquals("/pb13/p1/power/ac", sensorConfig.getMqttTopicConfig().getTopic());
      assertEquals("pb13:p1:ac", sensorConfig.getName());
      assertEquals("Accumulated consumed power", sensorConfig.getDescription());

      sensorConfig = getSensorConfig(sensorConfigs, SensorType.POWER_AP);
      assertNotNull(sensorConfig);
      assertEquals(SensorType.POWER_AP, sensorConfig.getSensorType());
      assertEquals("/pb13/p1/power/ap", sensorConfig.getMqttTopicConfig().getTopic());
      assertEquals("pb13:p1:ap", sensorConfig.getName());
      assertEquals("Accumulated produced power", sensorConfig.getDescription());
   }

   private SensorConfig getSensorConfig(List<SensorConfig> sensorConfigs, SensorType type) {
      return sensorConfigs.stream()
              .filter(sensorConfig -> sensorConfig.getSensorType().equals(type))
              .findFirst()
              .orElse(null);
   }
}
