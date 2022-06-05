package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import mockit.Capturing;
import mockit.Mocked;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitPlatform.class)
public class SensorValueCacheTest {

    @Mocked
    private LoggerFactory LoggerFactoryMock;
    @Capturing
    private Logger loggerMock;

    @Test
    public void testRepeatedValues() throws MqttException {
        BlockingQueue<SensorValue> queue = new ArrayBlockingQueue(1000);
        SensorNode sensorNode = new SensorNode("01:02:03:04:05:06", 1);
        SensorNode anotherNode = new SensorNode("02:03:04:05:06:07", 1);
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.0)));
        assertTrue(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.0)));
        assertEquals(1, svCache.getLatch().get(sensorNode).size());
        assertEquals(1, svCache.getLatch().get(sensorNode).get(0).getMeasurementType());
        assertEquals(3.0, svCache.getLatch().get(sensorNode).get(0).getValue());

        assertFalse(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.5)));
        assertFalse(svCache.isRepeatedValue(new SensorValue(anotherNode, new Date(), 2, 3.5)));
        assertEquals(1, svCache.getLatch().get(sensorNode).size());
        assertEquals(1, svCache.getLatch().get(sensorNode).get(0).getMeasurementType());
        assertEquals(3.5, svCache.getLatch().get(sensorNode).get(0).getValue());
        assertEquals(1, svCache.getLatch().get(anotherNode).size());
        assertEquals(2, svCache.getLatch().get(anotherNode).get(0).getMeasurementType());
        assertEquals(3.5, svCache.getLatch().get(anotherNode).get(0).getValue());
        

        assertEquals(1, svCache.getLatch().get(sensorNode).size());
        assertEquals(1, svCache.getLatch().get(sensorNode).get(0).getMeasurementType());
        assertEquals(3.5, svCache.getLatch().get(sensorNode).get(0).getValue());

        svCache.isRepeatedValue(new SensorValue(anotherNode, new Date(), 3, 3.0));
        assertEquals(2, svCache.getLatch().get(anotherNode).size());
        assertEquals(2, svCache.getLatch().get(anotherNode).get(0).getMeasurementType());
        assertEquals(3.5, svCache.getLatch().get(anotherNode).get(0).getValue());
        assertEquals(3, svCache.getLatch().get(anotherNode).get(1).getMeasurementType());
        assertEquals(3.0, svCache.getLatch().get(anotherNode).get(1).getValue());
     }
}
