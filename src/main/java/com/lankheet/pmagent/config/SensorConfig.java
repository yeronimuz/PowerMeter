package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lankheet.domiot.model.SensorType;

import java.util.Arrays;

@Data
public class SensorConfig {
    @JsonProperty
    private String name;

    @JsonProperty
    private SensorType sensorType;

    @JsonProperty
    private MqttTopicConfig mqttTopicConfig;

    @JsonProperty
    private String description;

    SensorType of(String value) {
        return Arrays.stream(SensorType.values()).
                filter(type -> type.name().equals(value)).
                findFirst().orElse(null);
    }
}
