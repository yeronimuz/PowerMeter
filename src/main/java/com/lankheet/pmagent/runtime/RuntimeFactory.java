package com.lankheet.pmagent.runtime;

import lombok.extern.slf4j.Slf4j;
import org.lankheet.domiot.model.Device;
import org.lankheet.domiot.model.DomiotParameter;
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
    public static void addRuntimeInfo(Device device) {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        Runtime runtime = Runtime.getRuntime();
        File[] fsRoots = File.listRoots();

        device.addParametersItem(new DomiotParameter()
                        .name("HW:logicalProcessors")
                        .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                        .value(hal.getProcessor().getLogicalProcessors().size()))
                .addParametersItem(new DomiotParameter()
                        .name("jvm:maxMemory(B)")
                        .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                        .value(runtime.maxMemory()))
                .addParametersItem(new DomiotParameter()
                        .name("jvm:totalMemory(B)")
                        .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                        .value(runtime.totalMemory()))
                .addParametersItem(new DomiotParameter()
                        .name("OS")
                        .parameterType(DomiotParameter.ParameterTypeEnum.STRING)
                        .value(os.toString()))
                .addParametersItem(new DomiotParameter()
                        .name("usb devices")
                        .parameterType(DomiotParameter.ParameterTypeEnum.STRING)
                        .value(hal.getUsbDevices(true).stream().map(UsbDevice::getName).collect(Collectors.joining(","))));
        Arrays.stream(fsRoots).forEach(fileRoot -> {
            device.addParametersItem(new DomiotParameter()
                    .name(fileRoot.getName() + ": file system root")
                    .parameterType(DomiotParameter.ParameterTypeEnum.STRING)
                    .value(fileRoot.getAbsolutePath()));
            device.addParametersItem(new DomiotParameter()
                    .name(fileRoot.getName() + ": total space(B)")
                    .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                    .value(fileRoot.getTotalSpace()));
            device.addParametersItem(new DomiotParameter()
                    .name(fileRoot.getName() + ": free space(B)")
                    .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                    .value(fileRoot.getFreeSpace()));
            device.addParametersItem(new DomiotParameter()
                    .name(fileRoot.getName() + ": usable space(B)")
                    .parameterType(DomiotParameter.ParameterTypeEnum.NUMBER)
                    .value(fileRoot.getUsableSpace()));
        });
    }
}
