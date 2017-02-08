package com.lankheet.pmagent;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class LocalStorageConfig extends Configuration {

	@NotEmpty
	private String localStoragePath;
	
	@NotEmpty
	private String filenamePattern;

	@JsonProperty
	public String getStoragePath() {
		return localStoragePath;
	}

	@JsonProperty
	public void setStoragePath(String localStoragePath) {
		this.localStoragePath = localStoragePath;
	}

	@JsonProperty
	public String getFileNamePattern() {
		return filenamePattern;
	}
	
	@JsonProperty
	public void setFileNamePattern(String pattern) {
		this.filenamePattern = pattern;
	}
}
