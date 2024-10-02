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

package org.domiot.p1.pmagent.p1;

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
        DeviceDto deviceDto = DeviceDto.builder()
                .macAddress("AA:BB:CC:DD:EE:FF").build();
        deviceDto.addSensor(SensorDto.builder().sensorId(1L).sensorType(SensorTypeDto.POWER_CT1).build())
                .addSensor(SensorDto.builder().sensorId(2L).sensorType(SensorTypeDto.POWER_CT2).build())
                .addSensor(SensorDto.builder().sensorId(3L).sensorType(SensorTypeDto.POWER_PT1).build())
                .addSensor(SensorDto.builder().sensorId(4L).sensorType(SensorTypeDto.POWER_PT2).build())
                .addSensor(SensorDto.builder().sensorId(5L).sensorType(SensorTypeDto.POWER_AC).build())
                .addSensor(SensorDto.builder().sensorId(6L).sensorType(SensorTypeDto.POWER_AP).build())
                .addSensor(SensorDto.builder().sensorId(7L).sensorType(SensorTypeDto.GAS_METER).build());
        List<SensorValueDto> sensorValues;
        sensorValues = DatagramToSensorValueMapper.convertP1Datagram(deviceDto, p1Datagram);

        assertEquals(7, sensorValues.size());
        assertEquals(1, sensorValues.get(0).getSensorId());
        assertEquals(207.138, sensorValues.get(0).getValue());

        assertEquals(2, sensorValues.get(1).getSensorId());
        assertEquals(269.06, sensorValues.get(1).getValue());

        assertEquals(3, sensorValues.get(2).getSensorId());
        assertEquals(27.545, sensorValues.get(2).getValue());

        assertEquals(4, sensorValues.get(3).getSensorId());
        assertEquals(74.828, sensorValues.get(3).getValue());

        assertEquals(5, sensorValues.get(4).getSensorId());
        assertEquals(0.984, sensorValues.get(4).getValue());

        assertEquals(6, sensorValues.get(5).getSensorId());
        assertEquals(0.0, sensorValues.get(5).getValue());

        assertEquals(7, sensorValues.get(6).getSensorId());
        assertEquals(86.298, sensorValues.get(6).getValue());
    }
}
