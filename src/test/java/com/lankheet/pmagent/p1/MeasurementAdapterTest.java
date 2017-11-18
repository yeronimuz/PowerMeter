package com.lankheet.pmagent.p1;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.utils.JsonUtil;

/**
 * This test needs a local MQTT broker that is not secured
 */
public class MeasurementAdapterTest {

	@Test
	public void test() throws IOException, URISyntaxException {
		String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1_2.log").toURI())));
		System.out.println(input);

		P1Datagram dg = P1Parser.parse(input);
		List<Measurement> measurements = MeasurementAdapter.convertP1Datagram(1, dg);
		assertThat(measurements.size(), is(7));
		assertThat(measurements.get(0).getType(), is(MeasurementType.CONSUMED_POWER_T1.getId()));
		assertThat(measurements.get(0).getValue(), is(207.138));
		assertThat(measurements.get(0).getSensorId(), is(1));
		assertThat(measurements.get(1).getType(), is(MeasurementType.PRODUCED_POWER_T1.getId()));
		assertThat(measurements.get(1).getValue(), is(269.06));
		assertThat(measurements.get(2).getType(), is(MeasurementType.CONSUMED_POWER_T2.getId()));
		assertThat(measurements.get(2).getValue(), is(27.545));
		assertThat(measurements.get(3).getType(), is(MeasurementType.PRODUCED_POWER_T2.getId()));
		assertThat(measurements.get(3).getValue(), is(74.828));
		assertThat(measurements.get(4).getType(), is(MeasurementType.ACTUAL_CONSUMED_POWER.getId()));
		assertThat(measurements.get(4).getValue(), is(0.984));
		assertThat(measurements.get(5).getType(), is(MeasurementType.ACTUAL_PRODUCED_POWER.getId()));
		assertThat(measurements.get(5).getValue(), is(0.0));
		assertThat(measurements.get(6).getType(), is(MeasurementType.CONSUMED_GAS.getId()));
		assertThat(measurements.get(6).getValue(), is(86.298));
	}

}
