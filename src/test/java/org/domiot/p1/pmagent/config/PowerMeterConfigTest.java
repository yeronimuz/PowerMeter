package org.domiot.p1.pmagent.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PowerMeterConfigTest {
    private static DeviceConfig deviceConfig;

    @BeforeAll
    static void setup() throws IOException {
        deviceConfig = PowerMeterConfig.loadConfigurationFromFile("src/test/resources/power-meter.yml");
    }

    @Test
    void testPmAgentParameters() {
        assertEquals(10000, deviceConfig.getInternalQueueSize());
    }

    @Test
    void testConfigSerial() {
        SerialPortConfig serialConfig = deviceConfig.getSerialPort();
        assertNotNull(serialConfig);
        assertEquals(115200, serialConfig.getBaudRate());
        assertEquals("/dev/ttyUSB0", serialConfig.getUart());
    }

    @Test
    void testConfigMqtt() {
        MqttConfig mqttConfig = deviceConfig.getMqttBroker();
        assertNotNull(mqttConfig);
        assertEquals("PM_unique_client_name", mqttConfig.getClientName());
        assertEquals("johndoe", mqttConfig.getUserName());
        assertEquals(2, mqttConfig.getSubscriptions().size());
        assertEquals("p1", mqttConfig.getSubscriptions().get(0).toLowerCase());
        assertEquals("domiot", mqttConfig.getSubscriptions().get(1).toLowerCase());
    }

    @Test
    void testSensorConfig() {
        List<SensorConfig> sensorConfigs = deviceConfig.getSensorConfigs();
        assertEquals(7, sensorConfigs.size());

        SensorConfig sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_PT1);
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_PT1, sensorConfig.getSensorType());
        assertEquals(0L, sensorConfig.getSensorId());
        assertEquals("sensor/power/pt1", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("power", sensorConfig.getMqttTopicConfig().getTopicType());
        assertEquals("Produced power T1", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_PT2);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_PT2, sensorConfig.getSensorType());
        assertEquals("sensor/power/pt2", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Produced power T2", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_CT1);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_CT1, sensorConfig.getSensorType());
        assertEquals("sensor/power/ct1", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Consumed power T1", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_AC);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_AC, sensorConfig.getSensorType());
        assertEquals("sensor/power/ac", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Accumulated consumed power", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_AP);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_AP, sensorConfig.getSensorType());
        assertEquals("sensor/power/ap", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Accumulated produced power", sensorConfig.getDescription());
    }

    @Test
    void testWriteConfig() throws IOException {
        PowerMeterConfig.saveConfigurationToFile("application-updated.yml", deviceConfig, false);
        deviceConfig = PowerMeterConfig.loadConfigurationFromFile("src/test/resources/power-meter.yml");
        assertEquals(7, deviceConfig.getSensorConfigs().size());
    }

    private SensorConfig getSensorConfig(List<SensorConfig> sensorConfigs, SensorTypeDto type) {
        return sensorConfigs.stream()
                .filter(sensorConfig -> sensorConfig.getSensorType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
