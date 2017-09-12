package com.lankheet.pmagent.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttConfig {
	
	@NotEmpty
	private String url;

	@JsonProperty
	public String getUrl() {
		return url;
	}

	@JsonProperty
	public void setUrl(String url) {
		this.url = url;
	}
}
