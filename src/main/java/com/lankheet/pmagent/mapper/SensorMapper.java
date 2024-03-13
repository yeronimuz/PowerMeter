package com.lankheet.pmagent.mapper;

import com.lankheet.pmagent.config.SensorConfig;
import org.lankheet.domiot.domotics.dto.MqttTopicDto;
import org.lankheet.domiot.domotics.dto.SensorDto;

public class SensorMapper {
    private SensorMapper() {
    }

    public static SensorDto map(SensorConfig sensorConfig) {
        return SensorDto.builder()
                .sensorType(sensorConfig.getSensorType())
                .mqttTopic(MqttTopicDto.builder()
                        .type(sensorConfig.getMqttTopicConfig().getTopicType())
                        .path(sensorConfig.getMqttTopicConfig().getTopic())
                        .build())
                .build();
    }
}
