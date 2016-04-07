package com.lankheet.pmagent;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {
	
	@NotEmpty
    private String serialPort = "/dev/ttyUSB0";

	@JsonProperty
    public String getSerialPort() {
        return serialPort;
    }
	
	@JsonProperty
	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}
}
