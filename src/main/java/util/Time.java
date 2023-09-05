package util;

public class Time {
    private static final double secondsPerNanosecond = 1E-9;
    // Static variables are initialised when program starts, so this works.
    private static final double timeStarted = System.nanoTime();

    public static double getTime() {
        return (System.nanoTime() - timeStarted) * secondsPerNanosecond;
    }
}
