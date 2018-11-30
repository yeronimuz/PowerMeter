package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PMAgentConfig {

    @JsonProperty
    private SensorConfig sensorConfig = new SensorConfig();
	
    @JsonProperty
    private SerialPortConfig serialPortConfig = new SerialPortConfig();
    
    @JsonProperty
    private MqttConfig mqttConfig = new MqttConfig();
    
    /**
     * Get serialPort.
     * @return the serialPort
     */
    public SerialPortConfig getSerialPortConfig() {
        return serialPortConfig;
    }

    /**
     * Set serialPort.
     * @param serialPort the serialPort to set
     */
    public void setSerialPort(SerialPortConfig serialPort) {
        this.serialPortConfig = serialPort;
    }

    /**
     * Set sensorConfig.
     * @param sensorConfig the sensorConfig to set
     */
    public void setSensorConfig(SensorConfig sensorConfig) {
        this.sensorConfig = sensorConfig;
    }

    /**
     * Get mqttConfig.
     * @return the mqttConfig
     */
    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    /**
     * Set mqttConfig.
     * @param mqttConfig the mqttConfig to set
     */
    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    /**
     * Get sensorConfig.
     * @return the sensorConfig
     */
    public SensorConfig getSensorConfig() {
        return sensorConfig;
    }

    /**
     * Set serialPortConfig.
     * @param serialPortConfig the serialPortConfig to set
     */
    public void setSerialPortConfig(SerialPortConfig serialPortConfig) {
        this.serialPortConfig = serialPortConfig;
    }
    
}
