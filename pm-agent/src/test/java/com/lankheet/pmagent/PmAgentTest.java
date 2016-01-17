package com.lankheet.pmagent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class PmAgentTest {

	@Test
	public void test() throws IOException, URISyntaxException {
		String input = new String(Files.readAllBytes(Paths.get(getClass().getResource("/p1.log").toURI())));
		System.out.println(input);
	}

}
