/**
 * 
 */
package com.lankheet.pmagent.p1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lankheet.pmagent.beans.Measurement;

/**
 * Converts a P1 Measurement to single measurements<BR>
 * P1 contains multiple measurements (T1 prod & cons, T2 prod & cons, gas)
 */
public class MeasurementAdapter {

	/**
	 * Takes one datagram and converts it into one or more single measurements
	 * <BR>
	 * A P1 value will be ignored if it is 0
	 * 
	 * @param datagram
	 *            The datagram to convert
	 * @return A list with single measurements
	 */
	public static List<Measurement> convertP1Datagram(P1Datagram datagram) {
		LocalDateTime ts = datagram.getDateTimeStamp();
		List<Measurement> measurementsList = new ArrayList<>();
		
		if (datagram.getConsumedPowerTariff1() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.CONSUMED_POWER_T1, datagram.getConsumedPowerTariff1()));
		}
		if (datagram.getProducedPowerTariff1() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.PRODUCED_POWER_T1, datagram.getProducedPowerTariff1()));
		}
		if (datagram.getConsumedPowerTariff2() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.CONSUMED_POWER_T2, datagram.getConsumedPowerTariff2()));
		}
		if (datagram.getProducedPowerTariff2() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.PRODUCED_POWER_T2, datagram.getProducedPowerTariff2()));
		}
		if (datagram.getCurrentConsumedPwr() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.ACTUAL_CONSUMED_POWER, datagram.getCurrentConsumedPwr()));
		}
		if (datagram.getCurrentDeliveredPwr() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.ACTUAL_PRODUCED_POWER, datagram.getCurrentDeliveredPwr()));
		}
		if (datagram.getConsumedGas() != 0.0) {
			measurementsList
					.add(new Measurement(ts, MeasurementType.CONSUMED_GAS, datagram.getConsumedGas()));
		}

		return measurementsList;
	}
}
