package org.domiot.p1.pmagent.mqtt.config;

import org.lankheet.domiot.domotics.dto.DeviceDto;

public interface DeviceConfigListener {

    void updateConfig(DeviceDto device);
}