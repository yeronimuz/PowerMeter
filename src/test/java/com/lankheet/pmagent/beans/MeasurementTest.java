package com.lankheet.pmagent.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.utils.JsonUtil;

public class MeasurementTest {
	private static final Logger LOG = LogManager.getLogger(MeasurementTest.class);

	@Test
	public void test() throws JsonParseException, JsonMappingException, IOException {
		Date now = new Date();
		Measurement meas = new Measurement(0, now, MeasurementType.CONSUMED_GAS.getId(), 2.0);
		String jsonMeasurement = JsonUtil.toJson(meas);
		LOG.info(jsonMeasurement);
		
		Measurement measBack = JsonUtil.measurementFromJson(jsonMeasurement);
		assertThat(measBack.getType(), is(MeasurementType.CONSUMED_GAS.getId()));
		assertThat(measBack.getTimeStamp().compareTo(now), is(0));
		assertThat(measBack.getValue(), is(2.0));
	}

}
