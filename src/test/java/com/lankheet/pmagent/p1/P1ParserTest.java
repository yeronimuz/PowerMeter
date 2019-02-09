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

package com.lankheet.pmagent.p1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import com.lankheet.utils.JsonUtil;

public class P1ParserTest {

	@Test
	public void testHome1() throws IOException, URISyntaxException {
		String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1_2.log").toURI())));
		System.out.println(input);
		
		P1Datagram dg = P1Parser.parse(input);
		assertThat(dg.getConsumedPowerTariff1(), is(equalTo(207.138)));
		assertThat(dg.getConsumedPowerTariff2(), is(equalTo(269.06)));
		assertThat(dg.getProducedPowerTariff1(), is(equalTo(27.545)));
		assertThat(dg.getProducedPowerTariff2(), is(equalTo(74.828)));
		assertThat(dg.getConsumedGas(), is(equalTo(86.298)));
		assertThat(dg.getActualConsumedPwr(), is(equalTo(0.984)));
		assertThat(dg.getActualDeliveredPwr(), is(equalTo(0.0)));
		assertThat(dg.getCurrentTariff(), is(equalTo((byte)2)));
		
		String jsonString = JsonUtil.toJson(dg);
		System.out.println(jsonString);
	}

    @Test
    public void testAnotherHome() throws IOException, URISyntaxException {
        String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1w.cap").toURI())));
        System.out.println(input);
        
        P1Datagram dg = P1Parser.parse(input);
        assertThat(dg.getConsumedPowerTariff1(), is(2527.633));
        assertThat(dg.getConsumedPowerTariff2(), is(3108.891));
        assertThat(dg.getProducedPowerTariff1(), is(0.0));
        assertThat(dg.getProducedPowerTariff2(), is(0.0));
        assertThat(dg.getConsumedGas(), is(3561.545));
        assertThat(dg.getActualConsumedPwr(), is(0.176));
        assertThat(dg.getActualDeliveredPwr(), is(0.0));
        assertThat(dg.getCurrentTariff(), is((byte)1));
        
        String jsonString = JsonUtil.toJson(dg);
        System.out.println(jsonString);
    }
    
    @Test
    public void TestDouble() {
        String doubleToParse = "2527.633";
        double dVal = Double.parseDouble(doubleToParse);
        assertThat(dVal, is(2527.633));
    }
}
