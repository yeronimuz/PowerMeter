/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


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
