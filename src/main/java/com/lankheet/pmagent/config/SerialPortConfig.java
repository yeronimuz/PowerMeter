package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SerialPortConfig {

    private String serialPort = "/dev/ttyUSB0";

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
