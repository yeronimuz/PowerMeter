package org.domiot.p1.pmagent.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MqttConfig {
    @JsonProperty
    private String clientName;

    @JsonProperty
    private String url;

    @JsonProperty
    private String userName;

    @JsonProperty
    private String password;

    @JsonProperty
    private List<String> subscriptions;
}
