package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.PMAgentConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorValueSenderTest
{
   @Mock
   private LoggerFactory LoggerFactoryMock;
   @Mock
   private MqttClient    mqttClientMock;

   @Mock
   private Logger loggerMock;
   @Captor
   private ArgumentCaptor<String> logMessage;

   private static PMAgentConfig config;


   @BeforeAll
   static void doSetup()
      throws IOException
   {
      config = PMAgentConfig.loadConfigurationFromFile("src/test/resources/application.yml");
   }


   @Test
   void testSendMessage()
      throws Exception
   {
      doNothing().when(mqttClientMock).publish(anyString(), any(MqttMessage.class));
      BlockingQueue<SensorValue> queue = new ArrayBlockingQueue(1000);
      SensorValueSender sensorValueSender = new SensorValueSender(queue, config.getMqttConfig(), 10000);
      setField(sensorValueSender, "mqttClient", mqttClientMock);

      sensorValueSender.newSensorValue(new SensorValue(new SensorNode("01:02:03:04", SensorType.POWER_METER.getId()),
                                                       new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 3.5));

      verify(mqttClientMock).publish(anyString(), any(MqttMessage.class));
   }

   private void setField(SensorValueSender sensorValueSender, String fieldName, Object object)
      throws IllegalAccessException
   {
      List<Field> fields = ReflectionUtils.findFields(SensorValueSender.class, f -> f.getName().equals(fieldName), TOP_DOWN);
      Field field = fields.get(0);
      field.setAccessible(true);
      field.set(sensorValueSender, object);
   }
}
