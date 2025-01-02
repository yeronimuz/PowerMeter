package org.domiot.p1.pmagent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.domiot.p1.sensor.SensorValueCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorValueCacheTest {

    @Test
    void testRepeatedValues() {
        SensorDto sensor = SensorDto.builder().sensorId(1L).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertTrue(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertEquals(3.0, svCache.getLatch().get(sensor.getSensorId()).doubleValue());

        assertFalse(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(sensor.getSensorId()).doubleValue());

        assertTrue(svCache.isRepeatedValue( SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
    }

    @Test
    void testDifferentSensors() {
        SensorDto sensor = SensorDto.builder().sensorId(1L).build();
        SensorDto sensor2 = SensorDto.builder().sensorId(2L).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));
        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(sensor2.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.0)
                .build()));

        assertEquals(3.0, svCache.getLatch().get(sensor.getSensorId()).doubleValue());
        assertEquals(3.0, svCache.getLatch().get(sensor2.getSensorId()).doubleValue());
    }

    @Test
    void testEqualSensor() {
        SensorDto sensor = SensorDto.builder().sensorId(1L).build();
        SensorDto sensorClone = SensorDto.builder().sensorId(1L).build();
        SensorValueCache svCache = new SensorValueCache();

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertTrue(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(sensorClone.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build()));
        assertEquals(3.5, svCache.getLatch().get(sensor.getSensorId()).doubleValue());

        assertFalse(svCache.isRepeatedValue(SensorValueDto.builder()
                .sensorId(sensor.getSensorId())
                .timeStamp(LocalDateTime.now())
                .value(2.5)
                .build()));
    }
}
