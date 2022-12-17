package src.utilities;

import java.util.concurrent.TimeUnit;

public class SimpleTimeConverter {
    public static String getTimeStringFromLong(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
        return minutes + ":" + seconds;
    }
}
