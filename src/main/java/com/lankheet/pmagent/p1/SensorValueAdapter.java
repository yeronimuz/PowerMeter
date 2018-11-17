package com.lankheet.pmagent.p1;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.MeasurementType;

/**
 * Converts a P1 SensorValue to single SensorValues<BR>
 * P1 contains multiple SensorValues (T1 prod & cons, T2 prod & cons, gas)
 */
public class SensorValueAdapter {

    /**
     * Takes one datagram and converts it into one or more single SensorValues <BR>
     * 
     * @param sensorId The configured sensorId that generated the SensorValue
     * @param datagram The datagram to convert
     * @return A list with single SensorValues
     */
    public static List<SensorValue> convertP1Datagram(SensorNode sensorNode, P1Datagram datagram) {
        Date ts = Date.from(datagram.getDateTimeStamp().atZone(ZoneId.systemDefault()).toInstant());
        List<SensorValue> SensorValuesList = new ArrayList<SensorValue>() {
            {
                // TODO: It should be possible to filter what needs to be sent. Now it's
                // hard coded
                // TODO: Repeated measurements should be filtered out

                add(new SensorValue(sensorNode, ts, MeasurementType.CONSUMED_POWER_T1.getId(),
                        datagram.getConsumedPowerTariff1()));
                add(new SensorValue(sensorNode, ts, MeasurementType.PRODUCED_POWER_T1.getId(),
                        datagram.getProducedPowerTariff1()));
                add(new SensorValue(sensorNode, ts, MeasurementType.CONSUMED_POWER_T2.getId(),
                        datagram.getConsumedPowerTariff2()));
                add(new SensorValue(sensorNode, ts, MeasurementType.PRODUCED_POWER_T2.getId(),
                        datagram.getProducedPowerTariff2()));
                add(new SensorValue(sensorNode, ts, MeasurementType.ACTUAL_CONSUMED_POWER.getId(),
                        datagram.getCurrentConsumedPwr()));
                add(new SensorValue(sensorNode, ts, MeasurementType.ACTUAL_PRODUCED_POWER.getId(),
                        datagram.getCurrentDeliveredPwr()));
                add(new SensorValue(sensorNode, ts, MeasurementType.CONSUMED_GAS.getId(), datagram.getConsumedGas()));
            }
        };

        return SensorValuesList;
    }
}
