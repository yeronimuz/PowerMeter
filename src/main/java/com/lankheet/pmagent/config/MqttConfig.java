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

package com.lankheet.pmagent.config;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttConfig {
	
	@NotEmpty
	private String url;
	
	@NotEmpty
	private String userName;
	
	@NotEmpty
	private String password;
	
	@NotEmpty
	private String caFilePath;
	
	private String crtFilePath;
	
	private String clientKeyFilePath;
	
	private List<MqttTopicConfig> topics;
	
	@JsonProperty
	public String getUrl() {
		return url;
	}

	@JsonProperty
	public void setUrl(String url) {
		this.url = url;
	}
	
	@JsonProperty
	public String getCaFilePath() {
		return caFilePath;
	}
	
	@JsonProperty
	public void setCaFilePath(String caFilePath) {
		this.caFilePath = caFilePath;
	}
	
	@JsonProperty
	public String getCrtFilePath() {
		return crtFilePath;
	}
	
	@JsonProperty
	public void setCrtFilePath(String crtFilePath) {
		this.crtFilePath = crtFilePath;
	}
	
	@JsonProperty
	public String getClientKeyFilePath() {
		return clientKeyFilePath;
	}

	@JsonProperty
	public void setClientKeyFilePath(String clientKeyFilePath) {
		this.clientKeyFilePath = clientKeyFilePath;
	}

	@JsonProperty
	public String getUserName() {
		return userName;
	}

	@JsonProperty
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String passWord) {
		this.password = passWord;
	}

	@JsonProperty
	public List<MqttTopicConfig> getTopics() {
		return topics;
	}
	
	@JsonProperty
	public void setTopics(List<MqttTopicConfig> topics) {
		this.topics = topics;
	}
}
