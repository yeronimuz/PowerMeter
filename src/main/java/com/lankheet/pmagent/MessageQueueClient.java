/**
 * 
 */
package com.lankheet.pmagent;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.utils.JsonUtil;

/**
 * Wrapper class for Mqtt message queue client
 *
 */
public class MessageQueueClient implements AutoCloseable {
	private MqttClient client;

	
	public MessageQueueClient(String url) throws MqttException {
		client = new MqttClient(url, MqttClient.generateClientId());
	}
	
	public MqttClient getClient() {
		return client;
	}
	
	public void connect() throws MqttSecurityException, MqttException {
		client.connect();
		
	}
	
	public void publish(Measurement measurement) throws MqttPersistenceException, MqttException {
		MqttMessage message = new MqttMessage();
		message.setPayload(JsonUtil.toJson(measurement).getBytes());
		client.publish("test", message);
	}
	
	public void disconnect() throws Exception {
		close();
	}

	@Override
	public void close() throws Exception {
		client.disconnect();
	}

}
