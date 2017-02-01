package com.lankheet.pmagent.resources;

import java.io.IOException;
import java.util.jar.Manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is a resource for providing manifest details of the application
 * @author jeroen
 *
 */
public class AboutPMAgent {
	
	private Manifest manifest;
	
	public AboutPMAgent() throws IOException {
		manifest = new Manifest();
		manifest.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
	}

	@JsonProperty
	public String getApplicationTitle() {
		return manifest.getMainAttributes().getValue("Implementation-Title");
	}

	@JsonProperty
	public String getVersion() {
		return manifest.getMainAttributes().getValue("Implementation-Version");
	}
}
