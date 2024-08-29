package org.domiot.p1.pmagent.mqtt.config;

import lombok.Getter;
import org.lankheet.domiot.domotics.dto.DeviceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * The DeviceConfigUpdater class is responsible for managing a list of DeviceConfigListener objects and notifying them about configuration updates for a Device.
 * It implements the DeviceConfigListener interface to receive update notifications.
 */
@Getter
public class DeviceConfigUpdater implements DeviceConfigListener {
    List<DeviceConfigListener> listeners = new ArrayList<>();

    public void addListener(DeviceConfigListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(DeviceConfigListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void updateConfig(DeviceDto device) {
        listeners.forEach(listener -> listener.updateConfig(device));
    }
}
