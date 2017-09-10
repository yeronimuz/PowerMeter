package com.lankheet.pmagent.p1;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.lankheet.pmagent.MessageQueueClient;
import com.lankheet.pmagent.beans.Measurement;

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
		assertThat(measurements.size(), is(6));
		measurements.forEach(measurement -> {
			if (measurement.getValue() != 0.0) {

				System.out.println(measurement);
				try (MessageQueueClient msgQclient = new MessageQueueClient("tcp://localhost:1883");) {
					msgQclient.connect();
					msgQclient.publish(measurement);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
