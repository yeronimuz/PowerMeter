package com.lankheet.pmagent;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class PowerMeterShutdownHook extends Thread {
    @Override
    public void run() {
        log.warn("Shutdown Hook is cleaning up!");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        Runtime runtime = Runtime.getRuntime();

        log.info("Memory availabe: {}", hal.getMemory().getAvailable());
        log.info("Memory total: {}", hal.getMemory().getTotal());
        log.info("Processor currentFreq: {}", hal.getProcessor().getCurrentFreq());
        log.info("Processor maxFreq: {}", hal.getProcessor().getMaxFreq());
        log.info("Process Id: {}", os.getCurrentProcess().getProcessID());
        log.info("Process Name: {}", os.getCurrentProcess().getName());
        log.info("Start time: {}", LocalDateTime.ofInstant(Instant.ofEpochMilli(os.getCurrentProcess().getStartTime()), ZoneId.systemDefault()));
        log.info("SystemUpTime: {}", os.getSystemUptime());
        log.info("Runtime jvm:maxMemory {}", runtime.maxMemory());
        log.info("Runtime totalMemory {}", runtime.totalMemory());
        log.info("Runtime freeMemory {}", runtime.freeMemory());
        log.info("Runtime usedMemory {}", runtime.totalMemory() - runtime.freeMemory());
        log.info("Runtime maxMemory {}", runtime.maxMemory());
    }
}
