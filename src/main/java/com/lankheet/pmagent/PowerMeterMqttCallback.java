package com.lankheet.pmagent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerMeterMqttCallback implements MqttCallback
{
   private static final Logger LOG = LoggerFactory.getLogger(PowerMeterMqttCallback.class);

   private MqttClient mqttClient;


   public PowerMeterMqttCallback(MqttClient mqttClient)
   {
      this.mqttClient = mqttClient;
   }


   @Override
   public void connectionLost(Throwable cause)
   {
      // Probably, this method will only be called when no data is to be sent and a connection was
      // disturbed. Since we send data every 10 seconds, this call will probably not be made.
      LOG.error("Connection loss: {}", cause.getMessage());
      try
      {
         // TODO: How many times, and what delay?
         mqttClient.reconnect();
      }
      catch (MqttException e)
      {
         LOG.error("Reconnection Mqtt client failed: {}", e.getMessage());
      }
   }


   @Override
   public void messageArrived(String topic, MqttMessage message)
      throws Exception
   {
      LOG.info("Received message, while not expected: {}", message.toString());
      // TODO: Cmd message: Reset latch (send repeated values)
      // * retry parameters for mqtt reconnect.
   }


   @Override
   public void deliveryComplete(IMqttDeliveryToken token)
   {
      LOG.debug("Delivery complete: ", token.toString());
   }
}
