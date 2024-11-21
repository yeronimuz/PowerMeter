package org.domiot.p1.pmagent.mqtt.config;

import java.io.IOException;

import org.lankheet.domiot.domotics.dto.DeviceDto;

public interface DeviceConfigListener {

    void updateConfig(DeviceDto device) throws IOException;
}
