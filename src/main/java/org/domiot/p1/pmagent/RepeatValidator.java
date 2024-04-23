package org.domiot.p1.pmagent;

import java.time.LocalDateTime;

/**
 * The RepeatValidator class is used to determine if a given timestamp is around the minute border.
 * It provides a method to check if the timestamp is within a certain offset from the minute border.
 */
public class RepeatValidator {
    public static final int OFFSET_MS_DIFF = 20;
    private static final int ONE_MIN_IN_MS = 60000;

    static boolean isValueAroundMinuteBorder(LocalDateTime timestamp) {
        long msSinceMinuteBoarder = calculateMillisecondsSinceMinuteBorder(timestamp);
        return (msSinceMinuteBoarder <= OFFSET_MS_DIFF) || ((ONE_MIN_IN_MS - msSinceMinuteBoarder) <= OFFSET_MS_DIFF);
    }

    private static long calculateMillisecondsSinceMinuteBorder(LocalDateTime timestamp) {
        return timestamp.getSecond() * 1000 + timestamp.getNano() / 1_000_000;
    }
}
