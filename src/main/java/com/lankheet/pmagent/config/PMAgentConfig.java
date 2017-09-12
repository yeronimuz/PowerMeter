package com.lankheet.pmagent.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private SerialPortConfig serialPort = new SerialPortConfig();
    
    @Valid
    @NotNull
    @JsonProperty
    private MqttConfig mqqtConfig = new MqttConfig();
    
	//    @JsonProperty("serialPort")
    public SerialPortConfig getSerialPortConfig() {
        return serialPort;
    }

//    @JsonProperty("serialPort")
    public void setSerialPortFactory(SerialPortConfig factory) {
        this.serialPort = factory;
    }

    public MqttConfig getMqqtConfig() {
    	return mqqtConfig;
    }
    
    public void setMqqtConfig(MqttConfig mqqtConfig) {
    	this.mqqtConfig = mqqtConfig;
    }
}
