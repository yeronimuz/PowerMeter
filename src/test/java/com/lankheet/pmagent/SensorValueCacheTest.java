package com.lankheet.pmagent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import mockit.Capturing;
import mockit.Mocked;


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

        assertThat(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.0)), is(false));
        assertThat(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.0)), is(true));
        assertThat(svCache.getLatch().get(sensorNode).size(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getMeasurementType(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getValue(), is(3.0));

        assertThat(svCache.isRepeatedValue(new SensorValue(sensorNode, new Date(), 1, 3.5)), is(false));
        assertThat(svCache.isRepeatedValue(new SensorValue(anotherNode, new Date(), 2, 3.5)), is(false));
        assertThat(svCache.getLatch().get(sensorNode).size(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getMeasurementType(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getValue(), is(3.5));
        assertThat(svCache.getLatch().get(anotherNode).size(), is(1));
        assertThat(svCache.getLatch().get(anotherNode).get(0).getMeasurementType(), is(2));
        assertThat(svCache.getLatch().get(anotherNode).get(0).getValue(), is(3.5));
        

        assertThat(svCache.getLatch().get(sensorNode).size(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getMeasurementType(), is(1));
        assertThat(svCache.getLatch().get(sensorNode).get(0).getValue(), is(3.5));

        svCache.isRepeatedValue(new SensorValue(anotherNode, new Date(), 3, 3.0));
        assertThat(svCache.getLatch().get(anotherNode).size(), is(2));
        assertThat(svCache.getLatch().get(anotherNode).get(0).getMeasurementType(), is(2));
        assertThat(svCache.getLatch().get(anotherNode).get(0).getValue(), is(3.5));
        assertThat(svCache.getLatch().get(anotherNode).get(1).getMeasurementType(), is(3));
        assertThat(svCache.getLatch().get(anotherNode).get(1).getValue(), is(3.0));
     }
}
