package com.lankheet.pmagent.mapper;

import com.lankheet.pmagent.config.SensorConfig;
import org.lankheet.domiot.model.MqttTopic;
import org.lankheet.domiot.model.Sensor;

public class SensorMapper {
    private SensorMapper() {
    }

    public static Sensor map(SensorConfig sensorConfig) {
        return new Sensor()
                .type(sensorConfig.getSensorType())
                .name(sensorConfig.getName())
                .description(sensorConfig.getDescription())
                .mqttTopic(new MqttTopic().path(sensorConfig.getMqttTopicConfig().getTopic()));
    }
}
