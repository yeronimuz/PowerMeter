package org.domiot.p1.utils;


import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetUtilsTest {

    @Test
    public void test() throws IOException {
        String macAddress = NetUtils.getMacAddress();
        String[] splits = macAddress.split(":");
        assertEquals(2, macAddress.indexOf(":"));
        assertEquals(6, splits.length);
    }
}
