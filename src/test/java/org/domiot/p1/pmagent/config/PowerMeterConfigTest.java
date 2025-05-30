package org.domiot.p1.pmagent.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lankheet.domiot.domotics.dto.SensorTypeDto;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PowerMeterConfigTest {
    private static DeviceConfig deviceConfig;

    @BeforeAll
    static void setup() throws IOException {
        deviceConfig = PowerMeterConfig.loadConfigurationFromFile("src/test/resources/power-meter.yml");
    }

    @Test
    void testPmAgentParameters() {

                deviceConfig.getDeviceParameters()
                        .stream()
                        .filter(param -> param.getName().equals("internalQueueSize"))
                        .findFirst()
                        .ifPresent( configParameter -> {
                            System.out.println(configParameter);
                            assertEquals(10000, configParameter.getValue());
                        });
    }

    @Test
    void testConfigSerial() {
        SerialPortConfig serialConfig = deviceConfig.getSerialPort();
        assertNotNull(serialConfig);
        assertEquals(115200, serialConfig.getBaudRate());
        assertEquals("/tmp/fake-serial-pipe", serialConfig.getUart());
    }

    @Test
    void testConfigMqtt() {
        MqttConfig mqttConfig = deviceConfig.getMqttBroker();
        assertNotNull(mqttConfig);
        assertEquals("PM_AUT_client_name", mqttConfig.getClientName());
        assertEquals("johndoe", mqttConfig.getUserName());
        assertEquals(4, mqttConfig.getSubscriptions().size());
        assertTrue(mqttConfig.getSubscriptions().contains("config"));
        assertTrue(mqttConfig.getSubscriptions().contains("update"));
        assertTrue(mqttConfig.getSubscriptions().contains("p1"));
        assertTrue(mqttConfig.getSubscriptions().contains("domiot"));
    }

    @Test
    void testSensorConfig() {
        List<SensorConfig> sensorConfigs = deviceConfig.getSensorConfigs();
        assertEquals(7, sensorConfigs.size());

        SensorConfig sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_PT1);
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_PT1, sensorConfig.getSensorType());
        assertEquals(0L, sensorConfig.getSensorId());
        assertEquals("sensor/meterbox/pt1", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("power", sensorConfig.getMqttTopicConfig().getTopicType());
        assertEquals("Produced power T1", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_PT2);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_PT2, sensorConfig.getSensorType());
        assertEquals("sensor/meterbox/pt2", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Produced power T2", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_CT1);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_CT1, sensorConfig.getSensorType());
        assertEquals("sensor/meterbox/ct1", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Consumed power T1", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_AC);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_AC, sensorConfig.getSensorType());
        assertEquals("sensor/meterbox/ac", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Accumulated consumed power", sensorConfig.getDescription());

        sensorConfig = getSensorConfig(sensorConfigs, SensorTypeDto.POWER_AP);
        assertEquals(0L, sensorConfig.getSensorId());
        assertNotNull(sensorConfig);
        assertEquals(SensorTypeDto.POWER_AP, sensorConfig.getSensorType());
        assertEquals("sensor/meterbox/ap", sensorConfig.getMqttTopicConfig().getTopic());
        assertEquals("Accumulated produced power", sensorConfig.getDescription());
    }

    @Test
    void testWriteConfig() throws IOException {
        PowerMeterConfig.saveConfigurationToFile("build/tmp/application-updated.yml", deviceConfig, false);
        deviceConfig = PowerMeterConfig.loadConfigurationFromFile("src/test/resources/power-meter.yml");
        assertEquals(7, deviceConfig.getSensorConfigs().size());
    }

    @Test
    void testIsAllSensorsHaveIds() {
        deviceConfig.getSensorConfigs().forEach(config -> {config.setSensorId(0);});
        assertFalse(PowerMeterConfig.isAllSensorsHaveIds(deviceConfig));
        deviceConfig.getSensorConfigs().forEach(config -> {config.setSensorId(1L);});
        assertTrue(PowerMeterConfig.isAllSensorsHaveIds(deviceConfig));
    }

    private SensorConfig getSensorConfig(List<SensorConfig> sensorConfigs, SensorTypeDto type) {
        return sensorConfigs.stream()
                .filter(sensorConfig -> sensorConfig.getSensorType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
