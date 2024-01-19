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
   private static PMAgentConfig pmaConfig;

   @BeforeAll
   static void setup() throws IOException {
      pmaConfig = PMAgentConfig.loadConfigurationFromFile("src/test/resources/application.yml");
   }

   @Test
   void testPmAgentParameters() throws IOException {
      assertEquals(3600000L, pmaConfig.getRepeatValuesAfter());
      assertEquals(10000, pmaConfig.getInternalQueueSize());
   }


   @Test
   void testConfigSerial()
   {
      SerialPortConfig serialConfig = pmaConfig.getSerialPort();
      assertNotNull(serialConfig);
      assertEquals(115200, serialConfig.getBaudRate());
      assertEquals("/dev/ttyUSB0", serialConfig.getUart());
   }


   @Test
   void testConfigMqtt()
   {
      MqttConfig mqttConfig = pmaConfig.getMqttBroker();
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
      List<SensorConfig> sensorConfigs = pmaConfig.getSensorConfigs();
      assertEquals(7, sensorConfigs.size());
      assertEquals(SensorType.POWER_PT1, sensorConfigs.get(0).getType());
      assertEquals(SensorType.POWER_PT2, sensorConfigs.get(1).getType());
   }
}
