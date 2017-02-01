package com.lankheet.pmagent;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {
	
	@NotEmpty
    private String serialPort = "/dev/ttyUSB0";
	
	@NotNull
	private Integer baudRate = 115200;
	
	@JsonProperty
    public String getSerialPort() {
        return serialPort;
    }
	
	@JsonProperty
	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}

	@JsonProperty
    public int getBaudRate() {
        return baudRate;
    }
	
	@JsonProperty
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}
}
