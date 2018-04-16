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

package com.lankheet.pmagent;

import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.pmagent.p1.P1Parser;
import com.lankheet.pmagent.p1.SensorValueAdapter;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Wrapper around SerialPort Reader
 *
 */
public class SerialPortReader implements SerialPortEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(SerialPortReader.class);

	private static final char STOP_TOKEN = '!';
	private static final String PMETER_UNIQUE_KEY = "/XMX5LGBBFG1009021021";
	private static String buffS = "";
	private static final int WAIT_FOR_DATA = 500;
	
	private SerialPort serialPort;
	
	SensorValueListener sensorValueListener;

	private SensorNode sensorNode;
	
	public SerialPortReader(SerialPort serialPort, SensorNode sensorNode, SensorValueListener listener) {
		this.serialPort = serialPort;
		this.sensorValueListener = listener;
		this.sensorNode = sensorNode;
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
				LOG.error(ex.getMessage());
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
		List<SensorValue> sensorValueList = SensorValueAdapter.convertP1Datagram(sensorNode, datagram);
		sensorValueList.forEach(sensorValue -> {
			sensorValueListener.newSensorValue(sensorValue);
		});
	}
}
