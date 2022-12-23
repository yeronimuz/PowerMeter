package com.domiot.powermeter.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public interface MqttConfig
{
   @JsonProperty
   String getUrl();


   @JsonProperty
   void setUrl(String url);


   @JsonProperty
   String getUserName();


   @JsonProperty
   void setUserName(String userName);


   @JsonProperty
   String getPassword();


   @JsonProperty
   void setPassword(String passWord);


   @JsonProperty
   List<MqttTopicConfig> getTopics();


   @JsonProperty
   void setTopics(List<MqttTopicConfig> topics);


   @JsonProperty
   String getClientName();


   @JsonProperty
   void setClientName(String clientName);


   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   String toString();
}
