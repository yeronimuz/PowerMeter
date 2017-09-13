package com.lankheet.pmagent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.lankheet.iot.datatypes.Measurement;

public class MeasurementSender implements MeasurementListener {
	private static final Logger LOG = LogManager.getLogger(MeasurementSender.class);

	private MessageQueueClient msgQclient;

	public MeasurementSender(String urlString) throws MqttException {
		setMsgQclient(new MessageQueueClient(urlString /* "tcp://192.168.2.10:1883" */));
	}

	@Override
	public void newMeasurement(Measurement measurement) {
		MessageQueueClient msgQclient = null;
		System.out.println(measurement);
		try {
			msgQclient.connect();
			msgQclient.publish(measurement);
			msgQclient.disconnect();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public MessageQueueClient getMsgQclient() {
		return msgQclient;
	}

	public void setMsgQclient(MessageQueueClient msgQclient) {
		this.msgQclient = msgQclient;
	}

}
