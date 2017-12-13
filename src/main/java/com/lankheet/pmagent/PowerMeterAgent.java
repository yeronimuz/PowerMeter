/**
 * MIT License
 * 
 * Copyright (c) 2017 Lankheet Software and System Solutions
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lankheet.pmagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lankheet.pmagent.config.MqttConfig;
import com.lankheet.pmagent.config.PMAgentConfig;
import com.lankheet.pmagent.health.MqttHealthCheck;
import com.lankheet.pmagent.resources.AboutPMAgent;
import com.lankheet.pmagent.resources.PMAboutResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * The service reads datagrams from the P1 serial interface<br>
 * It saves the datagram on disk, location is specified in yml config file<br>
 */
public class PowerMeterAgent extends Application<PMAgentConfig> {

    private static final int SERIAL_DATA_BITS = 8;

    private static final Logger LOG = LoggerFactory.getLogger(PowerMeterAgent.class);

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
        final int sensorId = configuration.getSensorConfig().getSensorId();

        // TODO: Start new thread (or something) that<BR>
        // * reads data files
        // * puts them in the queue

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
            serialPort.addEventListener(new SerialPortReader(serialPort, sensorId,
                    new MeasurementSender(mqttClientManager.getClient(), mqttConfig.getTopics())));

        } catch (SerialPortException ex) {
            LOG.error(ex.getMessage());
        }

        environment.getApplicationContext().setContextPath("/api");
        environment.jersey().register(pmaResource);
        environment.lifecycle().manage(mqttClientManager);
        environment.healthChecks().register("mqtt", new MqttHealthCheck(mqttClientManager.getClient()));
    }
}
