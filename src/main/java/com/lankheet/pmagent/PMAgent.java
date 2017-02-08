
/**
 * 
 */
package com.lankheet.pmagent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lankheet.localstorage.LocalStorage;
import com.lankheet.localstorage.LocalStorageFile;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.pmagent.p1.P1Parser;
import com.lankheet.pmagent.resources.AboutPMAgent;
import com.lankheet.pmagent.resources.PMAboutResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It saves the datagram on disk, location in config file<br>
 */
public class PMAgent extends Application<PMAgentConfig>{

	private static final Logger LOG = LogManager.getLogger(PMAgent.class);

	/** P1 UART */
	static SerialPort serialPort;
	
	/** File storage for temporary saving data when the database is not available */
	static LocalStorage localStorage;
	
	public static void main(String[] args) throws Exception {
		new PMAgent().run(args[0], args[1]);
	}
	
	@Override
	public void initialize(Bootstrap<PMAgentConfig> bootstrap) {
		LOG.info("P1 manager", "");
	}
	
	@Override
	public void run(PMAgentConfig configuration, Environment environment) throws Exception {
		final PMAboutResource pmaResource = new PMAboutResource(new AboutPMAgent());
		
		String dirPath = configuration.getLocalStorageConfig().getStoragePath();
		String pattern = configuration.getLocalStorageConfig().getFileNamePattern();
		
		localStorage = new LocalStorageFile();
		localStorage.activate(dirPath);
		
		serialPort = new SerialPort(configuration.getSerialPortConfig().getUart());
		// serialPort = new SerialPort("/dev/ttyUSB0");
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
			serialPort.addEventListener(new SerialPortReader());

		} catch (SerialPortException ex) {
			LOG.error(ex.getMessage());
		}
		
		environment.getApplicationContext().setContextPath("/api");
		environment.jersey().register(pmaResource);
	}

	static class SerialPortReader implements SerialPortEventListener {

		private static String buffS = "";

		public void serialEvent(SerialPortEvent event) {

			String chunkS = null;
			if (event.isRXCHAR()) {
				try {
					// Wait to get bigger data chunks
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				int numChars = event.getEventValue();
				try {
					byte buf[] = serialPort.readBytes(numChars);
					chunkS = new String(buf);
					LOG.info(chunkS);
				} catch (SerialPortException ex) {
					LOG.error(ex);
				}
				// Store
				if (numChars > 0) {
					buffS = buffS.concat(chunkS);
				}
				buffS = evaluateAndSaveSerialData(buffS);
			}
		}
		
		private String evaluateAndSaveSerialData(String bufS) {
			int start = bufS.indexOf("/XMX5LGBBFG1009021021");
			int stop = bufS.indexOf('!');
			
			LOG.debug("Start: " + start + ", Stop: " + stop + ", String: " + bufS);
			if ((start >= 0) && (stop >= 0) ) {
				// Enough captured, parse the datagram part, save the remainder
				LOG.debug("Parse:" + bufS.substring(start, stop + 4));
				
				P1Datagram datagram = P1Parser.parse(bufS.substring(start, stop + 4));
				localStorage.storeP1Measurement(datagram);
				LOG.info("Saved: " + datagram);
				return bufS.substring(stop + 4); // sometimes only 3 chars. CR/LF not taken into account
			} 
			// else wait for another event
			return bufS;
		}
	}
}