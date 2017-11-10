package com.lankheet.pmagent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lankheet.iot.datatypes.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.config.SensorConfig;
import com.lankheet.pmagent.config.SerialPortConfig;

import cucumber.api.java.After;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class PowerMeterAgentConfigTest {
	// Prepare the SUT
	private static class PMAgentConfigTester extends Application<PMAgentConfig> {
		private static PMAgentConfigTester instance = null;
		private PMAgentConfig pmaConfig;

		private PMAgentConfigTester() {
			// Do not allow instantiation through constructor
		}

		@Override
		public void run(PMAgentConfig configuration, Environment environment) throws Exception {
			this.pmaConfig = configuration;
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
		PMAgentConfigTester.getInstance().run("server", "src/test/resources/application.yml");
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
		assertThat(sensorConfig.getSensorId(), is(1));
		assertThat(sensorConfig.getSensorTypes().get(0), is(SensorType.POWER_METER));
		assertThat(sensorConfig.getSensorTypes().get(1), is(SensorType.GAS_METER));
	}
}
