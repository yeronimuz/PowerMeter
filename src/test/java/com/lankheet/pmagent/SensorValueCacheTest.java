package com.lankheet.pmagent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.lankheet.domiot.model.Device;
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
        SensorDto sensor = new SensorDto().sensorType(SensorTypeDto.GAS_METER);
        SensorDto sensor2 = new SensorDto().sensorType(SensorTypeDto.POWER_AC);
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.0)));
        assertTrue(svCache.isRepeatedValue(new SensorValueDto()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.0)));
        assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.5)));
        assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                .sensor(sensor2)
                .timeStamp(LocalDateTime.now())
                .value(3.5)));
     }

     @Test
     void testDifferentSensors() {
         SensorDto sensor = new SensorDto().sensorType(SensorTypeDto.GAS_METER);
         SensorDto sensor2 = new SensorDto().sensorType(SensorTypeDto.POWER_AC);
         SensorValueCache svCache = new SensorValueCache();

         assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                 .sensor(sensor)
                 .timeStamp(LocalDateTime.now())
                 .value(3.0)));
         assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                 .sensor(sensor2)
                 .timeStamp(LocalDateTime.now())
                 .value(3.0)));

         assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());
         assertEquals(3.0, svCache.getLatch().get(sensor2).doubleValue());

     }
     @Test
    void testEqualSensor() {
         SensorDto sensor = new SensorDto().sensorType(SensorTypeDto.GAS_METER);
         SensorDto sensorClone = new SensorDto().sensorType(SensorTypeDto.GAS_METER);
         SensorValueCache svCache = new SensorValueCache();

         assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                 .sensor(sensor)
                 .timeStamp(LocalDateTime.now())
                 .value(3.5)));
         assertTrue(svCache.isRepeatedValue(new SensorValueDto()
                 .sensor(sensorClone)
                 .timeStamp(LocalDateTime.now())
                 .value(3.5)));
         assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

         assertFalse(svCache.isRepeatedValue(new SensorValueDto()
                 .sensor(sensor)
                 .timeStamp(LocalDateTime.now())
                 .value(2.5)));
     }
}
