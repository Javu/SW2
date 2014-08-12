import fantasyteam.ft1.Timing;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Javu
 */
public class TimingTest {
    
    private Timing timer;
    
    private static final Logger LOGGER = Logger.getLogger(TimingTest.class.getName());
    
    @BeforeMethod
    private void setupTiming() {
        timer = new Timing();
    }
    
    @Test
    public void testWaitTime() {
        LOGGER.log(Level.INFO,"--- START TEST testWaitTime ---");
        timer.waitTime(50);
        LOGGER.log(Level.INFO,"--- FINISH TEST ---");
    }
}
