package com.lankheet.pmagent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.config.SerialPortConfig;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.pmagent.p1.P1Parser;
import com.lankheet.pmagent.p1.SensorValueAdapter;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Wrapper around SerialPort Reader. It runs in it's own thread and puts sensorValue objects in a
 * queue.<BR>
 * When disconnected, a retry mechanism will reconnect periodically (NR_OF_LOOPS_FOR_RETRY *
 * SERIAL_PORT_READER_LOOP_WAIT_MS) ms
 */
public class SerialPortReader implements SerialPortEventListener, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SerialPortReader.class);
    private static final int NR_OF_LOOPS_FOR_RETRY = 10;

    private static final int SERIAL_PORT_READER_LOOP_WAIT_MS = 100;


    private static final int SERIAL_DATA_BITS = 8;
    private static final char STOP_TOKEN = '!';
    private static String powerMeterUniqueKey;
    private static String buffS = "";

    /**
     * The delay is determined to be accurate for receiving one datagram at a time.<BR>
     * Other values will lead to fragmentation of datagrams.
     */
    private static final int WAIT_FOR_DATA = 500;

    private final BlockingQueue<SensorValue> queue;
    private SerialPort serialPort;
    private SerialPortConfig serialPortConfig;

    private SensorNode sensorNode;

    public SerialPortReader(BlockingQueue<SensorValue> queue, SerialPortConfig serialPortConfig,
            SensorNode sensorNode) {
        this.queue = queue;
        this.serialPortConfig = serialPortConfig;
        this.sensorNode = sensorNode;
    }

    @Override
    public void run() {
        powerMeterUniqueKey = serialPortConfig.getP1Key();
        serialPort = new SerialPort(serialPortConfig.getUart());
        try {
            // FIXME: When port cannot be opened, retry in endless loop
            // Don't return, but retry indefinitely
            if (!serialPort.openPort()) {
                LOG.error("Serial port: Open port failed");
                return;
            }
            serialPort.setParams(serialPortConfig.getBaudRate(), SERIAL_DATA_BITS, 1, 0);
            int mask = SerialPort.MASK_RXCHAR;
            if (!serialPort.setEventsMask(mask)) {
                LOG.error("Serial port: Unable to set mask");
                return;
            }
            serialPort.addEventListener(this);
        } catch (SerialPortException ex) {
            LOG.error(ex.getMessage());
        }
        int counter = 0;
        while (true) {
            counter++;
            if (!serialPort.isOpened() && (counter % NR_OF_LOOPS_FOR_RETRY) == 0) {
                try {
                    serialPort.openPort();
                } catch (SerialPortException e) {
                    LOG.error("Unable to re-open serial port: " + e.getMessage());;
                }
            }
            // The serial port triggers a new event and is handled by serialEvent
            try {
                Thread.sleep(SERIAL_PORT_READER_LOOP_WAIT_MS);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
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
        int start = bufS.indexOf(powerMeterUniqueKey);
        int stop = bufS.indexOf(STOP_TOKEN); // Followed by 3 or 4 characters
        // (checksum)

        LOG.debug("Start: " + start + ", Stop: " + stop + ", String: " + bufS);
        if ((start >= 0) && (stop >= 0)) {
            // Enough captured, parse the datagram part, save the remainder
            LOG.debug("Parse:" + bufS.substring(start, stop + 4));

            P1Datagram datagram = P1Parser.parse(bufS.substring(start, stop + 4));
            publishDatagram(datagram);
            LOG.info("Saved: " + datagram);
            return bufS.substring(stop + 4); // sometimes only 3 chars.
                                             // CR/LF not taken into
                                             // account
        }
        // else wait for another event
        return bufS;
    }

    private void publishDatagram(P1Datagram datagram) {
        List<SensorValue> sensorValueList = SensorValueAdapter.convertP1Datagram(sensorNode, datagram);
        LOG.debug("Putting " + sensorValueList.size() + " values in the queue...");
        for (SensorValue sensorValue : sensorValueList) {
            try {
                queue.put(sensorValue);
            } catch (InterruptedException e) {
                LOG.error("Putting data in queue was interrupted");
            }
        }
    }
}
