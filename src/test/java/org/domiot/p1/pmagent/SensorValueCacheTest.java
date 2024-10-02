package org.domiot.p1.pmagent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SensorValueCacheTest {

    @Test
    void testRepeatedValues() {
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertTrue(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertEquals(3.0, svCache.getLatch().get(1L));

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(1L));

        assertTrue(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
    }

    @Test
    void testDifferentSensors() {
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(2L)
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));

        assertEquals(3.0, svCache.getLatch().get(1L));
        assertEquals(3.0, svCache.getLatch().get(2L));
    }

    @Test
    void testEqualSensor() {
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertTrue(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(1L).doubleValue());

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(2.5)
                .build()));
    }
}
