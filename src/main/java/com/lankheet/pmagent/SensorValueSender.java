package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * Worker thread for sending SensorValue objects to an MQTT broker.<BR>
 * <ul><li>It reads SensorValues from a Queue.
 * <li>It sends the values to an MQTT broker.
 * </ul>
 */
public class SensorValueSender implements Runnable
{
   private static final Logger LOG = LoggerFactory.getLogger(SensorValueSender.class);

   private final BlockingQueue<SensorValue> queue;

   private MqttClient mqttClient;

   private final MqttConfig mqttConfig;

   private final SensorValueCache sensorValueCache = new SensorValueCache();


   /**
    * Constructor.
    *
    * @param queue      The blocking queue that is filled with sensor values.
    * @param mqttConfig The mqtt configuration.
    */
   public SensorValueSender(BlockingQueue<SensorValue> queue, MqttConfig mqttConfig)
   {
      this.queue = queue;
      this.mqttConfig = mqttConfig;
   }


   @Override
   public void run()
   {
      try
      {
         connectToBroker(mqttConfig);
      }
      catch (MqttException e)
      {
         LOG.error("Cannot connect: {}", e.getMessage());
         // TODO: Make recoverable
         System.exit(-1);
      }

      while (true)
      {
         try
         {
            newSensorValue(queue.take());
         }
         catch (InterruptedException e)
         {
            LOG.error("Reading queue was interrupted");
         }
      }

      // TODO put next lines in cleanup
      // mqttClient.disconnect();
      // mqttClient.close();
   }


   private void connectToBroker(MqttConfig mqttConfig)
      throws MqttException
   {
      LOG.info("Mqtt broker connection starting: {}", mqttConfig);
      String userName = mqttConfig.getUserName();
      String password = mqttConfig.getPassword();
      mqttClient = new MqttClient(mqttConfig.getUrl(), mqttConfig.getClientName());

      MqttConnectOptions options = new MqttConnectOptions();
      mqttClient.setCallback(new PowerMeterMqttCallback(mqttClient));
      options.setConnectionTimeout(60);
      options.setKeepAliveInterval(60);
      options.setUserName(userName);
      options.setPassword(password.toCharArray());
      LOG.debug("Connecting to {}", mqttConfig.getUrl());
      // TODO: Connect in a loop. Send notifications when broker is down for 5? minutes
      mqttClient.connect(options);
   }


   public void newSensorValue(SensorValue sensorValue)
   {
      if (!sensorValueCache.isRepeatedValue(sensorValue))
      {
         LOG.debug("new value: {}", sensorValue);
         String mqttTopic = null;
         TopicType topicType = getTopicTypeFromSensorValueType(sensorValue);
         // Get the destination
         Optional<MqttTopicConfig> mqttTopicConfigOptional = mqttConfig.getTopics().stream()
            .filter((MqttTopicConfig topicConfig) ->
                       topicConfig.getType().getTopicName().equals(topicType.getTopicName())).findFirst();
         mqttTopic = mqttTopicConfigOptional.map(MqttTopicConfig::getTopic).orElse(null);

         boolean isConnectionOk = true;
         MqttMessage message = new MqttMessage();
         message.setPayload(JsonUtil.toJson(sensorValue).getBytes());
         LOG.debug("Sending Topic: {}, Msg: {}", mqttTopic, message);
         do
         {
            try
            {
               mqttClient.publish(mqttTopic, message);
               isConnectionOk = true;
            }
            catch (Exception e)
            {
               LOG.error(e.getMessage());
               isConnectionOk = false;
               try
               {
                  mqttClient.reconnect();
                  Thread.sleep(500);
               }
               catch (MqttException | InterruptedException e1)
               {
                  // NOP
               }
            }
         }
         while (!isConnectionOk);
      }
   }


   private TopicType getTopicTypeFromSensorValueType(SensorValue sensorValue)
   {
      TopicType returnType = null;
      // FIXME: Gas measurement is mapped to lnb/eng/power instead of lnb/eng/gas
      switch (SensorType.getType(sensorValue.getSensorNode().getSensorType()))
      {
         case POWER_METER:
            returnType = TopicType.POWER;
            break;
         case GAS_METER:
            returnType = TopicType.GAS;
            break;
         case HUMIDITY:
            returnType = TopicType.HUMIDITY;
            break;
         case TEMPERATURE:
            returnType = TopicType.TEMPERATURE;
            break;
         default:
            LOG.error("There is no mapping for measurementType: {}", sensorValue.getSensorNode().getSensorType());
      }
      return returnType;
   }
}
