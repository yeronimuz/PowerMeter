package com.lankheet.pmagent.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttTopicConfig {
	@NotEmpty
	private String topic;

	@NotEmpty
	private TopicType type;

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
