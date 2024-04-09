package org.domiot.p1.pmagent;

import org.domiot.p1.pmagent.p1.DatagramToSensorValueMapper;
import org.domiot.p1.pmagent.p1.P1Datagram;
import org.domiot.p1.pmagent.p1.P1Parser;
import org.domiot.p1.utils.JvmMemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * Wrapper around a BufferedReader. The BufferedReader is the input stream of the external process that is spawned in the main thread.
 * </pre>
 */
@Slf4j
public class P1Reader implements Runnable {
    private static final String STOP_TOKEN = "!";
    private final String powerMeterUniqueKey;
    private String buffS = "";
    private final BlockingQueue<SensorValueDto> queue;
    private final DeviceDto device;
    private final BufferedReader serialBufReader;
    private final AtomicBoolean continueFlag = new AtomicBoolean(true);
    private int nrOfDatagramsProcessed = 0;

    public P1Reader(BlockingQueue<SensorValueDto> queue,
                    String powerMeterUniqueKey,
                    DeviceDto device,
                    BufferedReader serialBufReader) {
        this.queue = queue;
        this.powerMeterUniqueKey = powerMeterUniqueKey;
        this.device = device;
        this.serialBufReader = serialBufReader;
    }

    public void stop() {
        continueFlag.set(false);
    }

    @Override
    public void run() {
        String line;
        try {
            while (continueFlag.get()) {
                line = this.serialBufReader.readLine();
                if (line != null && !line.isEmpty()) {
                    buffS = buffS.concat(line + "\n");
                    if (line.startsWith(STOP_TOKEN)) {
                        processP1Data(buffS);
                        buffS = "";
                    }
                }
            }
        } catch (IOException e) {
            log.error("Unable to read data from serial port");
        }
    }

    private String processP1Data(String bufS) {
        int start = bufS.indexOf(powerMeterUniqueKey);
        int stop = bufS.indexOf(STOP_TOKEN); // Followed by 3 or 4 characters (checksum)

        if ((start >= 0) && (stop >= 0)) {
            // Enough captured, parse the datagram part, save the remainder
            String stringToParse = bufS.substring(start, stop + 4);

            P1Datagram datagram = P1Parser.parse(stringToParse);
            publishDatagram(datagram);
            log.debug("Saved: {}", datagram);
            return bufS.substring(stop + 4); // sometimes only 3 chars.
            // CR/LF not taken into account
        }
        // else wait for more lines
        return bufS;
    }

    private void publishDatagram(P1Datagram datagram) {
        List<SensorValueDto> sensorValueList = DatagramToSensorValueMapper.convertP1Datagram(device, datagram);

        JvmMemoryUtil.logMemoryStatistics();

        log.debug("DG {}: Putting {} values in the queue...", nrOfDatagramsProcessed++, sensorValueList.size());

        for (SensorValueDto sensorValue : sensorValueList) {
            try {
                queue.put(sensorValue);
                log.debug("Queue space: {}", queue.remainingCapacity());
            } catch (InterruptedException e) {
                log.error("Putting data in queue was interrupted");
            }
        }
    }
}
