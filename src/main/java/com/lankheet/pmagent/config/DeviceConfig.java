package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.utils.NetUtils;
import lombok.Data;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.MqttTopicDto;
import org.lankheet.domiot.domotics.dto.SensorDto;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceConfig {
    @JsonProperty
    private long repeatValuesAfter;

    @JsonProperty
    private int internalQueueSize;

    @JsonProperty
    private String nic;

    @JsonProperty
    private SerialPortConfig serialPort = new SerialPortConfig();

    @JsonProperty
    private MqttConfig mqttBroker = new MqttConfig();

    @JsonProperty
    private List<SensorConfig> sensorConfigs;

    public DeviceDto toDevice() throws SocketException {
        String macAddress = NetUtils.getMacAddress(nic);
        return new DeviceDto()
                .macAddress(macAddress)
                .sensors(toSensorList(macAddress));
    }

    List<SensorDto> toSensorList(String macAddress) {
        List<SensorDto> sensorList = new ArrayList<>();
        for (SensorConfig sensorConfig : sensorConfigs) {
            SensorDto sensor = new SensorDto()
                    .sensorType(sensorConfig.getSensorType())
                    .deviceMac(macAddress)
                    // MqttConfigType not used
                    .mqttTopic(new MqttTopicDto()
                            .path(sensorConfig.getMqttTopicConfig().getTopic())
                            .type(sensorConfig.getMqttTopicConfig().getTopicType()));
            sensorList.add(sensor);
        }
        return sensorList;
    }

}
