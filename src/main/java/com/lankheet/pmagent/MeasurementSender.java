package com.lankheet.pmagent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.utils.JsonUtil;

public class MeasurementSender implements MeasurementListener {
	private static final Logger LOG = LogManager.getLogger(MeasurementSender.class);

	private MqttClient mqttClient;
	
	private String topic;

	public MeasurementSender(MqttClient mqttClient, String string) throws MqttException {
		this.mqttClient = mqttClient;
	}

	@Override
	public void newMeasurement(Measurement measurement) {
		System.out.println(measurement);
		try {
			MqttMessage message = new MqttMessage();
			message.setPayload(JsonUtil.toJson(measurement).getBytes());

			mqttClient.publish(topic, message);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
}
