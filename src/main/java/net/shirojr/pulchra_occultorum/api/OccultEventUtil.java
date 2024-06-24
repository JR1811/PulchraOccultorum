package net.shirojr.pulchra_occultorum.api;

public class OccultEventUtil {
    private OccultEventUtil() {
    }

    public static long timeFromDays(int days) {
        return days * 24000L;
    }

    public static int daysFromTime(long time) {
        return (int) (time / 24000L);
    }
}
