package com.lankheet.pmagent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SensorValueCacheTest {

    @Test
    void testRepeatedValues() {
        SensorDto sensor = SensorDto.builder().sensorType(SensorTypeDto.GAS_METER).build();
        SensorDto sensor2 = SensorDto.builder().sensorType(SensorTypeDto.POWER_AC).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertTrue(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensor(sensor2)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
    }

    @Test
    void testDifferentSensors() {
        SensorDto sensor = SensorDto.builder().sensorType(SensorTypeDto.GAS_METER).build();
        SensorDto sensor2 = SensorDto.builder().sensorType(SensorTypeDto.POWER_AC).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensor(sensor2)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));

        assertEquals(3.0, svCache.getLatch().get(sensor).doubleValue());
        assertEquals(3.0, svCache.getLatch().get(sensor2).doubleValue());
    }

    @Test
    void testEqualSensor() {
        SensorDto sensor = SensorDto.builder().sensorType(SensorTypeDto.GAS_METER).build();
        SensorDto sensorClone = SensorDto.builder().sensorType(SensorTypeDto.GAS_METER).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertTrue(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensor(sensorClone)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(sensor).doubleValue());

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensor(sensor)
                .timeStamp(LocalDateTime.now())
                .value(2.5)
                .build()));
    }
}
