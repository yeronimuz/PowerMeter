package org.domiot.p1.pmagent;

import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.domiot.p1.pmagent.config.DeviceConfig;
import org.domiot.p1.pmagent.config.PowerMeterConfig;
import org.domiot.p1.pmagent.mqtt.MqttService;
import org.domiot.p1.sensor.SensorValueSender;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.MqttTopicDto;
import org.lankheet.domiot.domotics.dto.SensorDto;
import org.lankheet.domiot.domotics.dto.SensorValueDto;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class SensorValueSenderTest {
    @Mock
    private LoggerFactory loggerFactoryMock;
    @Mock
    private MqttClient mqttClientMock;
    @Mock
    private MqttService mqttServiceMock;
    @Mock
    private Logger loggerMock;
    @Captor
    private ArgumentCaptor<String> logMessage;

    private static DeviceConfig config;
    private static DeviceDto device;

    @BeforeAll
    static void doSetup()
            throws IOException {
        config = PowerMeterConfig.loadConfigurationFromFile("src/test/resources/power-meter.yml");
        device = DeviceDto.builder()
                .sensors(Collections.singletonList(SensorDto.builder()
                        .sensorId(1L)
                        .mqttTopic(MqttTopicDto.builder().path("meterkast/gas").build())
                        .build()))
                .build();
    }

    @Test
    void testSendMessage()
            throws Exception {
        doNothing().when(mqttClientMock).publish(anyString(), any(MqttMessage.class));
        BlockingQueue<SensorValueDto> queue = new ArrayBlockingQueue<>(1000);
        SensorValueSender sensorValueSender = new SensorValueSender(queue, mqttServiceMock, device);
        setField(sensorValueSender, "mqttClient", mqttClientMock);

        sensorValueSender.newSensorValue(SensorValueDto.builder()
                .sensorId(1L)
                .timeStamp(LocalDateTime.now())
                .value(3.5)
                .build());

        verify(mqttClientMock).publish(anyString(), any(MqttMessage.class));
    }

    private void setField(SensorValueSender sensorValueSender, String fieldName, Object object)
            throws IllegalAccessException {
        List<Field> fields = ReflectionUtils.findFields(SensorValueSender.class, f -> f.getName().equals(fieldName), TOP_DOWN);
        Field field = fields.get(0);
        field.setAccessible(true);
        field.set(sensorValueSender, object);
    }
}
