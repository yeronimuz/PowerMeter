package com.lankheet.pmagent.runtime;

import lombok.extern.slf4j.Slf4j;
import org.lankheet.domiot.domotics.dto.DeviceDto;
import org.lankheet.domiot.domotics.dto.DomiotParameterDto;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.UsbDevice;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Used in device health monitoring
 */
@Slf4j
public class RuntimeFactory {
    private RuntimeFactory() {
    }

    /**
     * Add OS and JVM info to the Device
     * See: <a href="https://github.com/oshi/oshi/blob/master/oshi-core/src/test/java/oshi/SystemInfoTest.java">...</a>
     *
     * @param device The device to enrich
     */
    public static void addRuntimeInfo(DeviceDto device) {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        Runtime runtime = Runtime.getRuntime();
        File[] fsRoots = File.listRoots();

        device.addParameter(new DomiotParameterDto()
                        .name("HW:logicalProcessors")
                        .parameterType("NUMBER")
                        .value(hal.getProcessor().getLogicalProcessors().size()))
                .addParameter(new DomiotParameterDto()
                        .name("jvm:maxMemory(B)")
                        .parameterType("NUMBER")
                        .value(runtime.maxMemory()))
                .addParameter(new DomiotParameterDto()
                        .name("jvm:totalMemory(B)")
                        .parameterType("NUMBER")
                        .value(runtime.totalMemory()))
                .addParameter(new DomiotParameterDto()
                        .name("OS")
                        .parameterType("STRING")
                        .value(os.toString()))
                .addParameter(new DomiotParameterDto()
                        .name("usb devices")
                        .parameterType("STRING")
                        .value(hal.getUsbDevices(true)
                                .stream()
                                .map(UsbDevice::getName)
                                .collect(Collectors.joining(","))));
        Arrays.stream(fsRoots).forEach(fileRoot -> {
            device.addParameter(new DomiotParameterDto()
                    .name(fileRoot.getName() + ": file system root")
                    .parameterType("STRING")
                    .value(fileRoot.getAbsolutePath()));
            device.addParameter(new DomiotParameterDto()
                    .name(fileRoot.getName() + ": total space(B)")
                    .parameterType("NUMBER")
                    .value(fileRoot.getTotalSpace()));
            device.addParameter(new DomiotParameterDto()
                    .name(fileRoot.getName() + ": free space(B)")
                    .parameterType("NUMBER")
                    .value(fileRoot.getFreeSpace()));
            device.addParameter(new DomiotParameterDto()
                    .name(fileRoot.getName() + ": usable space(B)")
                    .parameterType("NUMBER")
                    .value(fileRoot.getUsableSpace()));
        });
    }
}
