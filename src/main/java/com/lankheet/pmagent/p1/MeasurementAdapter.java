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

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;

/**
 * Converts a P1 Measurement to single measurements<BR>
 * P1 contains multiple measurements (T1 prod & cons, T2 prod & cons, gas)
 */
public class MeasurementAdapter {

	/**
	 * Takes one datagram and converts it into one or more single measurements <BR>
	 * @param sensorId The configured sensorId that generated the measurement
	 * @param datagram
	 *            The datagram to convert
	 * @return A list with single measurements
	 */
	public static List<Measurement> convertP1Datagram(int sensorId, P1Datagram datagram) {
		Date ts = Date.from(datagram.getDateTimeStamp().atZone(ZoneId.systemDefault()).toInstant());
		List<Measurement> measurementsList = new ArrayList<>();

		// TODO: It should be possible to filter what needs to be sent. Now it's
		// hard coded

		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.CONSUMED_POWER_T1.getId(), datagram.getConsumedPowerTariff1()));
		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.PRODUCED_POWER_T1.getId(), datagram.getProducedPowerTariff1()));
		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.CONSUMED_POWER_T2.getId(), datagram.getConsumedPowerTariff2()));
		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.PRODUCED_POWER_T2.getId(), datagram.getProducedPowerTariff2()));
		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.ACTUAL_CONSUMED_POWER.getId(), datagram.getCurrentConsumedPwr()));
		measurementsList
				.add(new Measurement(sensorId, ts, MeasurementType.ACTUAL_PRODUCED_POWER.getId(), datagram.getCurrentDeliveredPwr()));
		measurementsList.add(new Measurement(sensorId, ts, MeasurementType.CONSUMED_GAS.getId(), datagram.getConsumedGas()));

		return measurementsList;
	}
}
