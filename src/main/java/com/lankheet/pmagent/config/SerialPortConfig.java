package com.lankheet.pmagent.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SerialPortConfig {
	@NotEmpty
	private String serialPort = "/dev/ttyUSB0";

	@NotNull
	private Integer baudRate = 115200;

	@JsonProperty
	public String getUart() {
		return serialPort;
	}

	@JsonProperty
	public void setUart(String serialPort) {
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
