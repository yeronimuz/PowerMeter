package com.lankheet.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Network utilities.
 */
public class NetUtils
{
   private static final Logger LOG = LoggerFactory.getLogger(NetUtils.class);


   /**
    * Get the local MAC address. eth0 = b8:27:eb:59:50:fb (pidora)
    *
    * @return The MAC address in the format AA:BB:CC:DD:EE:FF
    * @throws UnknownHostException Unknown localhost
    * @throws SocketException      Could not open socket to localhost
    */
   public static String getMacAddress(String itfName)
      throws SocketException
   {
      String macAddress;
      StringBuilder sb1 = new StringBuilder();
      for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces()))
      {
         if (itfName.equals(networkInterface.getDisplayName()))
         {
            LOG.debug("Get MAC address {}", itfName);
            byte[] mac = networkInterface.getHardwareAddress();
            sb1 = new StringBuilder();
            int i = 0;
            for (byte b : mac)
            {
               sb1.append(String.format("%02X%s", b, (i++ < mac.length - 1) ? ":" : ""));
            }
            break;
         }
      }
      macAddress = sb1.toString();
      LOG.debug("MAC address: {}", macAddress);
      return macAddress;
   }
}
