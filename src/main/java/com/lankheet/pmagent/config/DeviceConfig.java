package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.utils.NetUtils;
import lombok.Data;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.MqttTopic;
import org.lankheet.domiot.model.Sensor;

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

    public Device toDevice() throws SocketException {
        Device device = new Device();
        device.setMacAddress(NetUtils.getMacAddress(nic));
        device.setSensors(toSensorList());
        return device;
    }

    List<Sensor> toSensorList() {
        List<Sensor> sensorList = new ArrayList<>();
        for (SensorConfig sensorConfig: sensorConfigs) {
            Sensor sensor = new Sensor();
            sensor.setType(sensorConfig.getSensorType());
            sensor.setName(sensorConfig.getName());
            sensor.setDescription(sensorConfig.getDescription());
            // MqttConfigType not used
            sensor.setMqttTopic(new MqttTopic().path(sensorConfig.getMqttTopicConfig().getTopic()));
            sensorList.add(sensor);
        }
        return sensorList;
    }

}
