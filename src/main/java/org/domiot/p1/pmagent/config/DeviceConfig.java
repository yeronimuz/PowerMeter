package org.domiot.p1.pmagent.config;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DeviceConfig {
    private static final int DEFAULT_INTERNALQUEUE_SIZE = 100000;
    @JsonProperty
    private SerialPortConfig serialPort = new SerialPortConfig();

    @JsonProperty
    private MqttConfig mqttBroker = new MqttConfig();

    @JsonProperty
    private List<ConfigParameter> deviceParameters;

    @JsonProperty
    private List<SensorConfig> sensorConfigs;

    public int getInternalQueueSize() {
        Optional<ConfigParameter> internalQueueSizeOptional = deviceParameters
                .stream()
                .filter(configParameter -> configParameter.getName().equals("internalQueueSize"))
                .findFirst();

        return internalQueueSizeOptional.isPresent()
                ? ((Integer) internalQueueSizeOptional.get().getValue())
                : DEFAULT_INTERNALQUEUE_SIZE;
    }

}
