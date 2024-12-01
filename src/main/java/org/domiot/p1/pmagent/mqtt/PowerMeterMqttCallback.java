package org.domiot.p1.pmagent.mqtt;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.lankheet.domiot.domotics.dto.DeviceDto;

@Slf4j
public class PowerMeterMqttCallback implements MqttCallback {
    private final MqttClient mqttClient;
    private final SynchronousQueue<DeviceDto> configQueue;

    public PowerMeterMqttCallback(MqttClient mqttClient, Queue<DeviceDto> configQueue) {
        this.mqttClient = mqttClient;
        this.configQueue = (SynchronousQueue<DeviceDto>) configQueue;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Probably, this method will only be called when no data is to be sent and a connection was
        // disturbed. Since we send data every second, this call will probably not be made.
        log.error("Connection loss: {}", cause.getMessage());
        try {
            // TODO: How many times, and what delay?
            mqttClient.reconnect();
        } catch (MqttException e) {
            log.error("Reconnection Mqtt client failed: {}", e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws IOException {
        if ((message != null) && topic.equals("config")) {
            log.info("Message on topic {} received: {}", topic, new String(message.getPayload()));
            byte[] payload = message.getPayload();
            ObjectMapper mapper = new ObjectMapper();
            try {
                configQueue.put(mapper.readValue(payload, DeviceDto.class));
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("Delivery complete: {}", token);
    }
}
