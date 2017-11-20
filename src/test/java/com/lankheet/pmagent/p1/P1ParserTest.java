package com.lankheet.pmagent.p1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import com.lankheet.utils.JsonUtil;

public class P1ParserTest {

	@Test
	public void test() throws IOException, URISyntaxException {
		String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1_2.log").toURI())));
		System.out.println(input);
		
		P1Datagram dg = P1Parser.parse(input);
		assertThat(dg.getConsumedPowerTariff1(), is(equalTo(207.138)));
		assertThat(dg.getConsumedPowerTariff2(), is(equalTo(27.545)));
		assertThat(dg.getProducedPowerTariff1(), is(equalTo(269.06)));
		assertThat(dg.getProducedPowerTariff2(), is(equalTo(74.828)));
		assertThat(dg.getConsumedGas(), is(equalTo(86.298)));
		assertThat(dg.getCurrentConsumedPwr(), is(equalTo(0.984)));
		assertThat(dg.getCurrentDeliveredPwr(), is(equalTo(0.0)));
		assertThat(dg.getCurrentTariff(), is(equalTo((byte)2)));
		
		String jsonString = JsonUtil.toJson(dg);
		System.out.println(jsonString);
	}
}
