package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MqttConfig
{
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
