package org.domiot.p1.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.lankheet.domiot.model.SensorType;

import java.util.Arrays;

@Data
public class SensorConfig {
    @JsonProperty
    private long sensorId;

    @JsonProperty
    private SensorTypeDto sensorType;

    @JsonProperty
    private MqttTopicConfig mqttTopicConfig;

    @JsonProperty
    private String description;

    SensorType of(String typeString) {
        return Arrays.stream(SensorType.values()).
                filter(type -> type.name().equals(typeString)).
                findFirst().orElse(null);
    }
}
