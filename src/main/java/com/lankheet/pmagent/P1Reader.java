package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.pmagent.p1.DatagramToSensorValueMapper;
import com.lankheet.pmagent.p1.P1Datagram;
import com.lankheet.pmagent.p1.P1Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class P1Reader implements Runnable
{
   private static final Logger                     LOG          = LoggerFactory.getLogger(P1Reader.class);
   private static final String                     STOP_TOKEN   = "!";
   private final        String                     powerMeterUniqueKey;
   private              String                     buffS        = "";
   private final        BlockingQueue<SensorValue> queue;
   private final        SensorNode                 sensorNode;
   private final        BufferedReader             serialBufReader;
   private final        AtomicBoolean                    continueFlag = new AtomicBoolean(true);


   public P1Reader(BlockingQueue<SensorValue> queue, String powerMeterUniqueKey, SensorNode sensorNode, BufferedReader serialBufReader)
   {
      this.queue = queue;
      this.powerMeterUniqueKey = powerMeterUniqueKey;
      this.sensorNode = sensorNode;
      this.serialBufReader = serialBufReader;
   }


   public void stop()
   {
      continueFlag.set(false);
   }


   @Override
   public void run()
   {
      String line;
      try
      {
         while (continueFlag.get())
         {
            line = this.serialBufReader.readLine();
            if (line != null && !line.isEmpty())
            {
               buffS = buffS.concat(line + "\n");
               if (line.startsWith(STOP_TOKEN))
               {
                  processP1Data(buffS);
                  buffS = "";
               }
            }
         }
      }
      catch (IOException e)
      {
         LOG.error("Unable to read data from serial port");
      }
   }


   private String processP1Data(String bufS)
   {
      int start = bufS.indexOf(powerMeterUniqueKey);
      int stop = bufS.indexOf(STOP_TOKEN); // Followed by 3 or 4 characters (checksum)

      if ((start >= 0) && (stop >= 0))
      {
         // Enough captured, parse the datagram part, save the remainder
         String stringToParse = bufS.substring(start, stop + 4);

         P1Datagram datagram = P1Parser.parse(stringToParse);
         publishDatagram(datagram);
         LOG.debug("Saved: {}", datagram);
         return bufS.substring(stop + 4); // sometimes only 3 chars.
         // CR/LF not taken into account
      }
      // else wait for more lines
      return bufS;
   }


   private void publishDatagram(P1Datagram datagram)
   {
      List<SensorValue> sensorValueList = DatagramToSensorValueMapper.convertP1Datagram(sensorNode, datagram);
      LOG.debug("Putting {} values in the queue...", sensorValueList.size());
      for (SensorValue sensorValue : sensorValueList)
      {
         try
         {
            queue.put(sensorValue);
         }
         catch (InterruptedException e)
         {
            LOG.error("Putting data in queue was interrupted");
         }
      }
   }
}
