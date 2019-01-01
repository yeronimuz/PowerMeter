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

    /**
     * Worker method for this class.
     * @param sensorValue The sensor value that is to be inspected
     * 
     * @return true: The value was already processed, false: new value
     */
    public boolean isRepeatedValue(SensorValue sensorValue) {
        boolean isRepeated = false;
        int indexToBeReplaced = -1;
        SensorNode sensorNode = sensorValue.getSensorNode();
        List<SensorValue> sensorValues = new ArrayList<>();

        if (!latch.isEmpty() && latch.containsKey(sensorValue.getSensorNode())) {
            sensorValues = latch.get(sensorNode);
            for (SensorValue sensorValueLatch : sensorValues) {
                isRepeated |= sensorValueLatch.equals(sensorValue);
                if(sensorValueLatch.equalsInType(sensorValue)) {
                    // Only value differs, store new value
                    indexToBeReplaced = sensorValues.indexOf(sensorValueLatch);
                }
            }
            if (indexToBeReplaced != -1) {
                sensorValues.set(indexToBeReplaced, sensorValue);
            }
        }
        if (!isRepeated && indexToBeReplaced == -1) {
            sensorValues.add(sensorValue);
            latch.put(sensorNode, sensorValues);
        }
        LOG.debug(toString());
        return isRepeated;
    }

    /**
     * In order to be able to resend repeated values when requested, this reset is needed.
     */
    public void resetLatch() {
        latch = new HashMap<>();
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

    /**
     * Get latch.
     * @return the latch
     */
    public Map<SensorNode, List<SensorValue>> getLatch() {
        return latch;
    }
}
