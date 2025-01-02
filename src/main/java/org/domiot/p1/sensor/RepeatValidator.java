package org.domiot.p1.sensor;

import java.time.LocalDateTime;

/**
 * The RepeatValidator class is used to determine if a given timestamp is around the minute rollover.
 * This is needed because timestamps of sensor values need to be at the same interval when they are to be plotted in a graph.
 */
public class RepeatValidator {
    public static final int OFFSET_MS_DIFF = 20;
    private static final int ONE_MIN_IN_MS = 60000;

    private RepeatValidator() {
    }

    /**
     * check if the timestamp is within a certain offset from the minute border.
     *
     * @param timestamp Is the timestamp close enough to a minute boundary.
     * @return True if the timestamp is near to minute rollover
     */
    public static boolean isValueAroundMinuteRollOver(LocalDateTime timestamp) {
        long msSinceMinuteRollOver = calculateMillisecondsSinceMinuteRollOver(timestamp);
        return (msSinceMinuteRollOver <= OFFSET_MS_DIFF) || ((ONE_MIN_IN_MS - msSinceMinuteRollOver) <= OFFSET_MS_DIFF);
    }

    private static long calculateMillisecondsSinceMinuteRollOver(LocalDateTime timestamp) {
        return timestamp.getSecond() * 1000 + (long) timestamp.getNano() / 1_000_000;
    }
}
