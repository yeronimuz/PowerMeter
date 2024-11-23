package org.domiot.p1.pmagent.mapper;

import java.net.SocketException;

import org.domiot.p1.pmagent.config.DeviceConfig;
import org.domiot.p1.utils.NetUtils;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.DomiotParameterDto;

/**
 * Maps a Device config to a Device DTO
 */
public class DeviceMapper {

    private DeviceMapper() {
    }

    /**
     * Map device configuration to a Device DTO
     *
     * @param deviceConfig The device configuration
     * @return The device DTO object
     * @throws SocketException The active network interface list cannot be retrieved
     */
    public static DeviceDto map(DeviceConfig deviceConfig) throws SocketException {
        DeviceDto deviceDto = DeviceDto.builder()
                .hardwareVersion("RaspberryPi B+")
                .macAddress(NetUtils.getMacAddress())
                .sensors(deviceConfig.getSensorConfigs().stream().map(SensorMapper::map).toList()).build();
        deviceDto.addParameter(DomiotParameterDto.builder()
                        .name("internalQueueSize")
                        .parameterType("NUMBER")
                        .value(deviceConfig.getInternalQueueSize())
                        .build())
                .addParameter(DomiotParameterDto.builder()
                        .name("repeatValuesAfter")
                        .parameterType("NUMBER")
                        .value(deviceConfig.getRepeatValuesAfter()).build());
        return deviceDto;
    }
}
