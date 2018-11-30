package com.lankheet.pmagent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

public class SensorValueSenderTest {

    private @Mocked MqttClient mqttClientMock;
    private @Mocked LoggerFactory LoggerFactoryMock;
    private @Capturing Logger loggerMock;

    private static List<MqttTopicConfig> topics = new ArrayList<>();

    @BeforeClass
    public static void doSetup() {
        MqttTopicConfig configA = new MqttTopicConfig();
        configA.setTopic("lnb/eng/test");
        configA.setType(TopicType.POWER);
        MqttTopicConfig configB = new MqttTopicConfig();
        configB.setTopic("lnb/eng/gas");
        configB.setType(TopicType.GAS);
        topics.add(configA);
        topics.add(configB);
    }

    @Test
    public void testSendMessage() throws MqttException {
        new Expectations() {{
            LoggerFactory.getLogger(SensorValueSender.class);
            result = loggerMock;
        }};
        SensorValueSender sensorValueSender = new SensorValueSender(mqttClientMock, topics);
        sensorValueSender.newSensorValue(new SensorValue(new SensorNode("01:02:03:04", SensorType.POWER_METER.getId()),
                new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 3.5));

        new Verifications() {{
            loggerMock.error(anyString);
            times = 0;

            mqttClientMock.publish(anyString, (MqttMessage) any);
            times = 1;
        }};
    }

    @Test
    public void testRepeatedValues() throws MqttException {
        SensorNode sensorNode = new SensorNode("01:02:03:04:05:06", 1);
        SensorNode anotherNode = new SensorNode("02:03:04:05:06:07", 2);
        SensorValueSender sensorValueSender = new SensorValueSender(mqttClientMock, topics);

        sensorValueSender.newSensorValue(new SensorValue(sensorNode, new Date(), 1, 3.0 ));
        sensorValueSender.newSensorValue(new SensorValue(sensorNode, new Date(), 1, 3.0 ));
        sensorValueSender.newSensorValue(new SensorValue(sensorNode, new Date(), 1, 3.5 ));
        sensorValueSender.newSensorValue(new SensorValue(anotherNode, new Date(), 2, 3.0));

        new Verifications() {{
            mqttClientMock.publish(anyString, (MqttMessage) any);
            times = 3;
        }};
    }
}
