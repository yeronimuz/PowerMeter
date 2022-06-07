package com.lankheet.pmagent;

import com.lankheet.iot.datatypes.domotics.SensorValue;

/**
 * SensorValueListener listens for new sensor values.<BR> The measurements are coming (a.o.) from the P1 smart meter via the SensorValueAdapter.<BR>
 */
public interface SensorValueListener
{

   /**
    * A new value has arrived.
    *
    * @param sensorValue The value coming from the sensor
    */
   void newSensorValue(SensorValue sensorValue);
}
