package org.domiot.p1.pmagent.mqtt;

import org.lankheet.domiot.domotics.dto.DeviceDto;

public interface MqttConfigListener {
    void onUpdateDevice(DeviceDto deviceDto);
}
