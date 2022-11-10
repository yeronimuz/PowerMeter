package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.config.SerialPortConfig;
import gherkin.lexer.Th;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class P1ReaderTest
{
   @InjectMocks
   private P1Reader p1Reader;

   @Mock
   private BlockingQueue<SensorValue> queue;
   @Mock
   private Logger                     logger;


   @Test
   void testOneDatagram(
      @Mock BlockingQueue<SensorValue> queue,
      @Mock SensorNode sensorNode,
      @Mock BufferedReader bufferedReader)

      throws InterruptedException, IOException
   {
      when(bufferedReader.readLine()).thenReturn(
         "/ISK5\\2M550T-1013\n"
         , "1-3:0.2.8(50)\n"
         , "0-0:1.0.0(220601161852S)\n"
         , "0-0:96.1.1(4530303534303037363939333832353230)\n"
         , "1-0:1.8.1(001779.182*kWh)\n"
         , "1-0:1.8.2(002180.316*kWh)\n"
         , "1-0:2.8.1(000967.320*kWh)\n"
         , "1-0:2.8.2(001994.968*kWh)\n"
         , "0-0:96.14.0(0002)\n"
         , "1-0:1.7.0(00.000*kW)\n"
         , "1-0:2.7.0(00.490*kW)\n"
         , "0-0:96.7.21(00005)\n"
         , "0-0:96.7.9(00004)\n"
         , "1-0:99.97.0(2)(0-0:96.7.19)(211109105421W)(0000004239*s)(220519205327S)(0000011498*s)\n"
         , "1-0:32.32.0(00002)\n"
         , "1-0:52.32.0(00003)\n"
         , "1-0:72.32.0(00001)\n"
         , "1-0:32.36.0(00001)\n"
         , "1-0:52.36.0(00001)\n"
         , "1-0:72.36.0(00001)\n"
         , "0-0:96.13.0()\n"
         , "1-0:32.7.0(231.1*V)\n"
         , "1-0:52.7.0(229.5*V)\n"
         , "1-0:72.7.0(231.3*V)\n"
         , "1-0:31.7.0(000*A)\n"
         , "1-0:51.7.0(001*A)\n"
         , "1-0:71.7.0(003*A)\n"
         , "1-0:21.7.0(00.000*kW)\n"
         , "1-0:41.7.0(00.266*kW)\n"
         , "1-0:61.7.0(00.000*kW)\n"
         , "1-0:22.7.0(00.000*kW)\n"
         , "1-0:42.7.0(00.000*kW)\n"
         , "1-0:62.7.0(00.757*kW)\n"
         , "0-1:24.1.0(003)\n"
         , "0-1:96.1.0(4730303738353635353936353235323230)\n"
         , "0-1:24.2.1(220601161501S)(01265.379*m3)\n"
         , "!291A\n");

      p1Reader = new P1Reader(queue, "/ISK5\\2M550T-1013", sensorNode, bufferedReader);
      Thread p1Thread = new Thread(p1Reader);
      p1Thread.start();
      Thread.sleep(500);
      p1Reader.stop();

      verify(queue, times(7)).put(any(SensorValue.class));
      verify(logger, times(0)).error(anyString());
   }
}