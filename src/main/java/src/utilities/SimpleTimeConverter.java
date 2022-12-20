package src.utilities;

import java.util.concurrent.TimeUnit;

public class SimpleTimeConverter {
    public static String getTimeStringFromLong(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
        if (seconds < 9)
            return minutes + ":0" + seconds;
        return minutes + ":" + seconds;
    }
}
