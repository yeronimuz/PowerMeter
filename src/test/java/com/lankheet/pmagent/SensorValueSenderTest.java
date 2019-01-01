package com.lankheet.pmagent;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.PMAgentConfig;
import mockit.Capturing;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

public class SensorValueSenderTest {

    @Mocked
    private  LoggerFactory LoggerFactoryMock;
    @Mocked 
    private MqttClient mqttClientMock;
    @Capturing 
    private Logger loggerMock;

    private static PMAgentConfig config; 

    @BeforeClass
    public static void doSetup() throws IOException {
        config = PMAgentConfig.loadConfigurationFromFile("src/test/resources/application.yml");
     }

    @Test
    public void testSendMessage() throws Exception {
       new Expectations() {{
            LoggerFactory.getLogger(SensorValueSender.class);
            result = loggerMock;
            
            mqttClientMock.publish(anyString, (MqttMessage) any);
        }};
        BlockingQueue<SensorValue> queue = new ArrayBlockingQueue(1000);
        SensorValueSender sensorValueSender = new SensorValueSender(queue, config.getMqttConfig());
        Deencapsulation.setField(sensorValueSender, "mqttClient", mqttClientMock);
        
        sensorValueSender.newSensorValue(new SensorValue(new SensorNode("01:02:03:04", SensorType.POWER_METER.getId()),
                new Date(), MeasurementType.ACTUAL_CONSUMED_POWER.getId(), 3.5));

        new Verifications() {{
            loggerMock.error(anyString);
            times = 0;

            mqttClientMock.publish(anyString, (MqttMessage) any);
            times = 1;
        }};
    }
}
