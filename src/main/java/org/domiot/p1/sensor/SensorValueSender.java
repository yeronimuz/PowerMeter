package org.domiot.p1.sensor;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;

import org.domiot.p1.pmagent.mqtt.MqttService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.lankheet.domiot.utils.JsonUtil;

/**
 * Worker thread for sending SensorValue objects to an MQTT broker.<BR>
 * <ul><li>It reads SensorValues from a Queue.
 * <li>It sends the values to an MQTT broker.
 * </ul>
 */
@Slf4j
public class SensorValueSender implements Runnable {
    private final DeviceDto deviceDto;
    private volatile boolean running = true;
    private final BlockingQueue<SensorValueDto> queue;

    private MqttClient mqttClient;

    private final MqttService mqttService;
    private final SensorValueCache sensorValueCache = new SensorValueCache();
    private DeviceDto device;


    /**
     * Constructor.
     *
     * @param queue       The blocking queue that is filled with sensor values.
     * @param mqttService The mqtt service.
     * @param device      The device that holds the sensors responsible for the sensor values
     */
    public SensorValueSender(BlockingQueue<SensorValueDto> queue, MqttService mqttService, DeviceDto device) {
        this.queue = queue;
        this.mqttService = mqttService;
        this.deviceDto = device;
    }

    @Override
    public void run() {
        while (running) {
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

    public void newSensorValue(SensorValueDto sensorValue) {
        if (!sensorValueCache.isRepeatedValue(sensorValue) || shouldRepeatValueAfterMinute(sensorValue)) {
            Optional<SensorDto> sensorDtoOptional = this.deviceDto.getSensors().stream()
                    .filter(sensor -> sensor.getSensorId() == sensorValue.getSensorId())
                    .findFirst();
            String mqttTopic = null;
            if (sensorDtoOptional.isPresent()) {
                mqttTopic = sensorDtoOptional.get().getMqttTopic().getPath();
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
                        reconnect();
                    }
                }
                while (!isConnectionOk);
            }
        }
    }

    /**
     * Checks whether the given sensor value should be repeated or not.
     * The repeatValuesAfter value determines the granularity of time to repeat values. See README for more explanation.
     *
     * @param sensorValue The sensor value to be checked.
     * @return true if the value should be repeated, false otherwise.
     */
    boolean shouldRepeatValueAfterMinute(SensorValueDto sensorValue) {
        return RepeatValidator.isValueAroundMinuteRollOver(sensorValue.getTimeStamp());
    }

    private void reconnect() {
        try {
            log.error("Attempting to reconnect to the broker");
            mqttClient.reconnect();
            Thread.sleep(1000);
        } catch (MqttException | InterruptedException e) {
            log.error("Reconnect failed: {}", e.getMessage());
        }
    }
}