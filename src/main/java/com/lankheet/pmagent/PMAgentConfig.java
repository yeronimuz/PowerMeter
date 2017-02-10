package com.lankheet.pmagent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PMAgentConfig extends Configuration {
    @Valid
    @NotNull
    private SerialPortConfig SerialPort = new SerialPortConfig();
    
    @Valid
    @NotNull
    private LocalStorageConfig storageConfig = new LocalStorageConfig();
    
    @JsonProperty("storageConfig")
    public LocalStorageConfig getLocalStorageConfig() {
    	return storageConfig;
    }

    @JsonProperty("storageConfig")
    public void setLocalStorageConfig(LocalStorageConfig storageConfig) {
    	this.storageConfig = storageConfig;
    }

    @JsonProperty("serialPort")
    public SerialPortConfig getSerialPortConfig() {
        return SerialPort;
    }

    @JsonProperty("serialPort")
    public void setSerialPortFactory(SerialPortConfig factory) {
        this.SerialPort = factory;
    }

    /**
     * Return a filename based on a specified pattern
     * @param pattern {@link DateTimeFormatter}
     * @return String containing fileName without extension
     */
	public String getFileNameFromPattern(String pattern) {
		LocalDate date = LocalDate.now();
		DateTimeFormatter fmt =  DateTimeFormatter.ofPattern(pattern);
		return date.format(fmt);
	}
}
