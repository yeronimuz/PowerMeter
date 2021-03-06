package com.lankheet.pmagent;

import java.util.concurrent.BlockingQueue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.config.SerialPortConfig;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;


public class SerialPortReaderTest {

    @Mocked
    private SerialPort serialPortMock;
    @Mocked
    private SensorValueListener sensorValueListenerMock;
    @Mocked
    private SensorNode sensorNodeMock;
    @Mocked
    private BlockingQueue<SensorValue> queueMock;
    @Mocked
    private LoggerFactory LoggerFactoryMock;
    @Mocked
    private Logger loggerMock;

    private SerialPortConfig serialPortConfig = new SerialPortConfig() {
        {
            setBaudRate(115200);
            setUart("/dev/ttySomething");
            setP1Key("/XMX5LGBBFG1009021021");
        }
    };


    @Test
    public void testOneDatagram() throws SerialPortException, InterruptedException {

        SerialPortReader serialPortReader = new SerialPortReader(queueMock, serialPortConfig, sensorNodeMock);
        Deencapsulation.setField(serialPortReader, "serialPort", serialPortMock);
        Deencapsulation.setField(serialPortReader, "powerMeterUniqueKey", "/XMX5LGBBFG1009021021");

        new Expectations() {
            {
                serialPortMock.readBytes(200);
                result = new String("/XMX5LGBBFG1009021021\n" + "\n" + "1-3:0.2.8(42)\n" + "0-0:1.0.0(151009123245S)\n"
                        + "0-0:96.1.1(4530303331303033303331323532363135)\n" + "1-0:1.8.1(000207.138*kWh)\n"
                        + "1-0:1.8.2(000269.060*kWh)\n" + "1-0:2.8.1(000027.545*kWh)\n" + "1-0:2.8.2(000074.828*kWh)\n"
                        + "0-0:96.14.0(0002)\n" + "1-0:1.7.0(00.984*kW)\n" + "1-0:2.7.0(00.000*kW)\n"
                        + "0-0:96.7.21(00002)\n" + "0-0:96.7.9(00000)\n" + "1-0:99.97.0(0)(0-0:96.7.19)\n"
                        + "1-0:32.32.0(00000)\n" + "1-0:32.36.0(00000)\n" + "0-0:96.13.1()\n" + "0-0:96.13.0()\n"
                        + "1-0:31.7.0(004*A)\n" + "1-0:21.7.0(00.984*kW)\n" + "1-0:22.7.0(00.000*kW)\n"
                        + "0-1:24.1.0(003)\n" + "0-1:96.1.0(4730303332353631323335313738313135)\n"
                        + "0-1:24.2.1(151009120000S)(00086.298*m3)\n" + "!99CF").getBytes();
            }
        };

        serialPortReader.serialEvent(new SerialPortEvent("", SerialPortEvent.RXCHAR, 200 /* nr_of_bytes */));

        new Verifications() {
            {
                queueMock.put((SensorValue) any);;
                times = 7;

                loggerMock.error(anyString);
                times = 0;
            }
        };
    }

    @Test
    public void testErrorReadingSerialPort() throws SerialPortException {
        SerialPortReader serialPortReader = new SerialPortReader(queueMock, serialPortConfig, sensorNodeMock);
        Deencapsulation.setField(serialPortReader, "serialPort", serialPortMock);
        new Expectations() {
            {
//                LoggerFactory.getLogger(SerialPortReader.class);
//                result = loggerMock;

                serialPortMock.readBytes(200);
                result = new SerialPortException("tty", "that was a fault", "third");
            }
        };


        serialPortReader.serialEvent(new SerialPortEvent("", SerialPortEvent.RXCHAR, 200 /* nr_of_bytes */));


        new Verifications() {
            {
                loggerMock.error("Port name - tty; Method name - that was a fault; Exception type - third.");
                times = 1;
            }
        };
    }

    public void testShortChunksFromSerialPort() {
        // TODO
    }

    public void testFlowAfterExceptionWithLotsOfChunks() {
        // TODO
    }
}
