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

    public static final String NUMBER_TYPE = "NUMBER";
    public static final String STRING_TYPE = "STRING";

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

        device.addParameter(DomiotParameterDto.builder()
                        .name("HW:logicalProcessors")
                        .parameterType(NUMBER_TYPE)
                        .value(hal.getProcessor().getLogicalProcessors().size())
                        .build())
                .addParameter(DomiotParameterDto.builder()
                        .name("jvm:maxMemory(B)")
                        .parameterType(NUMBER_TYPE)
                        .value(runtime.maxMemory())
                        .build())
                .addParameter(DomiotParameterDto.builder()
                        .name("jvm:totalMemory(B)")
                        .parameterType(NUMBER_TYPE)
                        .value(runtime.totalMemory())
                        .build())
                .addParameter(DomiotParameterDto.builder()
                        .name("OS")
                        .parameterType(STRING_TYPE)
                        .value(os.toString())
                        .build())
                .addParameter(DomiotParameterDto.builder()
                        .name("usb devices")
                        .parameterType(STRING_TYPE)
                        .value(hal.getUsbDevices(true)
                                .stream()
                                .map(UsbDevice::getName)
                                .collect(Collectors.joining(",")))
                        .build());
        Arrays.stream(fsRoots).forEach(fileRoot -> {
            device.addParameter(DomiotParameterDto.builder()
                    .name(fileRoot.getName() + ": file system root")
                    .parameterType(STRING_TYPE)
                    .value(fileRoot.getAbsolutePath())
                    .build());
            device.addParameter(DomiotParameterDto.builder()
                    .name(fileRoot.getName() + ": total space(B)")
                    .parameterType(NUMBER_TYPE)
                    .value(fileRoot.getTotalSpace())
                    .build());
            device.addParameter(DomiotParameterDto.builder()
                    .name(fileRoot.getName() + ": free space(B)")
                    .parameterType(NUMBER_TYPE)
                    .value(fileRoot.getFreeSpace())
                    .build());
            device.addParameter(DomiotParameterDto.builder()
                    .name(fileRoot.getName() + ": usable space(B)")
                    .parameterType(NUMBER_TYPE)
                    .value(fileRoot.getUsableSpace())
                    .build());
        });
    }
}
