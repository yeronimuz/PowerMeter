/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.domiot.p1.pmagent.p1;

import org.junit.jupiter.api.Test;
import org.lankheet.domiot.utils.JsonUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class P1ParserTest {

    @Test
    void testHome1()
            throws IOException, URISyntaxException {
        String input = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("/p1_2.log")).toURI())));
        System.out.println(input);

        P1Datagram dg = P1Parser.parse(input);
        assertEquals(207.138, dg.getConsumedPowerTariff1());
        assertEquals(269.06, dg.getConsumedPowerTariff2());
        assertEquals(27.545, dg.getProducedPowerTariff1());
        assertEquals(74.828, dg.getProducedPowerTariff2());
        assertEquals(86.298, dg.getConsumedGas());
        assertEquals(0.984, dg.getActualConsumedPwr());
        assertEquals(0.0, dg.getActualDeliveredPwr());
        assertEquals((byte) 2, dg.getCurrentTariff());

        String jsonString = JsonUtil.toJson(dg);
        System.out.println(jsonString);
    }

    @Test
    void testHome2()
            throws IOException, URISyntaxException {
        String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1v2.log").toURI())));
        System.out.println(input);

        P1Datagram dg = P1Parser.parse(input);
        assertEquals(1779.182, dg.getConsumedPowerTariff1());
        assertEquals(2180.316, dg.getConsumedPowerTariff2());
        assertEquals(967.32, dg.getProducedPowerTariff1());
        assertEquals(1994.968, dg.getProducedPowerTariff2());
        assertEquals(1265.379, dg.getConsumedGas());
        assertEquals(0.0, dg.getActualConsumedPwr());
        assertEquals(0.49, dg.getActualDeliveredPwr());
        assertEquals((byte) 2, dg.getCurrentTariff());

        String jsonString = JsonUtil.toJson(dg);
        System.out.println(jsonString);
    }

    @Test
    void testAnotherHome()
            throws IOException, URISyntaxException {
        String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1w.cap").toURI())));
        System.out.println(input);

        P1Datagram dg = P1Parser.parse(input);
        assertEquals(2527.633, dg.getConsumedPowerTariff1());
        assertEquals(3108.891, dg.getConsumedPowerTariff2());
        assertEquals(0.0, dg.getProducedPowerTariff1());
        assertEquals(0.0, dg.getProducedPowerTariff2());
        assertEquals(3561.545, dg.getConsumedGas());
        assertEquals(0.176, dg.getActualConsumedPwr());
        assertEquals(0.0, dg.getActualDeliveredPwr());
        assertEquals((byte) 1, dg.getCurrentTariff());

        String jsonString = JsonUtil.toJson(dg);
        System.out.println(jsonString);
    }

    @Test
    void TestDouble() {
        assertEquals(2527.633, Double.parseDouble("2527.633"));
    }
}
