package fantasyteam.ft1;

import java.util.Calendar;

/**
 *
 * @author Javu
 */
public class Timing {
    
    private Calendar start_time;
    
    public Timing() {
        start_time  = Calendar.getInstance();
    }
    
    public void startTiming() {
        start_time = Calendar.getInstance();
    }
    
    public long getStartTime() {
        return start_time.getTimeInMillis();
    }
    
    public long getTime() {
        Calendar current_time = Calendar.getInstance();
        return current_time.getTimeInMillis() - start_time.getTimeInMillis();
    }
    
    public void waitTime(long ms) {
        Calendar time = Calendar.getInstance();
        long time_difference = 0;
        while(time_difference <= ms) {
            time_difference = Calendar.getInstance().getTimeInMillis() - time.getTimeInMillis();
        }
    }
}
