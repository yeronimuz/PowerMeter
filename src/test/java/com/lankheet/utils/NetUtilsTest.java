package com.lankheet.utils;

import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class NetUtilsTest {

    @Test
    public void test() throws IOException {
        String macAddress = NetUtils.getMacAddress("wlo1");
        String[] splits = macAddress.split(":");
        assertEquals(2, macAddress.indexOf(":"));
        assertEquals(6, splits.length);
    }
}
