package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SerialPortConfig
{
   @JsonProperty
   private String p1Key; // /XMX5LGBBFG1009021021

   @JsonProperty
   private String uart = "/dev/ttyUSB0";

   @JsonProperty
   private Integer baudRate = 115200;
}
