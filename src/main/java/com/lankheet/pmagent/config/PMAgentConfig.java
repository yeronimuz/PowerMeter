package com.lankheet.pmagent.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {

	@Valid
    @NotNull
    @JsonProperty
    private SensorConfig sensorConfig = new SensorConfig();
	
    @Valid
    @NotNull
    @JsonProperty
    private SerialPortConfig serialPort = new SerialPortConfig();
    
    @Valid
    @NotNull
    @JsonProperty
    private MqttConfig mqttConfig = new MqttConfig();
    
    public SerialPortConfig getSerialPortConfig() {
        return serialPort;
    }

    public void setSerialPortFactory(SerialPortConfig factory) {
        this.serialPort = factory;
    }

    public MqttConfig getMqttConfig() {
    	return mqttConfig;
    }
    
    public SensorConfig getSensorConfig() {
    	return sensorConfig;
    }
    
}
