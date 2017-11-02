package com.lankheet.pmagent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;

import mockit.Capturing;
import mockit.Expectations;
import mockit.MockUp;
import mockit.Mocked;

public class MeasurementSenderTest {

	private @Mocked MqttClient mqttClientMock;
	private @Mocked LogManager logManagerMock;
	private @Capturing Logger loggerMock;

	private static List<MqttTopicConfig> topics = new ArrayList<MqttTopicConfig>();
	
	@BeforeClass
	public static void doSetup() {
		MqttTopicConfig configA = new MqttTopicConfig();
		configA.setTopic("lnb/eng/test");
		configA.setType(TopicType.POWER);
		MqttTopicConfig configB = new MqttTopicConfig();
		configB.setTopic("lng/eng/gas");
		configB.setType(TopicType.GAS);
		topics.add(configA);
		topics.add(configB);
		
		new MockUp<MqttClient>() {
			void publish(String topic, MqttMessage msg) {
				System.out.println("publising...");
			}
		};
	}
	
	@Test
	public void test() throws MqttException {
		new Expectations() {
			{
				LogManager.getLogger(MeasurementSender.class);
				result = loggerMock;
			}
		};
		MeasurementSender measSender = new MeasurementSender(mqttClientMock, topics);
		measSender.newMeasurement(new Measurement(0, LocalDateTime.now(), MeasurementType.ACTUAL_CONSUMED_POWER, 3.5));

//		new Verifications() {
//			{
//				loggerMock.error(any);
//				times = 0;
//			}
//		};
	}

}
