package org.domiot.p1.pmagent.mapper;

import org.domiot.p1.pmagent.config.SensorConfig;
import org.lankheet.domiot.domotics.dto.MqttTopicDto;
import org.lankheet.domiot.domotics.dto.SensorDto;

/**
 * Map Sensor configuration class
 */
public class SensorMapper {

    private SensorMapper() {
    }

    /**
     * Map sensor configuration to Sensor DTO
     *
     * @param sensorConfig The configuration of the sensor
     * @return The configured Sensor DTO
     */
    public static SensorDto map(SensorConfig sensorConfig) {
        return SensorDto.builder()
                .sensorType(sensorConfig.getSensorType())
                .mqttTopic(MqttTopicDto.builder()
                        .type(sensorConfig.getMqttTopicConfig().getTopicType())
                        .path(sensorConfig.getMqttTopicConfig().getTopic())
                        .build())
                .parameters(DomiotParameterMapper.mapList(sensorConfig.getParameters()))
                .build();
    }
}
