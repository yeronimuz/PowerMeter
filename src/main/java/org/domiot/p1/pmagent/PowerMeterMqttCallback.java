package org.domiot.p1.pmagent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.domiot.p1.pmagent.mqtt.config.DeviceConfigUpdater;
import org.eclipse.paho.client.mqttv3.*;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PowerMeterMqttCallback implements MqttCallback {
    private static final Logger LOG = LoggerFactory.getLogger(PowerMeterMqttCallback.class);

    DeviceConfigUpdater deviceConfigUpdater = new DeviceConfigUpdater();
    private final MqttClient mqttClient;

    public PowerMeterMqttCallback(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Probably, this method will only be called when no data is to be sent and a connection was
        // disturbed. Since we send data every 10 seconds, this call will probably not be made.
        LOG.error("Connection loss: {}", cause.getMessage());
        try {
            // TODO: How many times, and what delay?
            mqttClient.reconnect();
        } catch (MqttException e) {
            LOG.error("Reconnection Mqtt client failed: {}", e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws IOException {
        if ((message != null) && topic.equals("config")) {
            byte[] payload = message.getPayload();
            ObjectMapper mapper = new ObjectMapper();

            deviceConfigUpdater.updateConfig(mapper.readValue(payload, DeviceDto.class));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.debug("Delivery complete: {}", token.toString());
    }
}
