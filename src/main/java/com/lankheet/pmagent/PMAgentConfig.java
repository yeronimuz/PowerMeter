package com.lankheet.pmagent;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {
    @Valid
    @NotNull
    private SerialPortConfig SerialPort = new SerialPortConfig();

    @JsonProperty("serialPort")
    public SerialPortConfig getSerialPortConfig() {
        return SerialPort;
    }

    @JsonProperty("serialPort")
    public void setSerialPortFactory(SerialPortConfig factory) {
        this.SerialPort = factory;
    }

}
