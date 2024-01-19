package com.lankheet.pmagent.mapper;

import com.lankheet.pmagent.config.SensorConfig;
import org.lankheet.domiot.model.MqttTopic;
import org.lankheet.domiot.model.Sensor;

public class SensorMapper {
    private SensorMapper() {
    }

    public static Sensor map(SensorConfig sensorConfig) {
        return new Sensor()
                .type(sensorConfig.getType())
                .mqttTopic(new MqttTopic().path(sensorConfig.getMqttTopic()));
    }
}
