/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.pmagent;

import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.Measurement;
import com.lankheet.iot.datatypes.MeasurementType;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;

public class MeasurementSender implements MeasurementListener {
    private static final Logger LOG = LoggerFactory.getLogger(MeasurementSender.class);

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
