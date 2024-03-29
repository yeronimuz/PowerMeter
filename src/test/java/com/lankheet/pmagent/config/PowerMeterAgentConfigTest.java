package com.lankheet.pmagent.config;

import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.PowerMeterAgent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PowerMeterAgentConfigTest
{
   // Prepare the SUT
   private static class PMAgentConfigTester extends PowerMeterAgent
   {
      private static PMAgentConfigTester instance = null;
      private        PMAgentConfig       pmaConfig;


      private PMAgentConfigTester()
      {
         // Do not allow instantiation through constructor
      }


      public void run(String configFileName)
         throws IOException
      {
         this.pmaConfig = PMAgentConfig.loadConfigurationFromFile(configFileName);
      }


      public static PMAgentConfigTester getInstance()
      {
         if (instance == null)
         {
            instance = new PMAgentConfigTester();
         }
         return instance;
      }
   }


   @BeforeAll
   public static void setup()
      throws Exception
   {
      PMAgentConfigTester.getInstance().run("src/test/resources/application.yml");
   }


   @Test
   void testPmAgentParameters()
   {
      PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
      assertEquals(3600000L, pmaTester.pmaConfig.getRepeatValuesAfter());
      assertEquals(10000, pmaTester.pmaConfig.getInternalQueueSize());
   }


   @Test
   void testConfigSerial()
      throws Exception
   {
      PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
      SerialPortConfig serialConfig = pmaTester.pmaConfig.getSerialPortConfig();
      assertNotNull(serialConfig);
      assertEquals(115200, serialConfig.getBaudRate());
      assertEquals("/dev/ttyUSB0", serialConfig.getUart());
   }


   @Test
   void testConfigMqtt()
   {
      PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();

      MqttConfig mqttConfig = pmaTester.pmaConfig.getMqttConfig();
      assertNotNull(mqttConfig);
      assertEquals("PM_unique_client_name", mqttConfig.getClientName());
      assertEquals("johndoe", mqttConfig.getUserName());
      assertEquals(2, mqttConfig.getTopics().size());
      assertEquals("power", mqttConfig.getTopics().get(0).getType().toString().toLowerCase());
      assertEquals("lnb/eng/power", mqttConfig.getTopics().get(0).getTopic());
      assertEquals("gas", mqttConfig.getTopics().get(1).getType().toString().toLowerCase());
      assertEquals("lnb/eng/gas", mqttConfig.getTopics().get(1).getTopic());
   }


   @Test
   void testSensorConfig()
   {
      PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
      SensorConfig sensorConfig = pmaTester.pmaConfig.getSensorConfig();
      assertEquals("wlo1", sensorConfig.getNic());
      assertEquals(SensorType.POWER_METER, sensorConfig.getSensorTypes().get(0));
      assertEquals(SensorType.GAS_METER, sensorConfig.getSensorTypes().get(1));
   }
}
