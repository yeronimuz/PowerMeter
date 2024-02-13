/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.pmagent.p1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link DatagramToSensorValueMapper}
 */
@ExtendWith(MockitoExtension.class)
class DatagramToSensorValueMapperTest {
    @Test
    void test()
            throws IOException, URISyntaxException {
        String input = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("/p1_2.log")).toURI())));
        System.out.println(input);

        P1Datagram p1Datagram = P1Parser.parse(input);
        List<SensorValueDto> sensorValues = DatagramToSensorValueMapper.convertP1Datagram(
                new DeviceDto()
                        .addSensor(
                                new SensorDto()
                                        .sensorType(SensorTypeDto.POWER_CT1))
                        .addSensor(
                                new SensorDto()
                                        .sensorType(SensorTypeDto.POWER_AC))
                        .addSensor(
                                new SensorDto()
                                        .sensorType(SensorTypeDto.GAS_METER))
                        .macAddress("AA:BB:CC:DD:EE:FF"), p1Datagram);
        assertEquals(3, sensorValues.size());
        assertEquals(SensorTypeDto.POWER_CT1, sensorValues.get(0).sensor().sensorType());
        assertEquals(207.138, sensorValues.get(0).value());

//        assertEquals(MeasurementType.PRODUCED_POWER_T1.getId(), sensorValues.get(1).getMeasurementType());
//        assertEquals(27.545, sensorValues.get(1).getValue());
//
//        assertEquals(MeasurementType.CONSUMED_POWER_T2.getId(), sensorValues.get(2).getMeasurementType());
//        assertEquals(269.06, sensorValues.get(2).getValue());
//
//        assertEquals(MeasurementType.PRODUCED_POWER_T2.getId(), sensorValues.get(3).getMeasurementType());
//        assertEquals(74.828, sensorValues.get(3).getValue());
//
//        assertEquals(MeasurementType.ACTUAL_CONSUMED_POWER.getId(), sensorValues.get(4).getMeasurementType());
//        assertEquals(0.984, sensorValues.get(4).getValue());
//
//        assertEquals(MeasurementType.ACTUAL_PRODUCED_POWER.getId(), sensorValues.get(5).getMeasurementType());
//        assertEquals(0.0, sensorValues.get(5).getValue());
//
//        assertEquals(MeasurementType.CONSUMED_GAS.getId(), sensorValues.get(6).getMeasurementType());
//        assertEquals(86.298, sensorValues.get(6).getValue());
    }
}
