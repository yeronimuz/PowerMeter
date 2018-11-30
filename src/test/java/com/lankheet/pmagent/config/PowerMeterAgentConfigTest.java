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

package com.lankheet.pmagent.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.PowerMeterAgent;
import cucumber.api.java.After;

public class PowerMeterAgentConfigTest {
	// Prepare the SUT
	private static class PMAgentConfigTester extends PowerMeterAgent {
		private static PMAgentConfigTester instance = null;
		private PMAgentConfig pmaConfig;

		private PMAgentConfigTester() {
			// Do not allow instantiation through constructor
		}

		public void run(String configFileName) throws Exception {
			this.pmaConfig = this.loadConfigurationFromFile(configFileName);
		}

		public static PMAgentConfigTester getInstance() {
			if (instance == null) {
				instance = new PMAgentConfigTester();
			}
			return instance;
		}
	}

	@BeforeClass
	public static void setup() throws Exception {
		PMAgentConfigTester.getInstance().run("src/test/resources/application.yml");
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testConfigSerial() throws Exception {
		PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
		SerialPortConfig serialConfig = pmaTester.pmaConfig.getSerialPortConfig();
		assertThat(serialConfig, is(notNullValue()));
		assertThat(serialConfig.getBaudRate(), is(115200));
		assertThat(serialConfig.getUart(), is("/dev/ttyUSB0"));
	}

	@Test
	public void testConfigMqtt() {
		PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
		
		MqttConfig mqttConfig = pmaTester.pmaConfig.getMqttConfig();
		assertThat(mqttConfig, is(notNullValue()));
		assertThat(mqttConfig.getUserName(), is("johndoe"));
		assertThat(mqttConfig.getTopics().size(), is(2));
		assertThat(mqttConfig.getTopics().get(0).getType().toString().toLowerCase(), is("power"));
		assertThat(mqttConfig.getTopics().get(0).getTopic(), is("lnb/eng/power"));
		assertThat(mqttConfig.getTopics().get(1).getType().toString().toLowerCase(), is("gas"));
		assertThat(mqttConfig.getTopics().get(1).getTopic(), is("lnb/eng/gas"));
	}
	
	@Test
	public void testSensorConfig() {
		PMAgentConfigTester pmaTester = PMAgentConfigTester.getInstance();
		SensorConfig sensorConfig = pmaTester.pmaConfig.getSensorConfig();
		assertThat(sensorConfig.getNic(), is("wlo1"));
		assertThat(sensorConfig.getSensorTypes().get(0), is(SensorType.POWER_METER));
		assertThat(sensorConfig.getSensorTypes().get(1), is(SensorType.GAS_METER));
	}
}
