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
    // TODO: externalize this key in config file
    private static final String PMETER_UNIQUE_KEY = "/XMX5LGBBFG1009021021";
    private static String buffS = "";
    
    /**
     * The delay is determined to be accurate for receiving one datagram at a time.<BR>
     * Other values will lead to fragmentation of datagrams.
     */
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
                // Ignore
            }
            int numChars = event.getEventValue();
            try {
                byte buf[] = serialPort.readBytes(numChars);
                chunkS = new String(buf);
                LOG.debug(chunkS);
            } catch (SerialPortException ex) {
                LOG.error(ex.getMessage());
                return;
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
                // TODO: Set health status to unhealthy
                LOG.error(e.getMessage());
            }
            LOG.info("Saved: " + datagram);
            return bufS.substring(stop + 4); // sometimes only 3 chars.
                                             // CR/LF not taken into
                                             // account
        }
        // else wait for another event
        // TODO: Reset health status to healthy
        return bufS;
    }

    private void publishDatagram(P1Datagram datagram) throws MqttException {
        List<SensorValue> sensorValueList = SensorValueAdapter.convertP1Datagram(sensorNode, datagram);
        sensorValueList.forEach(sensorValue -> {
            sensorValueListener.newSensorValue(sensorValue);
        });
    }
}
