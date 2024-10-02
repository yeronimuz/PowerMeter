package org.domiot.p1.pmagent;

import org.domiot.p1.pmagent.config.DeviceConfig;
import org.domiot.p1.pmagent.config.PMAgentConfig;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorValueSenderTest {
    @Mock
    private LoggerFactory LoggerFactoryMock;
    @Mock
    private MqttClient mqttClientMock;

    @Mock
    private Logger loggerMock;
    @Captor
    private ArgumentCaptor<String> logMessage;

    private static DeviceConfig config;

    @BeforeAll
    static void doSetup()
            throws IOException {
        config = PMAgentConfig.loadConfigurationFromFile("src/test/resources/application.yml");
    }

    @Test
    void testSendMessage(@Mock DeviceDto device, @Mock SensorDto sensor, @Mock MqttTopicDto mqttTopic)
            throws Exception {
        doNothing().when(mqttClientMock).publish(anyString(), any(MqttMessage.class));
        when(device.getSensors()).thenReturn(List.of(sensor));
        when(sensor.getSensorId()).thenReturn(1L);
        when(sensor.getMqttTopic()).thenReturn(mqttTopic);
        when(mqttTopic.getPath()).thenReturn("topic");
        BlockingQueue<SensorValueDto> queue = new ArrayBlockingQueue<>(1000);
        SensorValueSender sensorValueSender = new SensorValueSender(queue, config.getMqttBroker(), device);
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
