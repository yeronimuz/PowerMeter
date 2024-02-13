package com.lankheet.pmagent.p1;

import lombok.extern.slf4j.Slf4j;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts a P1 SensorValue to single SensorValues<BR> P1 contains multiple SensorValues (T1 prod & cons, T2 prod & cons, gas)
 */
@Slf4j
public class DatagramToSensorValueMapper {

    private DatagramToSensorValueMapper() {
    }

    /**
     * Takes one datagram and converts it into one or more single SensorValues <BR>
     * The values to be included are determined by the sensor types in the device.
     *
     * @param device   The configured device that generated the SensorValue
     * @param datagram The datagram to convert
     * @return A list with SensorValues
     */
    public static List<SensorValueDto> convertP1Datagram(DeviceDto device, P1Datagram datagram) {
        List<SensorValueDto> sensorValueList = new ArrayList<>();
        LocalDateTime timestamp = LocalDateTime.now();
        for (SensorDto sensor : device.sensors()) {
            sensor.deviceMac(device.macAddress());
            sensorValueList.add(new SensorValueDto().sensor(sensor).timeStamp(timestamp).value(getValueFromDatagram(sensor, datagram)));
        }

        return sensorValueList;
    }

    private static double getValueFromDatagram(SensorDto sensor, P1Datagram datagram) {
        switch (sensor.sensorType()) {
            case POWER_PT1 -> {
                return datagram.getProducedPowerTariff1();
            }
            case POWER_PT2 -> {
                return datagram.getProducedPowerTariff2();
            }
            case POWER_CT1 -> {
                return datagram.getConsumedPowerTariff1();
            }
            case POWER_CT2 -> {
                return datagram.getConsumedPowerTariff2();
            }
            case POWER_AC -> {
                return datagram.getActualConsumedPwr();
            }
            case POWER_AP -> {
                return datagram.getActualDeliveredPwr();
            }
            case GAS_METER -> {
                return datagram.getConsumedGas();
            }
            case TEMP, HUMID, WATER, GAS_SENSOR, NOT_USED, HYDRO, STATUS, VOLTAGE_LEVEL, CURRENT_LEVEL -> log.error("No such sensorType for P1 device: {}", sensor.sensorType());

            default -> log.error("Current sensorType unknown for the current API: {}", sensor.sensorType());

        }
        return Double.MAX_VALUE;
    }
}
