package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.config.SerialPortConfig;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SerialPortReaderTest
{
   @InjectMocks
   private SerialPortReader serialPortReader;

   @Mock
   private SensorNode                 sensorNode;
   @Mock
   private BlockingQueue<SensorValue> queue;
   @Mock
   private Logger                     logger;

   private final SerialPortConfig serialPortConfig = new SerialPortConfig()
   {
      {
         setBaudRate(115200);
         setUart("/dev/ttySomething");
         setP1Key("/XMX5LGBBFG1009021021");
      }
   };

   @Test
   void testOneDatagram(@Mock SerialPort serialPort)
      throws SerialPortException, InterruptedException, IllegalAccessException
   {
      setField(serialPortReader, "serialPort", serialPort);
      setField(serialPortReader, "powerMeterUniqueKey", "/XMX5LGBBFG1009021021");
      when(serialPort.readBytes(200)).thenReturn(
         ("/XMX5LGBBFG1009021021\n" + "\n" + "1-3:0.2.8(42)\n" + "0-0:1.0.0(151009123245S)\n"
            + "0-0:96.1.1(4530303331303033303331323532363135)\n" + "1-0:1.8.1(000207.138*kWh)\n"
            + "1-0:1.8.2(000269.060*kWh)\n" + "1-0:2.8.1(000027.545*kWh)\n" + "1-0:2.8.2(000074.828*kWh)\n"
            + "0-0:96.14.0(0002)\n" + "1-0:1.7.0(00.984*kW)\n" + "1-0:2.7.0(00.000*kW)\n"
            + "0-0:96.7.21(00002)\n" + "0-0:96.7.9(00000)\n" + "1-0:99.97.0(0)(0-0:96.7.19)\n"
            + "1-0:32.32.0(00000)\n" + "1-0:32.36.0(00000)\n" + "0-0:96.13.1()\n" + "0-0:96.13.0()\n"
            + "1-0:31.7.0(004*A)\n" + "1-0:21.7.0(00.984*kW)\n" + "1-0:22.7.0(00.000*kW)\n"
            + "0-1:24.1.0(003)\n" + "0-1:96.1.0(4730303332353631323335313738313135)\n"
            + "0-1:24.2.1(151009120000S)(00086.298*m3)\n" + "!99CF").getBytes());

      serialPortReader.serialEvent(new SerialPortEvent("", SerialPortEvent.RXCHAR, 200 /* nr_of_bytes */));

      verify(queue, times(7)).put(any(SensorValue.class));
      verify(logger, times(0)).error(anyString());
   }


   private void setField(SerialPortReader serialPortReader, String fieldName, Object object)
      throws IllegalAccessException
   {
      List<Field> fields = ReflectionUtils.findFields(SerialPortReader.class, f -> f.getName().equals(fieldName), TOP_DOWN);
      Field field = fields.get(0);
      field.setAccessible(true);
      field.set(serialPortReader, object);
   }


   // @Test
   void testErrorReadingSerialPort(@Mock SerialPort serialPort, @Mock Logger logger)
      throws SerialPortException, IllegalAccessException
   {
      // TODO: Mocking logger fails
      setField(serialPortReader, "serialPort", serialPort);
      when(serialPort.readBytes(200)).thenThrow(new SerialPortException("tty", "that was a fault", "third"));
      serialPortReader.serialEvent(new SerialPortEvent("", SerialPortEvent.RXCHAR, 200 /* nr_of_bytes */));

      verify(logger, times(1)).error("Port name - tty; Method name - that was a fault; Exception type - third.");
   }


   void testShortChunksFromSerialPort()
   {
      // TODO
   }


   void testFlowAfterExceptionWithLotsOfChunks()
   {
      // TODO
   }
}
