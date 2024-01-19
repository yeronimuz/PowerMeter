package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.utils.NetUtils;
import lombok.Data;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.Sensor;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceConfig {
    @JsonProperty
    private String nic;

    @JsonProperty
    List<SensorConfig> sensorConfigs;

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
            sensor.setType(sensorConfig.getType());
            sensorList.add(sensor);
        }
        return sensorList;
    }

}
