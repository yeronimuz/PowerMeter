package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.Measurement;

/**
 * MeasurementsListener listens for newMeasurements.<BR>
 * The measurements are coming (a.o.) from the P1 smart meter via the
 * MeasurementAdapter.<BR>
 */
public interface MeasurementListener {

	void newMeasurement(Measurement measurement);

}
