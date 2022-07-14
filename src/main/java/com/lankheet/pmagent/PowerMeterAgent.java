package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * The service reads datagrams from the P1 serial interface<br> It sends a series of SensorValue objects to an MQTT broker<br> The serial port reader is
 * separated from the sending thread and coupled via a queue.<br> The capacity of the queue is QUEUE_SIZE / (VALUE_LOOP * NR_VALUES_PER_LOOP *
 * LOOPS_IN_MINUTE)<br> which is 25 minutes with 7 new measurements in a 10 seconds value loop.<br> So there is at least half an hour time when the MQTT
 * connection is dropped for re-establishing.
 */
public class PowerMeterAgent
{
   private static final Logger LOG = LoggerFactory.getLogger(PowerMeterAgent.class);


   public static void main(String[] args)
      throws Exception
   {
      InputStream is = PowerMeterAgent.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
      Manifest manifest = new Manifest(is);
      Attributes mainAttrs = manifest.getMainAttributes();
      String title = mainAttrs.getValue("Implementation-Title");
      String version = mainAttrs.getValue("Implementation-Version");
      String classifier = mainAttrs.getValue("Implementation-Classifier");

      showBanner();
      LOG.info("Starting {}: {}-{}", title, version, classifier);
      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         @Override
         public void run()
         {
            LOG.warn("Shutdown Hook is cleaning up!");
         }
      });
      if (args.length < 1)
      {
         showUsage(version, classifier);
         return;
      }
      new PowerMeterAgent().run(args[0]);
   }


   private static void showBanner()
   {
      String text = new Scanner(Objects.requireNonNull(PowerMeterAgent.class.getResourceAsStream("/banner.txt")), "UTF-8").useDelimiter("\\A").next();
      LOG.info(text);
   }


   private static void showUsage(String version, String classifier)
   {
      System.out.println("Missing configuration file!");
      System.out.println("Usage:");
      System.out.println("java -jar lnb-powermeter-" + version + "-" + classifier + " config.yml");
   }


   public void run(String configFileName)
      throws IOException

   {
      PMAgentConfig configuration = PMAgentConfig.loadConfigurationFromFile(configFileName);
      LOG.info("Configuration: {}", configuration);
      BlockingQueue<SensorValue> queue = new ArrayBlockingQueue<>(configuration.getInternalQueueSize());

      MqttConfig mqttConfig = configuration.getMqttConfig();

      final String nic = configuration.getSensorConfig().getNic();
      SensorNode sensorNode = new SensorNode(NetUtils.getMacAddress(nic), SensorType.POWER_METER.getId());

      new Thread(new SensorValueSender(queue, mqttConfig)).start();

      SerialPortReader serialPortReader = new SerialPortReader(queue, configuration.getSerialPortConfig(), sensorNode);
      new Thread(serialPortReader).start();
   }
}
