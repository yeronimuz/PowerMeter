package com.lankheet.pmagent.mapper;

import com.lankheet.pmagent.config.DeviceConfig;
import com.lankheet.utils.NetUtils;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.DomiotParameter;

import java.net.SocketException;

/**
 * Maps a Device config to a Device DTO
 */
public class DeviceMapper {

    private DeviceMapper() {
    }

    public static Device map(DeviceConfig deviceConfig) throws SocketException {
        return new Device()
                .hardwareVersion("RaspberryPi B+")
                .macAddress(NetUtils.getMacAddress(deviceConfig.getNic()))
                .sensors(deviceConfig.getSensorConfigs().stream().map(SensorMapper::map).toList())
                .addParametersItem(new DomiotParameter()
                        .name("internalQueueSize")
                        .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                        .value(deviceConfig.getInternalQueueSize()))
                .addParametersItem(new DomiotParameter()
                        .name("repeatValuesAfter")
                        .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                        .value(deviceConfig.getRepeatValuesAfter()));
    }
}
