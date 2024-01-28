package com.lankheet.pmagent;

import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.mqtt.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.lankheet.domiot.model.SensorValue;
import org.lankheet.domiot.utils.JsonUtil;

import java.util.concurrent.BlockingQueue;

/**
 * Worker thread for sending SensorValue objects to an MQTT broker.<BR>
 * <ul><li>It reads SensorValues from a Queue.
 * <li>It sends the values to an MQTT broker.
 * </ul>
 */
@Slf4j
public class SensorValueSender implements Runnable {
    private final BlockingQueue<SensorValue> queue;

    private MqttClient mqttClient;

    private final MqttService mqttService;

    private final SensorValueCache sensorValueCache = new SensorValueCache();

    /**
     * Constructor.
     *
     * @param queue      The blocking queue that is filled with sensor values.
     * @param mqttConfig The mqtt configuration.
     */
    public SensorValueSender(BlockingQueue<SensorValue> queue, MqttConfig mqttConfig) throws MqttException {
        this.queue = queue;
        this.mqttService = new MqttService(mqttConfig);
    }

    @Override
    public void run() {
         try {
             this.mqttClient = this.mqttService.connectToBroker();
         } catch (MqttException e) {
            System.exit(-1);
        }

        while (true) {
            try {
                // Check line, if down, reconnect
                if (!mqttClient.isConnected()) {
                    mqttClient = this.mqttService.connectToBroker();
                }
                newSensorValue(queue.take());
            } catch (InterruptedException e) {
                log.error("Reading queue was interrupted");
            } catch (MqttException e) {
                log.error("Unrecoverable error connecting to broker");
            }
        }
    }

    public void newSensorValue(SensorValue sensorValue) {
        if (!sensorValueCache.isRepeatedValue(sensorValue)) {
            String mqttTopic = sensorValue.getSensor().getMqttTopic().getPath();
            boolean isConnectionOk;
            MqttMessage message = new MqttMessage();
            message.setPayload(JsonUtil.toJson(sensorValue).getBytes());
            log.debug("Sending Topic: {}, Msg: {}", mqttTopic, message);
            do {
                try {
                    mqttClient.publish(mqttTopic, message);
                    isConnectionOk = true;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    isConnectionOk = false;
                    try {
                        mqttClient.reconnect();
                        Thread.sleep(500);
                    } catch (MqttException | InterruptedException e1) {
                        // NOP
                    }
                }
            }
            while (!isConnectionOk);
        }
    }
}
