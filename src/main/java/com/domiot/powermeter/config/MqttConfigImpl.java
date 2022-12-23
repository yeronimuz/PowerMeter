package com.domiot.powermeter.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MqttConfigImpl implements MqttConfig
{

   private String clientName;

   private String url;

   private String userName;

   private String password;

   private List<MqttTopicConfig> topics;


   @Override
   @JsonProperty
   public String getUrl()
   {
      return url;
   }


   @Override
   @JsonProperty
   public void setUrl(String url)
   {
      this.url = url;
   }


   @Override
   @JsonProperty
   public String getUserName()
   {
      return userName;
   }


   @Override
   @JsonProperty
   public void setUserName(String userName)
   {
      this.userName = userName;
   }


   @Override
   @JsonProperty
   public String getPassword()
   {
      return password;
   }


   @Override
   @JsonProperty
   public void setPassword(String passWord)
   {
      this.password = passWord;
   }


   @Override
   @JsonProperty
   public List<MqttTopicConfig> getTopics()
   {
      return topics;
   }


   @Override
   @JsonProperty
   public void setTopics(List<MqttTopicConfig> topics)
   {
      this.topics = topics;
   }


   @Override
   @JsonProperty
   public String getClientName()
   {
      return clientName;
   }


   @Override
   @JsonProperty
   public void setClientName(String clientName)
   {
      this.clientName = clientName;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "MqttConfig [clientName=" + clientName + ", url=" + url + ", userName=" + userName + ", password="
         + password + ", topics=" + topics + "]";
   }
}
