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
    public static final int NUMBER_OF_CONNECTION_RETRIES = 10;
    private final MqttService mqttService;

    public PowerMeterMqttCallback(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Probably, this method will only be called when no data is to be sent and a connection was
        // disturbed. Since we send data every second, this call will probably not be made.
        log.error("Connection lost: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws IOException {
        if ((message != null) && topic.equals("config")) {
            log.info("Message on topic {} received: {}", topic, new String(message.getPayload()));
            byte[] payload = message.getPayload();
            ObjectMapper mapper = new ObjectMapper();
            log.info("Notifying main thread for config arrival...");
            mqttService.notifyDeviceConfigListeners(mapper.readValue(payload, DeviceDto.class));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("Delivery complete: {}", token);
    }
}
