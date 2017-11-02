package com.lankheet.pmagent.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttTopicConfig {
	@NotEmpty
	private String topic;

	@NotEmpty
	private TopicType type;

	/**
	 * Constructor (for test purposes).
	 * 
	 * @param topic Mqtt topic
	 * @param type Type from P1 domain
	 */
//	public MqttTopicConfig(String topic, TopicType type) {
//		this.topic = topic;
//		this.type = type;
//	}
	
	@JsonProperty
	public String getTopic() {
		return topic;
	}

	@JsonProperty
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@JsonProperty
	public TopicType getType() {
		return type;
	}

	@JsonProperty
	public void setType(TopicType type) {
		this.type = type;
	}

}
