package com.lankheet.pmagent.mqtt;

import com.lankheet.pmagent.PowerMeterMqttCallback;
import com.lankheet.pmagent.config.MqttConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Arrays;

/**
 * Mqtt service class. Setup and Connect to MQTT broker
 */
@Slf4j
public class MqttService {
    private static final int MQTT_RETRIES = 10;
    private static final int MS_DELAY_BETWEEN_RETRIES = 1000;

    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;

    public MqttService(MqttConfig mqttConfig) throws MqttException {
        this.mqttConnectOptions = configMqttClient(mqttConfig);
    }

    private MqttConnectOptions configMqttClient(MqttConfig mqttConfig) throws MqttException {
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

    /**
     * Connect to the MQTT broker. Retry MQTT_RETRIES times with MS_DELAY_BETWEEN_RETRIES
     *
     * @return The connected mqttClient
     * @throws MqttException If after the retries still no connection was established
     */
    public MqttClient connectToBroker()
            throws MqttException {
        for (int count = 0; count < MQTT_RETRIES; count++) {
            try {
                log.debug("Connecting to {}", Arrays.stream(mqttConnectOptions.getServerURIs()).sequential().toList());
                mqttClient.connect(this.mqttConnectOptions);
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
        return this.mqttClient;
    }
}
