package com.lankheet.pmagent;

import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;

public class MeasurementSender implements MeasurementListener {
    private static final Logger LOG = LogManager.getLogger(MeasurementSender.class);

    private MqttClient mqttClient;

    private List<MqttTopicConfig> topics;

    /**
     * Constructor.
     * 
     * @param mqttClient The MQTT client that sends the measurements
     * @param topics The configured topics in the config file
     */
    public MeasurementSender(MqttClient mqttClient, List<MqttTopicConfig> topics) {
        this.mqttClient = mqttClient;
        this.topics = topics;
    }

    @Override
    public void newMeasurement(Measurement measurement) {
        LOG.trace("newMeasurement: " + measurement);
        String mqttTopic = null;
        TopicType topicType = getTopicTypeFromMeasurementType(measurement);
        // Get the destination
        for (MqttTopicConfig mtc : topics) {
            if (mtc.getType().getTopicName().equals(topicType.getTopicName())) {
                mqttTopic = mtc.getTopic();
                break;
            }
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(JsonUtil.toJson(measurement).getBytes());
            LOG.info("Sending Topic: " + mqttTopic + ", Message: " + message);
            mqttClient.publish(mqttTopic, message);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private TopicType getTopicTypeFromMeasurementType(Measurement measurement) {
        TopicType returnType = null;
        switch (MeasurementType.getType(measurement.getType())) {
            case PRODUCED_POWER_T1:
            case PRODUCED_POWER_T2:
            case CONSUMED_POWER_T1:
            case CONSUMED_POWER_T2:
            case ACTUAL_CONSUMED_POWER:
            case ACTUAL_PRODUCED_POWER:
                returnType = TopicType.POWER;
                break;
            case CONSUMED_GAS:
                returnType = TopicType.GAS;
                break;
            case HUMIDITY:
                returnType = TopicType.HUMIDITY;
                break;
            case TEMPERATURE:
                returnType = TopicType.TEMPERATURE;
                break;
            default:
                LOG.error("There is no mapping for measurementType:"
                        + measurement.getType().toString());
                // return null
        }
        return returnType;
    }
}
