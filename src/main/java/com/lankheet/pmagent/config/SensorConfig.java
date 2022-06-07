package com.lankheet.pmagent.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lankheet.iot.datatypes.entities.SensorType;

import java.util.List;

public class SensorConfig
{
   private String nic;

   private List<SensorType> sensorTypes;


   @JsonProperty
   public String getNic()
   {
      return nic;
   }


   @JsonProperty
   public List<SensorType> getSensorTypes()
   {
      return sensorTypes;
   }


   /**
    * Set nic.
    *
    * @param nic the nic to set
    */
   public void setNic(String nic)
   {
      this.nic = nic;
   }


   /**
    * Set sensorTypes.
    *
    * @param sensorTypes the sensorTypes to set
    */
   public void setSensorTypes(List<SensorType> sensorTypes)
   {
      this.sensorTypes = sensorTypes;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "SensorConfig [nic=" + nic + ", sensorTypes=" + sensorTypes + "]";
   }
}
