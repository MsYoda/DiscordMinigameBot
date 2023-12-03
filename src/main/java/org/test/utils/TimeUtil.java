package org.test.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    public static String getDurationInString(LocalDateTime from, LocalDateTime to)
    {
        Duration duration = Duration.between(from, to);
        return String.format("%dh %dmin", duration.toHours(), duration.toMinutes());
    }
}
