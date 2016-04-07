
/**
 * 
 */
package com.lankheet.pmagent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.localstorage.LocalStorage;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * @author jeroen
 *
 */
public class PMAgent extends Application<PMAgentConfig>{

	private static final Logger LOG = LogManager.getLogger("PMAgent");

	static SerialPort serialPort;

	public static void main(String[] args) throws Exception {
		new PMAgent().run(args);
	}
	
	// @Override
	public void initilize(Bootstrap<PMAgentConfig> bootstrap) {
		LOG.info("P1 manager", "");
		serialPort = new SerialPort("/dev/ttyUSB0");
		try {
			if (!serialPort.openPort()) {
				LOG.error("Serial port: Open port failed");
				return;
			}
			serialPort.setParams(115200, 8, 1, 0);// Set params
			int mask = SerialPort.MASK_RXCHAR;
			if (!serialPort.setEventsMask(mask)) {
				LOG.error("Serial port: Unable to set mask");
				return;
			}
			serialPort.addEventListener(new SerialPortReader());

		} catch (SerialPortException ex) {
			LOG.error(ex.getMessage());
		}
	}
	
	@Override
	public void run(PMAgentConfig configuration, Environment environment) throws Exception {
		// TODO Auto-generated method stub
		
	}

	static class SerialPortReader implements SerialPortEventListener {
		private static String tempS = "";
		private static LocalStorage localStorage;

		static {
			localStorage.activate();
		}
		/**
		 * Last datagram line starts with '!' First one starts with "XMX"
		 */
		public void serialEvent(SerialPortEvent event) {

			LOG.trace("Event: " + event.getEventType() + ", " + event.getEventValue());
			String bufS = null;
			if (event.isRXCHAR()) {
				int numChars = event.getEventValue();
				try {
					byte buf[] = serialPort.readBytes(numChars);
					bufS = new String(buf);
					LOG.info(bufS);
				} catch (SerialPortException ex) {
					LOG.error(ex);
				}
				// Store
				tempS += bufS;
				LOG.info("Store(" + tempS.length() + "): tempS = " + tempS);
				if (numChars > 20) {
					// Enough captured, parse it
					P1Datagram datagram = P1Parser.parse(tempS);
					LOG.info(datagram);
					tempS = "";
					localStorage.storeP1Measurement(datagram);
				}
			}
		}
	}

}