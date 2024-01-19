package com.lankheet.pmagent;

import lombok.extern.slf4j.Slf4j;
import org.lankheet.domiot.model.Sensor;
import org.lankheet.domiot.model.SensorValue;

import java.util.HashMap;
import java.util.Map;

/**
 * For each sensor value it will be evaluated whether it is a new or repeated value.
 */
@Slf4j
public class SensorValueCache {
    private Map<Sensor, Double> latch = new HashMap<>();

    /**
     * Worker method for this class.
     *
     * @param sensorValue The sensor value that is to be inspected
     * @return true: The value was already processed, false: new value
     */
    public boolean isRepeatedValue(SensorValue sensorValue) {
        boolean isRepeated = false;
        Sensor sensor = sensorValue.getSensor();

        if (!latch.isEmpty() && latch.containsKey(sensorValue.getSensor())) {
            double value = latch.get(sensor);
            if (value == sensorValue.getValue()) {
                isRepeated = true;
            }
        }
        if (!isRepeated) {
            latch.put(sensorValue.getSensor(), sensorValue.getValue());
            log.debug("Store new latch: {}", sensorValue);
        }
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

        latch.keySet().forEach(sensor -> {
            builder.append(String.format("{name = %s, type = %s} -> ", sensor.getName(), sensor.getType()));
            builder.append(String.format("value = %f}%n", latch.get(sensor)));
        });
        return builder.toString();
    }

    /**
     * Get latch.
     *
     * @return the latch
     */
    public Map<Sensor, Double> getLatch() {
        return latch;
    }
}
