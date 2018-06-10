package com.lankheet.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import org.junit.Test;

public class NetUtilsTest {

    @Test
    public void test() throws IOException {
        // TODO: Mock the system calls for this test with JMockit
        String macAddress = NetUtils.getMacAddress("wlo1");
        String[] splits = macAddress.split(":");
        assertThat(macAddress.indexOf(":"), is(2));
        assertThat(splits.length, is(6));
    }
}
