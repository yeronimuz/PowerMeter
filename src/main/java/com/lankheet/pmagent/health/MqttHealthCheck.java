package com.lankheet.pmagent.health;

import org.eclipse.paho.client.mqttv3.MqttClient;
import com.codahale.metrics.health.HealthCheck;

/**
 * HealthCheck for Mqtt communication.
 *
 */
public class MqttHealthCheck extends HealthCheck {

    private MqttClient mqttClient;

    public MqttHealthCheck(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Override
    protected Result check() throws Exception {
        return mqttClient.isConnected() ? Result.healthy()
                : Result.unhealthy("Mqtt server disconnected");
    }

}
