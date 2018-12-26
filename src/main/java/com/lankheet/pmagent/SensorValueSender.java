package com.lankheet.pmagent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.domotics.SensorValue;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.MqttTopicConfig;
import com.lankheet.pmagent.config.TopicType;
import com.lankheet.utils.JsonUtil;

public class SensorValueSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SensorValueSender.class);
    
    // TODO: Read from yaml file
    private static final String MQTT_PM_CLIENT_ID = "PM_CS1F08";

    private final BlockingQueue<SensorValue> queue;

    private MqttClient mqttClient;

    private MqttConfig mqttConfig;

    private Map<SensorNode, Map<Integer, Double>> lastMeasuredMap = new HashMap<>();

    /**
     * Constructor.
     * 
     * @param mqttClient The MQTT client that sends the measurements
     * @param topics The configured topics in the config file
     */
    public SensorValueSender(BlockingQueue<SensorValue> queue, MqttConfig mqttConfig) {
        this.queue = queue;
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void run() {
        try {
            connectToBroker(mqttConfig);
        } catch (MqttException e) {
            LOG.error("Cannot connect: " + e.getMessage());
            // TODO: Make recoverable
            System.exit(-1);
        }
        
        while(true) {
            try {
                newSensorValue(queue.take());
            } catch (InterruptedException e) {
                LOG.error("Reading queue was interrupted");
            }
        }
        
        // TODO put next lines in cleanup
        // mqttClient.disconnect();
        // mqttClient.close();
    }

    private void connectToBroker(MqttConfig mqttConfig) throws MqttException {
        String userName = mqttConfig.getUserName();
        String password = mqttConfig.getPassword();
        mqttClient = new MqttClient(mqttConfig.getUrl(), MQTT_PM_CLIENT_ID);

        MqttConnectOptions options = new MqttConnectOptions();
        mqttClient.setCallback(new PowerMeterMqttCallback(mqttClient));
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
        
        mqttClient.connect(options);
    }

    public void newSensorValue(SensorValue sensorValue) {
        // TODO: Fix repeated values
        // if (!isRepeatedValue(sensorValue)) {
            LOG.debug("new value: {}", sensorValue);
            String mqttTopic = null;
            TopicType topicType = getTopicTypeFromSensorValueType(sensorValue);
            // Get the destination
            for (MqttTopicConfig mqttTopicConfig : mqttConfig.getTopics()) {
                if (mqttTopicConfig.getType().getTopicName().equals(topicType.getTopicName())) {
                    mqttTopic = mqttTopicConfig.getTopic();
                    break;
                }
            }
            boolean isConnectionOk = true;
            MqttMessage message = new MqttMessage();
            message.setPayload(JsonUtil.toJson(sensorValue).getBytes());
            LOG.debug("Sending Topic: " + mqttTopic + ", Message: " + message);
            do {
            try {
                mqttClient.publish(mqttTopic, message);
                isConnectionOk = true;
            } catch (Exception e) {
                LOG.error(e.getMessage());
                isConnectionOk = false;
                
                try {
                    mqttClient.reconnect();
                } catch (MqttException e1) {
                    // NOP
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // NOP
                }
            }
            } while (!isConnectionOk);
            
        // }
    }


    private boolean isRepeatedValue(SensorValue sensorValue) {
        boolean isRepeated = false;
        SensorNode sensorNode = sensorValue.getSensorNode();

        Map<Integer, Double> typeValueMap = new HashMap<>();

        if (lastMeasuredMap.keySet().isEmpty() || !lastMeasuredMap.containsKey(sensorNode)) {
            typeValueMap.put(sensorValue.getMeasurementType(), sensorValue.getValue());
            lastMeasuredMap.put(sensorNode, typeValueMap);
        } else {
            // Check for type in map
            double lastValue = lastMeasuredMap.get(sensorNode).get(sensorValue.getMeasurementType());
            if (lastValue == sensorValue.getValue()) {
                isRepeated = true;
            } else {
                typeValueMap.put(sensorValue.getMeasurementType(), sensorValue.getValue());
                // TODO: Store new value
            }
        }
        return isRepeated;
    }


    private TopicType getTopicTypeFromSensorValueType(SensorValue sensorValue) {
        TopicType returnType = null;
        // FIXME: Gas measurement is mapped to lnb/eng/power instead of lnb/eng/gas
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
