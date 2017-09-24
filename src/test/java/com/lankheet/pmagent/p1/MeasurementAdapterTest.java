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
		List<Measurement> measurements = MeasurementAdapter.convertP1Datagram(dg);
		assertThat(measurements.size(), is(7));
		measurements.forEach(measurement -> {
			if (measurement.getValue() != 0.0) {

				System.out.println(measurement);
				try {
					MqttClient  mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
					MqttConnectOptions options = new MqttConnectOptions();
					options.setConnectionTimeout(60);
					options.setKeepAliveInterval(60);
					options.setUserName("jeroen");
					options.setPassword("Ittes_2".toCharArray());
					mqttClient.connect(options);
					MqttMessage msg = new MqttMessage();
					msg.setPayload(JsonUtil.toJson(measurement).getBytes());
					mqttClient.publish("test", msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
