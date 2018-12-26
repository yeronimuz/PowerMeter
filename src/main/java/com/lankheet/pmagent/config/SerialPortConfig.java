package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SerialPortConfig {
    private String p1Key; // /XMX5LGBBFG1009021021


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

    @JsonProperty
    public String getP1Key() {
        return p1Key;
    }

    @JsonProperty
    public void setP1Key(String p1Key) {
        this.p1Key = p1Key;
    }
}
