package com.lankheet.utils;

import java.io.IOException;
import org.junit.Test;

public class NetUtilsTest {

    @Test
    public void test() throws IOException {
        System.out.println(NetUtils.getMacAddress("wlo1"));
    }

}
