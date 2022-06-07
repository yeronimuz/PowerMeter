package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MqttConfig
{

   private String clientName;

   private String url;

   private String userName;

   private String password;

   private List<MqttTopicConfig> topics;


   @JsonProperty
   public String getUrl()
   {
      return url;
   }


   @JsonProperty
   public void setUrl(String url)
   {
      this.url = url;
   }


   @JsonProperty
   public String getUserName()
   {
      return userName;
   }


   @JsonProperty
   public void setUserName(String userName)
   {
      this.userName = userName;
   }


   @JsonProperty
   public String getPassword()
   {
      return password;
   }


   @JsonProperty
   public void setPassword(String passWord)
   {
      this.password = passWord;
   }


   @JsonProperty
   public List<MqttTopicConfig> getTopics()
   {
      return topics;
   }


   @JsonProperty
   public void setTopics(List<MqttTopicConfig> topics)
   {
      this.topics = topics;
   }


   @JsonProperty
   public String getClientName()
   {
      return clientName;
   }


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
