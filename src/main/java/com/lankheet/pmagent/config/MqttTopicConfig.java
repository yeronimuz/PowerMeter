package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttTopicConfig {
 
    private String topic;

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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MqttTopicConfig [topic=" + topic + ", type=" + type + "]";
    }
}
