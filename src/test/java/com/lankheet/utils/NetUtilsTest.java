package com.lankheet.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

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
