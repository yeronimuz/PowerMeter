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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SensorValueSenderTest {

    private @Mocked MqttClient mqttClientMock;
    private @Mocked LoggerFactory LoggerFactoryMock;
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
    }

    @Test
    public void test() throws MqttException {
        new Expectations() {
            {
                LoggerFactory.getLogger(SensorValueSender.class);
                result = loggerMock;
            }
        };
        SensorValueSender sensorValueSender = new SensorValueSender(mqttClientMock, topics);
        sensorValueSender.newSensorValue(new SensorValue(new SensorNode("01:02:03:04", SensorType.POWER_METER.getId()),
                new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 3.5));

        new Verifications() {
            {
                loggerMock.error(anyString);
                times = 0;
            }
        };
    }
}