package org.domiot.p1.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

/**
 * Network utilities.
 */
@Slf4j
public class NetUtils {
    /**
     * Get the local MAC address.
     *
     * @return The MAC address in the format AA:BB:CC:DD:EE:FF
     * @throws SocketException Could not open socket to localhost
     */
    public static String getMacAddress()
            throws SocketException {
        String macAddress;
        StringBuilder sb1 = new StringBuilder();
        for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            log.debug(networkInterface.getDisplayName());
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null) {
                sb1 = new StringBuilder();
                int i = 0;
                for (byte b : mac) {
                    sb1.append(String.format("%02X%s", b, (i++ < mac.length - 1) ? ":" : ""));
                }
                log.info("Found mac address {} at {}", sb1, networkInterface.getDisplayName());
                break;
            }
        }
        macAddress = sb1.toString();
        log.debug("MAC address: {}", macAddress);
        return macAddress;
    }
}
