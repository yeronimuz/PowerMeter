package com.lankheet.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Network utilities.
 */
public class NetUtils {
    /**
     * Get the local MAC address. eth0 = b8:27:eb:59:50:fb (pidora)
     * 
     * @return The MAC address in the format AA:BB:CC:DD:EE:FF
     * @throws UnknownHostException Unknown localhost
     * @throws SocketException Could not open socket to localhost
     */
    public static String getMacAddress(String itfName) throws UnknownHostException, SocketException {
        InetAddress ip = InetAddress.getLocalHost();
        String macAddress = null;
        StringBuilder sb1 = null;
        for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (itfName.equals(networkInterface.getDisplayName())) {
                byte[] mac = networkInterface.getHardwareAddress();
                sb1 = new StringBuilder();
                int i = 0;
                for (byte b : mac) {
                    sb1.append(String.format("%02X%s", b, (i++ < mac.length - 1) ? ":" : ""));
                }
            }
        }
        macAddress = sb1.toString();
        return macAddress;
    }

}
