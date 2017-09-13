package com.lankheet.pmagent;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.pmagent.p1.MeasurementAdapter;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.pmagent.p1.P1Parser;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Wrapper around SerialPort Reader
 *
 */
public class SerialPortReader implements SerialPortEventListener {
	private static final Logger LOG = LogManager.getLogger(SerialPortReader.class);

	private static final char STOP_TOKEN = '!';
	private static final String PMETER_UNIQUE_KEY = "/XMX5LGBBFG1009021021";
	private static String buffS = "";
	private static final int WAIT_FOR_DATA = 500;
	
	private SerialPort serialPort;
	
	MeasurementListener measurementListener;
	
	public SerialPortReader(SerialPort serialPort, MeasurementListener listener) {
		this.serialPort = serialPort;
		this.measurementListener = listener;
	}

	public void serialEvent(SerialPortEvent event) {

		String chunkS = null;
		if (event.isRXCHAR()) {
			try {
				// Wait to get bigger data chunks
				Thread.sleep(WAIT_FOR_DATA);
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
		int start = bufS.indexOf(PMETER_UNIQUE_KEY);
		int stop = bufS.indexOf(STOP_TOKEN); // Followed by 3 or 4 characters
		// (checksum)

		LOG.debug("Start: " + start + ", Stop: " + stop + ", String: " + bufS);
		if ((start >= 0) && (stop >= 0)) {
			// Enough captured, parse the datagram part, save the remainder
			LOG.debug("Parse:" + bufS.substring(start, stop + 4));

			P1Datagram datagram = P1Parser.parse(bufS.substring(start, stop + 4));
			try {
				publishDatagram(datagram);
			} catch (MqttException e) {
				LOG.error(e.getMessage());
			}
			LOG.info("Saved: " + datagram);
			return bufS.substring(stop + 4); // sometimes only 3 chars.
												// CR/LF not taken into
												// account
		}
		// else wait for another event
		return bufS;
	}

	private void publishDatagram(P1Datagram datagram) throws MqttException {
		List<Measurement> measurementsList = MeasurementAdapter.convertP1Datagram(datagram);
		measurementsList.forEach(measurement -> {
			measurementListener.newMeasurement(measurement);
		});
	}
}
