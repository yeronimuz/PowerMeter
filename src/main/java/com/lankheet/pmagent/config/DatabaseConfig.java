package com.lankheet.pmagent.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfig {
	
	@NotEmpty
	private String url = "jdbc:derby://localhost:1527/pmDB;create=true;user=me;password=mine";

	@JsonProperty
	public String getUrl() {
		return url;
	}

	@JsonProperty
	public void setUrl(String url) {
		this.url = url;
	}
}
