package com.lankheet.pmagent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.lankheet.pmagent.config.MqttConfig;

import io.dropwizard.lifecycle.Managed;

/**
 * MQTT client manager that manages the connection with the mqtt-broker.
 *
 */
public class MqttClientManager implements Managed {
	private static final Logger LOG = LogManager.getLogger(MqttClientManager.class);

	private MqttClient client;
	private static final String MQTT_PM_CLIENT_ID = "PM_CS1F08";
	private final MqttConnectOptions options = new MqttConnectOptions();

	public MqttClientManager(MqttConfig mqttConfig) throws MqttException {
		String userName = mqttConfig.getUserName();
		String password = mqttConfig.getPassword();
		client = new MqttClient(mqttConfig.getUrl(), MqttClient.generateClientId());

		client.setCallback(new PowerMeterMqttCallback(this));
		MqttConnectOptions options = new MqttConnectOptions();
		options.setConnectionTimeout(60);
		options.setKeepAliveInterval(60);
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
	}

	@Override
	public void start() throws Exception {
		LOG.info("ClientId: {}", client.getClientId());
		LOG.info("Connecting mqtt broker with options: {}", options);
		client.connect(options);
	}

	@Override
	public void stop() throws Exception {
		LOG.info("Closing mqtt connection...");
		client.disconnect();
		client.close();
	}
	
	/**
	 * Expose the client in order to provide reconnect possibilities
	 * @return The MqttClient
	 */
	public MqttClient getClient() {
		return client;
	}
}
