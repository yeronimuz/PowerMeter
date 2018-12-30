package com.lankheet.pmagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;

/**
 * For each sensor value it will be evaluated whether it is a new or repeated value.<BR>
 * Only new values need to be processed.<BR>
 * For each sensor node, multiple {type, value} pairs can be stored (multi-sensor nodes).
 */
public class SensorValueCache {
    private static final Logger LOG = LoggerFactory.getLogger(SensorValueCache.class);

    private Map<SensorNode, List<SensorValue>> latch = new HashMap<>();

    public boolean isRepeatedValue(SensorValue sensorValue) {
        boolean isRepeated = false;
        SensorNode sensorNode = sensorValue.getSensorNode();
        List<SensorValue> sensorValues = new ArrayList<>();

        if (!latch.isEmpty() && latch.containsKey(sensorValue.getSensorNode())) {
            for (SensorValue sensorValueLatch : latch.get(sensorNode)) {
                isRepeated |= sensorValueLatch.equals(sensorValue);
            }
        }
        // WARNING: SensorNode may differ in subsequent calls
        if (!isRepeated) {
            sensorValues.add(sensorValue);
            latch.put(sensorNode, sensorValues);
        }
        LOG.debug(toString());
        return isRepeated;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        latch.keySet().forEach(node -> {
            builder.append(String.format("{mac = %s, type = %s} -> ", node.getSensorMac(), node.getSensorType()));
            latch.get(node).forEach(val -> {
                builder.append(String.format("{type = %d, value = %f}\n", val.getMeasurementType(), val.getValue()));
            });
        });
        return builder.toString();
    }
}
