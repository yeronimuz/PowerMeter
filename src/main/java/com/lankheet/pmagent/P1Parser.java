package com.lankheet.pmagent;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class P1Parser {

	private static final String DECIMAL_PATTERN = "([0-9]*\\.[0-9]*)";

	private static final String KW_PATTERN = DECIMAL_PATTERN + "\\*kW";

	private static final String KWH_PATTERN = KW_PATTERN + "h";

	// \\(([0-9]*\\.[0-9]*)\\)$
	private static final String M3_PATTERN = "\\(" + DECIMAL_PATTERN + "\\)$";

	private static final Logger LOG = LogManager.getLogger(P1Parser.class);

	/**
	 * Take one complete message and parse it into a P1Datagram object
	 * 
	 * @param tempS
	 *            The text message to parse
	 * @return A P1 datagram object
	 */
	public static P1Datagram parse(String tempS) {
		LOG.debug(tempS);
		P1Datagram p1dg = new P1Datagram();
		String[] lines = tempS.split("[\\r\\n]+");
		for (String line : lines) {
			LOG.info(line);
			if (line.startsWith("/") || line.startsWith("!")) {
				// Ignore
			} else {
				int idx = line.indexOf('(');
				int idy = line.lastIndexOf(')');
				if ((idx == -1) || (idy == -1)) {
					continue;
				}
				String id = line.substring(0, idx);
				String value = line.substring(idx + 1, idy);
				parseP1Line(p1dg, id, value);
			}
		}
		return p1dg;
	}

	/**
	 * Parse a semi-raw value.
	 * 
	 * @param p1dg
	 * @param id
	 * @param value
	 */
	private static void parseP1Line(P1Datagram p1dg, String id, String value) {
		P1Standard p1 = P1Standard.getDescription(id);
		LOG.debug("parseP1Line: id = " + id + ", value = " + value);

		if (p1 != null) {
			switch (p1) {
			case VERSION_INFO:
				p1dg.setVersionInfo(Byte.parseByte(value));
				break;
			case CONSUMED_POWER_TARIFF_1:
				p1dg.setConsumedPowerTariff1(parseGetDoubleValue(value, KWH_PATTERN));
				break;
			case CONSUMED_POWER_TARIFF_2:
				p1dg.setConsumedPowerTariff2(parseGetDoubleValue(value, KWH_PATTERN));
				break;
			case DELIVERED_POWER_TARIFF_1:
				p1dg.setProducedPowerTariff1(parseGetDoubleValue(value, KWH_PATTERN));
				break;
			case PRODUCED_POWER_TARIFF_2:
				p1dg.setProducedPowerTariff2(parseGetDoubleValue(value, KWH_PATTERN));
				break;
			case DATE_TIMESTAMP:
				// Format 123456789012S|W; 1..0 is TS in Secs, 12 is 100ths of
				// secs
				p1dg.setDateTimeStamp(parseDateTimeValue(value.substring(0, value.length() - 1)));
				break;
			case EQUIPMENT_ID_01:
			case EQUIPMENT_ID_00:
				break;
			case CURRENT_TARIFF: // 2
				p1dg.setCurrentTariff(Byte.parseByte(value));
				break;
			case CURRENT_CONSUMED_PWR: // kW
				p1dg.setCurrentConsumedPwr(parseGetDoubleValue(value, KW_PATTERN));
				break;
			case CURREN_TDELIVERED_PWR: // kW
				p1dg.setCurrentDeliveredPwr(parseGetDoubleValue(value, KW_PATTERN));
				break;
			/** nr.of. power failures in any phase */
			case NR_OF_POWER_FAILURES_IN_ANY_PHASE:
			case NR_OF_LONG_POWER_FAILURES_IN_ANY_PHASE: // #
			case POWER_FAILURE_EVENT_LOG: //
			case NR_OF_VOLTAGE_SAGS_IN_PHASE_L1: //
			case NR_OF_VOLTAGE_SAGS_IN_PHASE_L2: // nr.of voltage sags in phase
													// L2
			case TEXT_MESSAGE_CODES: // numeric 8 digits
			case TEXT_MESSAGE: // max 1024 chars
			case INSTANTANEOUS_CURRENT_L1: //
			case INSTANTANEOUS_ACTIVE_POWER_L1_PLUS_P: // (kW)
			case INSTANTANEOUS_ACTIVE_POWER_L1_MIN_P: // Instantaneous active
														// power L1 -P
			case DEVICE_TYPE: // Device Type (003)
				break;
			case CONSUMED_GAS: // (151009120000S)(00086.298*m3) // consumed gas
				p1dg.setConsumedGas(parseM3(value));
				break;
			default:
				LOG.error("No such id: " + id + ", value: " + value);
			}
		} // else we skip this line
	}

	private static double parseGetDoubleValue(String value, String sPattern) {
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(value);

		return (matcher.find()) ? Double.parseDouble(matcher.group(1)) : 0;
	}

	private static double parseM3(String value) {
		Pattern pattern = Pattern.compile(DECIMAL_PATTERN);
		Matcher matcher = pattern.matcher(value);

		return (matcher.find()) ? Double.parseDouble(matcher.group(1)) : 0;
	}

	private static DateTime parseDateTimeValue(String value) {
		// Value contains TS in seconds and last 3 digits hundreds of seconds
		// and char 'S'
		return new DateTime(Long.parseLong(value.replace('S', '0')));
	}

}
