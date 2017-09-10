/**
 * 
 */
package com.lankheet.pmagent.p1;

/**
 * The types of measurements in P1 datagram
 * Meant for lookup table in DB
 */
public enum MeasurementType {
	PRODUCED_POWER_T1,
	PRODUCED_POWER_T2,
	CONSUMED_POWER_T1,
	CONSUMED_POWER_T2,
	ACTUAL_CONSUMED_POWER,
	ACTUAL_PRODUCED_POWER,
	CONSUMED_GAS,
	
	TEMPERATURE,
	HUMIDITY,
}
