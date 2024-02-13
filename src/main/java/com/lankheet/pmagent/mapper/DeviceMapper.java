package com.lankheet.pmagent.mapper;

import com.lankheet.pmagent.config.DeviceConfig;
import com.lankheet.utils.NetUtils;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.DomiotParameterDto;

import java.net.SocketException;

/**
 * Maps a Device config to a Device DTO
 */
public class DeviceMapper {

    private DeviceMapper() {
    }

    public static DeviceDto map(DeviceConfig deviceConfig) throws SocketException {
        return new DeviceDto()
                .hardwareVersion("RaspberryPi B+")
                .macAddress(NetUtils.getMacAddress(deviceConfig.getNic()))
                .sensors(deviceConfig.getSensorConfigs().stream().map(SensorMapper::map).toList())
                .addParameter(new DomiotParameterDto()
                        .name("internalQueueSize")
                        .parameterType("NUMBER")
                        .value(deviceConfig.getInternalQueueSize()))
                .addParameter(new DomiotParameterDto()
                        .name("repeatValuesAfter")
                        .parameterType("NUMBER")
                        .value(deviceConfig.getRepeatValuesAfter()));
    }
}
