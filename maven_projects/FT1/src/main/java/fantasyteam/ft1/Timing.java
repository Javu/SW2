package fantasyteam.ft1;

import java.util.Calendar;

/**
 * This class is used for real world timing operations. When the class is
 * constructed it stores the value of the current time (this value can be reset
 * to the new current time by using startTiming() if desired). This value can
 * then be used to calculate the time difference (in ms) between the new current
 * time and the value for time stored when the class was constructed (or when
 * startTiming() was run). This time difference can be used for real world
 * timing operations.
 *
 * @author Javu
 */
public class Timing {

    private Calendar start_time;

    /**
     * When constructed the class will take a snapshot of the current time. This
     * current time is used to calculate differences in time by comparing the
     * current time at any given moment to this snapshot.
     */
    public Timing() {
        start_time = Calendar.getInstance();
    }

    /**
     * Take a new snapshot of the current time to be used as the offset in
     * calculating time difference.
     */
    public void startTiming() {
        start_time = Calendar.getInstance();
    }

    /**
     * Returns the time snapshot created when this class was constructed, or if
     * the snapshot was reset by running startTiming().
     *
     * @return The time snapshot used to calculate the offset of the current
     * time.
     */
    public long getStartTime() {
        return start_time.getTimeInMillis();
    }

    /**
     * Calculates the difference between the current time and the time snapshot
     * stored in this class when it was constructed or startTiming() was run.
     * This will give you the time difference (in ms) between the current time
     * and the time snapshot.
     *
     * @return The time difference between the current time and the time
     * snapshot.
     */
    public long getTime() {
        Calendar current_time = Calendar.getInstance();
        return current_time.getTimeInMillis() - start_time.getTimeInMillis();
    }

    /**
     * Causes program execution to wait for a period of time specified in real
     * world milliseconds.
     *
     * @param ms The number of milliseconds to freeze execution of the program
     * for.
     */
    public void waitTime(long ms) {
        Calendar time = Calendar.getInstance();
        long time_difference = 0;
        while (time_difference <= ms) {
            time_difference = Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis();
        }
    }
}
