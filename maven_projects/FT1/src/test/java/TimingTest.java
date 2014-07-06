import fantasyteam.ft1.Timing;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Study
 */
public class TimingTest {
    
    private Timing timer;
    
    protected static final Logger LOGGER = Logger.getLogger(TimingTest.class.getName());
    
    @BeforeMethod
    private void setupTiming() {
        timer = new Timing();
    }
    
    @Test
    public void testWaitTime() {
        LOGGER.log(Level.INFO,"--- START TEST testWaitTime ---");
        timer.waitTime(5000);
        LOGGER.log(Level.INFO,"--- FINISH TEST ---");
    }
}
