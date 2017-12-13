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

/**
 * P1 standard ID's
 */
public enum P1Standard {
	VERSION_INFO("1-3:0.2.8", "VERSION_INFO"),
	/** YYMMDDHHmmSS[S|W] S for summer and W for winter*/
	DATE_TIMESTAMP("0-0:1.0.0", ""),	// 12 digit timestamp with 
	EQUIPMENT_ID_01("0-1:96.1.0", ""),
	EQUIPMENT_ID_00("0-0:96.1.1", ""),
	CONSUMED_POWER_TARIFF_1("1-0:1.8.1", ""), 
	DELIVERED_POWER_TARIFF_1("1-0:1.8.2", ""),
	CONSUMED_POWER_TARIFF_2("1-0:2.8.1", ""),
	PRODUCED_POWER_TARIFF_2("1-0:2.8.2", ""),
	CURRENT_TARIFF("0-0:96.14.0", ""),           // 2
	CURRENT_CONSUMED_PWR("1-0:1.7.0", ""),        // kW
	CURREN_TDELIVERED_PWR("1-0:2.7.0", ""),         // kW
	/** nr.of. power failures in any phase */
	NR_OF_POWER_FAILURES_IN_ANY_PHASE("0-0:96.7.21", ""),       
	NR_OF_LONG_POWER_FAILURES_IN_ANY_PHASE("0-0:96.7.9", ""),           // #
	POWER_FAILURE_EVENT_LOG("1-0:99.97.0", ""), // 
	NR_OF_VOLTAGE_SAGS_IN_PHASE_L1("1-0:32.32.0", ""),          // 
	NR_OF_VOLTAGE_SAGS_IN_PHASE_L2("1-0:32.36.0", ""),          // nr.of voltage sags in phase L2
	TEXT_MESSAGE_CODES("0-0:96.13.1", ""),               // numeric 8 digits
	TEXT_MESSAGE("0-0:96.13.0", ""),               // max 1024 chars
	INSTANTANEOUS_CURRENT_L1("1-0:31.7.0", ""),           // 
	INSTANTANEOUS_ACTIVE_POWER_L1_PLUS_P("1-0:21.7.0", ""),       // (kW)
	INSTANTANEOUS_ACTIVE_POWER_L1_MIN_P("1-0:22.7.0", ""),        // Instantaneous active power L1 -P
	DEVICE_TYPE("0-1:24.1.0", ""),             // Device Type (003)
	CONSUMED_GAS("0-1:24.2.1", ""); // (151009120000S)(00086.298*m3) // consumed gas	

	private String id;
	
	private String description;
	
	P1Standard(String strId, String description) {
		this.id = strId;
		this.description = description;
	}
	
	public String getCode() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static P1Standard getDescription(String id) {
		for (P1Standard p1: values()) {
			if (p1.getCode().equals(id)) {
				return p1;
			}
		}
		return null;
	}
}
