package com.lankheet.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Network utilities.
 */
@Slf4j
public class NetUtils {
    /**
     * Get the local MAC address. eth0 = b8:27:eb:59:50:fb (pidora)
     *
     * @return The MAC address in the format AA:BB:CC:DD:EE:FF
     * @throws UnknownHostException Unknown localhost
     * @throws SocketException      Could not open socket to localhost
     */
    public static String getMacAddress(String itfName)
            throws SocketException {
        String macAddress;
        StringBuilder sb1 = new StringBuilder();
        for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (itfName.equals(networkInterface.getDisplayName())) {
                log.debug("Get MAC address {}", itfName);
                byte[] mac = networkInterface.getHardwareAddress();
                sb1 = new StringBuilder();
                int i = 0;
                for (byte b : mac) {
                    sb1.append(String.format("%02X%s", b, (i++ < mac.length - 1) ? ":" : ""));
                }
                break;
            }
        }
        macAddress = sb1.toString();
        log.debug("MAC address: {}", macAddress);
        return macAddress;
    }
}
