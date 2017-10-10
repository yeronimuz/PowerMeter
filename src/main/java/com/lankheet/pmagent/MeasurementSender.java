package com.lankheet.pmagent;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;

public class MeasurementSender implements MeasurementListener {
	private static final Logger LOG = LogManager.getLogger(MeasurementSender.class);

	private MqttClient mqttClient;

	private List<MqttTopicConfig> topics;

	public MeasurementSender(MqttClient mqttClient, List<MqttTopicConfig> topics) throws MqttException {
		this.mqttClient = mqttClient;
		this.topics = topics;
	}

	@Override
	public void newMeasurement(Measurement measurement) {
		System.out.println(measurement);
		String mqttTopic = null;
		TopicType topicType = getTopicTypeFromMeasurementType(measurement);
		// Get the destination
		for (MqttTopicConfig mtc : topics) {
			if (mtc.getType().equals(topicType.getTopicName())) {
				mqttTopic = mtc.getTopic();
			}
		}
		try {
			MqttMessage message = new MqttMessage();
			message.setPayload(JsonUtil.toJson(measurement).getBytes());

			mqttClient.publish(mqttTopic, message);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private TopicType getTopicTypeFromMeasurementType(Measurement measurement) {
		TopicType returnType = null;
		switch (measurement.getType()) {
		case PRODUCED_POWER_T1:
		case PRODUCED_POWER_T2:
		case CONSUMED_POWER_T1:
		case CONSUMED_POWER_T2:
		case ACTUAL_CONSUMED_POWER:
		case ACTUAL_PRODUCED_POWER:
			returnType = TopicType.POWER;
			break;
		case CONSUMED_GAS:
			returnType = TopicType.GAS;
			break;
		case HUMIDITY:
			returnType = TopicType.HUMIDITY;
			break;
		case TEMPERATURE:
			returnType = TopicType.TEMPERATURE;
			break;
		default:
			LOG.error("There is no mapping for measurementType:" + measurement.getType().toString());
			// return null
		}
		return returnType;
	}
}
