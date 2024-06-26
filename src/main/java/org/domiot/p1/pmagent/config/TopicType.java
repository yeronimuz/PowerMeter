package org.domiot.p1.pmagent.config;

public enum TopicType {
    // TODO: Move to API, maybe a separate Mqtt API
    POWER("power"),
    GAS("gas"),
    TEMPERATURE("temp"),
    HUMIDITY("humidity"),
    ;

    private String topicName;

    private TopicType(String topicName) {
        this.setTopicName(topicName);
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public TopicType getTopicName(String topicName) {
        for (TopicType t : values()) {
            if (t.topicName.equals(topicName)) {
                return t;
            }
        }
        return null;
    }
}
