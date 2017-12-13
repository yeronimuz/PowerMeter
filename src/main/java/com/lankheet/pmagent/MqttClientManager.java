/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.pmagent;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.pmagent.config.MqttConfig;
import io.dropwizard.lifecycle.Managed;

/**
 * MQTT client manager that manages the connection with the mqtt-broker.
 *
 */
public class MqttClientManager implements Managed {
	private static final Logger LOG = LoggerFactory.getLogger(MqttClientManager.class);

	private MqttClient client;
	private static final String MQTT_PM_CLIENT_ID = "PM_CS1F08";
	private final MqttConnectOptions options = new MqttConnectOptions();

	public MqttClientManager(MqttConfig mqttConfig) throws MqttException {
		String userName = mqttConfig.getUserName();
		String password = mqttConfig.getPassword();
		client = new MqttClient(mqttConfig.getUrl(), MQTT_PM_CLIENT_ID);

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
