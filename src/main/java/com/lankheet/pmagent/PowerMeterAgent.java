/**
 * Main application class for the PowerMeter agent
 */
package com.lankheet.pmagent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.localstorage.LocalStorage;
import com.lankheet.localstorage.LocalStorageFile;
import com.lankheet.pmagent.beans.Measurement;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.resources.AboutPMAgent;
import com.lankheet.pmagent.resources.PMAboutResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It saves the datagram on disk, location is specified in yml config file<br>
 */
public class PowerMeterAgent extends Application<PMAgentConfig> {

	private static final Logger LOG = LogManager.getLogger(PowerMeterAgent.class);

	/** P1 UART */
	static SerialPort serialPort;

	public static void main(String[] args) throws Exception {
		new PowerMeterAgent().run(args[0], args[1]);
	}

	@Override
	public void initialize(Bootstrap<PMAgentConfig> bootstrap) {
		LOG.info("P1 manager", "");
	}

	@Override
	public void run(PMAgentConfig configuration, Environment environment) throws Exception {
		final PMAboutResource pmaResource = new PMAboutResource(new AboutPMAgent());

		// TODO: Start new thread (or something) that<BR>
		// * reads data files
		// * puts them in th queue

		serialPort = new SerialPort(configuration.getSerialPortConfig().getUart());

		try {
			if (!serialPort.openPort()) {
				LOG.error("Serial port: Open port failed");
				return;
			}
			serialPort.setParams(configuration.getSerialPortConfig().getBaudRate(), 8, 1, 0);
			int mask = SerialPort.MASK_RXCHAR;
			if (!serialPort.setEventsMask(mask)) {
				LOG.error("Serial port: Unable to set mask");
				return;
			}
			serialPort.addEventListener(
					new SerialPortReader(serialPort, new MeasurementSender("tcp://192.168.2.10:1883")));

		} catch (SerialPortException ex) {
			LOG.error(ex.getMessage());
		}

		environment.getApplicationContext().setContextPath("/api");
		environment.jersey().register(pmaResource);
	}
}