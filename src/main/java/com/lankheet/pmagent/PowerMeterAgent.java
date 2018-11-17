package com.lankheet.pmagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.iot.datatypes.domotics.SensorNode;
import com.lankheet.iot.datatypes.entities.SensorType;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.health.MqttHealthCheck;
import com.lankheet.pmagent.resources.AboutPMAgent;
import com.lankheet.pmagent.resources.PMAboutResource;
import com.lankheet.utils.NetUtils;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It sends a series of SensorValue objects to an MQTT broker<br>
 */
public class PowerMeterAgent extends Application<PMAgentConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(PowerMeterAgent.class);

    private static final int SERIAL_DATA_BITS = 8;

    /** P1 UART */
    static SerialPort serialPort;

    public static void main(String[] args) throws Exception {
        new PowerMeterAgent().run(args[0], args[1]);
    }

    @Override
    public void initialize(Bootstrap<PMAgentConfig> bootstrap) {
        LOG.info("P1 manager", "");
    }

    public void setSerialPort(SerialPort serialPort) {
        PowerMeterAgent.serialPort = serialPort;
    }

    @Override
    public void run(PMAgentConfig configuration, Environment environment) throws Exception {
        MqttConfig mqttConfig = configuration.getMqttConfig();
        MqttClientManager mqttClientManager = new MqttClientManager(mqttConfig);
        final PMAboutResource pmaResource = new PMAboutResource(new AboutPMAgent());
        final String nic = configuration.getSensorConfig().getNic();

        // TODO: Start new Dropwizard task that:<BR>
        // * reads data files
        // * puts them in the mqtt queue

        serialPort = new SerialPort(configuration.getSerialPortConfig().getUart());

        try {
            if (!serialPort.openPort()) {
                LOG.error("Serial port: Open port failed");
                return;
            }
            serialPort.setParams(configuration.getSerialPortConfig().getBaudRate(), SERIAL_DATA_BITS, 1, 0);
            int mask = SerialPort.MASK_RXCHAR;
            if (!serialPort.setEventsMask(mask)) {
                LOG.error("Serial port: Unable to set mask");
                return;
            }
            SensorNode sensorNode =
                    new SensorNode(NetUtils.getMacAddress(nic), SensorType.POWER_METER.getId());
            serialPort.addEventListener(new SerialPortReader(serialPort, sensorNode,
                    new SensorValueSender(mqttClientManager.getClient(), mqttConfig.getTopics())));

        } catch (SerialPortException ex) {
            LOG.error(ex.getMessage());
        }

        environment.getApplicationContext().setContextPath("/api");
        environment.jersey().register(pmaResource);
        environment.lifecycle().manage(mqttClientManager);
        environment.healthChecks().register("mqtt", new MqttHealthCheck(mqttClientManager.getClient()));
    }
}
