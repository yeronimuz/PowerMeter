package org.domiot.p1.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class JvmMemoryUtil {
    private JvmMemoryUtil() {}

    public static void logMemoryStatistics() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getSecond() == 0) {
            // Print every minute
            Runtime runtime = Runtime.getRuntime();

            log.info("Memory Max {} MB, Total {} MB, Used {} MB, Free {} MB",
                    runtime.maxMemory() / 1024 / 1024,
                    runtime.totalMemory() / 1024 / 1024,
                    (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
                    runtime.freeMemory() / 1024 / 1024);
        }
    }
}
