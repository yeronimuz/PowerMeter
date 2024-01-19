package com.lankheet.pmagent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.Sensor;
import org.lankheet.domiot.model.SensorType;
import org.lankheet.domiot.model.SensorValue;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SensorValueCacheTest {

//    @Mock
//    private LoggerFactory LoggerFactoryMock;
//    @Mock
//    private Logger loggerMock;

    @Test
    void testRepeatedValues() {
        Sensor sensor = new Sensor().type(SensorType.GAS_METER);
        Sensor sensor2 = new Sensor().type(SensorType.POWER_AC);
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(new SensorValue()
                .sensor(sensor)
                .timestamp(LocalDateTime.now())
                .value(3.0)));
        assertTrue(svCache.isRepeatedValue(new SensorValue()
                .sensor(sensor)
                .timestamp(LocalDateTime.now())
                .value(3.0)));
        assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue(new SensorValue()
                .sensor(sensor)
                .timestamp(LocalDateTime.now())
                .value(3.5)));
        assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue(new SensorValue()
                .sensor(sensor2)
                .timestamp(LocalDateTime.now())
                .value(3.5)));
     }

     @Test
     void testDifferentSensors() {
         Sensor sensor = new Sensor().type(SensorType.GAS_METER);
         Sensor sensor2 = new Sensor().type(SensorType.POWER_AC);
         SensorValueCache svCache = new SensorValueCache();

         assertFalse(svCache.isRepeatedValue(new SensorValue()
                 .sensor(sensor)
                 .timestamp(LocalDateTime.now())
                 .value(3.0)));
         assertFalse(svCache.isRepeatedValue(new SensorValue()
                 .sensor(sensor2)
                 .timestamp(LocalDateTime.now())
                 .value(3.0)));

         assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());
         assertEquals(3.0, svCache.getLatch().get(sensor2).doubleValue());

     }
     @Test
    void testEqualSensor() {
         Sensor sensor = new Sensor().type(SensorType.GAS_METER);
         Sensor sensorClone = new Sensor().type(SensorType.GAS_METER);
         SensorValueCache svCache = new SensorValueCache();

         assertFalse(svCache.isRepeatedValue(new SensorValue()
                 .sensor(sensor)
                 .timestamp(LocalDateTime.now())
                 .value(3.5)));
         assertTrue(svCache.isRepeatedValue(new SensorValue()
                 .sensor(sensorClone)
                 .timestamp(LocalDateTime.now())
                 .value(3.5)));
         assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

         assertFalse(svCache.isRepeatedValue(new SensorValue()
                 .sensor(sensor)
                 .timestamp(LocalDateTime.now())
                 .value(2.5)));
     }
}
