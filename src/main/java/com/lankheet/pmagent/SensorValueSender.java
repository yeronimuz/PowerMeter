package com.lankheet.pmagent;

import com.lankheet.pmagent.config.MqttConfig;
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
    private static final int MQTT_RETRIES = 10;
    private static final int MS_DELAY_BETWEEN_RETRIES = 1000;
    private final BlockingQueue<SensorValue> queue;

    private MqttClient mqttClient;

    private final MqttConfig mqttConfig;

    private final SensorValueCache sensorValueCache = new SensorValueCache();

    /**
     * Constructor.
     *
     * @param queue      The blocking queue that is filled with sensor values.
     * @param mqttConfig The mqtt configuration.
     */
    public SensorValueSender(BlockingQueue<SensorValue> queue, MqttConfig mqttConfig) {
        this.queue = queue;
        this.mqttConfig = mqttConfig;
    }

    @Override
    public void run() {
        MqttConnectOptions options = null;
        try {
            options = configMqttClient();
        } catch (MqttException e) {
            log.error("Could not create MQTT client {}", e.getMessage());
        }
        try {
            connectToBroker(options);
        } catch (MqttException e) {
            System.exit(-1);
        }

        while (true) {
            try {
                // Check line
                if (!mqttClient.isConnected()) {
                    connectToBroker(options);
                }
                newSensorValue(queue.take());
            } catch (InterruptedException e) {
                log.error("Reading queue was interrupted");
            } catch (MqttException e) {
                log.error("Unrecoverable error connecting to broker");
            }
        }
    }

    private MqttConnectOptions configMqttClient() throws MqttException {
        log.info("Mqtt broker connection starting: {}", mqttConfig);
        String userName = mqttConfig.getUserName();
        String password = mqttConfig.getPassword();
        mqttClient = new MqttClient(mqttConfig.getUrl(), mqttConfig.getClientName());

        MqttConnectOptions options = new MqttConnectOptions();
        mqttClient.setCallback(new PowerMeterMqttCallback(mqttClient));
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
        return options;
    }

    private void connectToBroker(MqttConnectOptions options)
            throws MqttException {
        for (int count = 0; count < MQTT_RETRIES; count++) {
            try {
                log.debug("Connecting to {}", mqttConfig.getUrl());
                mqttClient.connect(options);
                if (mqttClient.isConnected()) {
                    break;
                }
                Thread.sleep(MS_DELAY_BETWEEN_RETRIES);
            } catch (MqttException e) {
                log.error("Cannot connect: {}, retrying {}", e.getMessage(), count);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!mqttClient.isConnected()) {
            throw new MqttException(MqttException.REASON_CODE_BROKER_UNAVAILABLE, new Throwable("Unable to connect"));
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
