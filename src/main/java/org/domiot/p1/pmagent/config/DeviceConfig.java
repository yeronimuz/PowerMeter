package org.domiot.p1.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.domiot.p1.utils.NetUtils;
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
        String macAddress = NetUtils.getMacAddress();
        return DeviceDto.builder()
                .macAddress(macAddress)
                .sensors(toSensorList(macAddress))
                .build();
    }

    List<SensorDto> toSensorList(String macAddress) {
        List<SensorDto> sensorList = new ArrayList<>();
        for (SensorConfig sensorConfig : sensorConfigs) {
            SensorDto sensor = SensorDto.builder()
                    .sensorType(sensorConfig.getSensorType())
                    .deviceMac(macAddress)
                    // MqttConfigType not used
                    .mqttTopic(MqttTopicDto.builder()
                            .path(sensorConfig.getMqttTopicConfig().getTopic())
                            .type(sensorConfig.getMqttTopicConfig().getTopicType())
                            .build())
                    .build();
            sensorList.add(sensor);
        }
        return sensorList;
    }

}
