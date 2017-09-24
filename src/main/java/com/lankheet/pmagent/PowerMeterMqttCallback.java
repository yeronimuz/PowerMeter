package com.lankheet.pmagent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PowerMeterMqttCallback implements MqttCallback {
	private static final Logger LOG = LogManager.getLogger(PowerMeterMqttCallback.class);
	
	private MqttClientManager mqttClientManager;
	
	public PowerMeterMqttCallback(MqttClientManager mqttClientManager) {
		this.mqttClientManager = mqttClientManager;
	}

	@Override
	public void connectionLost(Throwable cause) {
		LOG.error("Connection loss: {}", cause.getMessage());
		try {
			mqttClientManager.getClient().reconnect();
		} catch (MqttException e) {
			LOG.fatal("Reconnection Mqtt client failed: {}", e.getMessage());
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOG.info("Received message, while not expected: {}", message.toString());

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		LOG.info("Delivery complete: ", token.toString());
	}

}
