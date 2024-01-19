package com.lankheet.pmagent;

import com.lankheet.pmagent.config.PMAgentConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.lankheet.domiot.model.MqttTopic;
import org.lankheet.domiot.model.Sensor;
import org.lankheet.domiot.model.SensorType;
import org.lankheet.domiot.model.SensorValue;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

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
      SensorValueSender sensorValueSender = new SensorValueSender(queue, config.getMqttBroker());
      setField(sensorValueSender, "mqttClient", mqttClientMock);

      sensorValueSender.newSensorValue(new SensorValue()
              .sensor(new Sensor()
                      .type(SensorType.POWER_AC)
                      .mqttTopic(new MqttTopic().path("/path")))
              .timestamp(LocalDateTime.now())
              .value( 3.5));

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
