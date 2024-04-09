package org.domiot.p1.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MqttTopicConfig {
    @JsonProperty
    private String topic;

    @JsonProperty
    private String topicType;
}
