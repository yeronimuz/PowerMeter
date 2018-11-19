package com.lankheet.pmagent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lankheet.iot.datatypes.domotics.SensorNode;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;

public class SensorValueSender implements SensorValueListener {
    private static final Logger LOG = LoggerFactory.getLogger(SensorValueSender.class);

    private MqttClient mqttClient;

    private List<MqttTopicConfig> topics;

    private Map<SensorNode, Map<Integer, Double>> lastMeasuredMap = new HashMap<>();

    /**
     * Constructor.
     * 
     * @param mqttClient The MQTT client that sends the measurements
     * @param topics The configured topics in the config file
     */
    public SensorValueSender(MqttClient mqttClient, List<MqttTopicConfig> topics) {
        this.mqttClient = mqttClient;
        this.topics = topics;
    }

    @Override
    public void newSensorValue(SensorValue sensorValue) {
        if (!isRepeatedValue(sensorValue)) {
            String mqttTopic = null;
            TopicType topicType = getTopicTypeFromSensorValueType(sensorValue);
            // Get the destination
            for (MqttTopicConfig mtc : topics) {
                if (mtc.getType().getTopicName().equals(topicType.getTopicName())) {
                    mqttTopic = mtc.getTopic();
                    break;
                }
            }
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(JsonUtil.toJson(sensorValue).getBytes());
                LOG.info("Sending Topic: " + mqttTopic + ", Message: " + message);
                mqttClient.publish(mqttTopic, message);
            }
            catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }


    private boolean isRepeatedValue(SensorValue sensorValue)
    {
        boolean isRepeated = false;
        SensorNode sensorNode = sensorValue.getSensorNode();
        Map<Integer, Double> typeValueMap = new HashMap<>();

        if (lastMeasuredMap.keySet().isEmpty() ||
           !lastMeasuredMap.containsKey(sensorNode)) {
            typeValueMap.put(sensorValue.getMeasurementType(), sensorValue.getValue());
            lastMeasuredMap.put(sensorValue.getSensorNode(), typeValueMap);
        } else {
            // Check for type in map
            double lastValue = lastMeasuredMap.get(sensorNode).get(sensorValue.getMeasurementType());
            if (lastValue == sensorValue.getValue()) {
                isRepeated = true;
            } else {
                typeValueMap.put(sensorValue.getMeasurementType(), sensorValue.getValue());
            }
        }
        return isRepeated;
    }


    private TopicType getTopicTypeFromSensorValueType(SensorValue sensorValue) {
        TopicType returnType = null;
        switch (SensorType.getType(sensorValue.getSensorNode().getSensorType())) {
            case POWER_METER:
                returnType = TopicType.POWER;
                break;
            case GAS_METER:
                returnType = TopicType.GAS;
                break;
            case HUMIDITY:
                returnType = TopicType.HUMIDITY;
                break;
            case TEMPERATURE:
                returnType = TopicType.TEMPERATURE;
                break;
            default:
                LOG.error("There is no mapping for measurementType:" + sensorValue.getSensorNode().getSensorType());
        }
        return returnType;
    }
}
