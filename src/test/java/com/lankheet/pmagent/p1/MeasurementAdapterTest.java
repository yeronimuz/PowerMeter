/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.pmagent.p1;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;
import com.lankheet.iot.datatypes.entities.SensorType;

/**
 * This test needs a local MQTT broker that is not secured
 */
public class MeasurementAdapterTest {

	@Test
	public void test() throws IOException, URISyntaxException {
		String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1_2.log").toURI())));
		System.out.println(input);

		P1Datagram dg = P1Parser.parse(input);
		List<SensorValue> sensorValues = SensorValueAdapter.convertP1Datagram(new SensorNode("01:02", SensorType.POWER_METER.getId()), dg);
		assertThat(sensorValues.size(), is(7));
		assertThat(sensorValues.get(0).getMeasurementType(), is(MeasurementType.CONSUMED_POWER_T1.getId()));
		assertThat(sensorValues.get(0).getValue(), is(207.138));
		assertThat(sensorValues.get(0).getSensorNode().getSensorMac(), is("01:02"));
		assertThat(sensorValues.get(1).getMeasurementType(), is(MeasurementType.PRODUCED_POWER_T1.getId()));
		assertThat(sensorValues.get(1).getValue(), is(269.06));
		assertThat(sensorValues.get(2).getMeasurementType(), is(MeasurementType.CONSUMED_POWER_T2.getId()));
		assertThat(sensorValues.get(2).getValue(), is(27.545));
		assertThat(sensorValues.get(3).getMeasurementType(), is(MeasurementType.PRODUCED_POWER_T2.getId()));
		assertThat(sensorValues.get(3).getValue(), is(74.828));
		assertThat(sensorValues.get(4).getMeasurementType(), is(MeasurementType.ACTUAL_CONSUMED_POWER.getId()));
		assertThat(sensorValues.get(4).getValue(), is(0.984));
		assertThat(sensorValues.get(5).getMeasurementType(), is(MeasurementType.ACTUAL_PRODUCED_POWER.getId()));
		assertThat(sensorValues.get(5).getValue(), is(0.0));
		assertThat(sensorValues.get(6).getMeasurementType(), is(MeasurementType.CONSUMED_GAS.getId()));
		assertThat(sensorValues.get(6).getValue(), is(86.298));
	}

}
