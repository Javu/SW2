package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author javu
 */
public class ListenThreadTest {
    
    private Server server;
    private int port;
    private boolean exception;
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    int wait = 30;
    
    protected static final Logger LOGGER = Logger.getLogger(ListenThreadTest.class.getName());
    
    /**
     * waitTime tells the test to wait for a specified amount of time, which is
     * useful when dealing with sockets and connections as they need to be given
     * a small amount of time before being able to perform certain tasks on
     * them. Without this short wait period a lot of these tests would fail.
     * This also serves as a benchmark to see how quickly networking tasks can
     * be performed after trying to connect sockets and other networking tasks.
     */
    private void waitTime() {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @BeforeMethod
    private void setupListenThread() throws IOException {
        port = 22224;
        Game game = EasyMock.createMock(Game.class);
        server = new Server(game,port,true);
        server.startThread();
        waitTime();
        exception = false;
    }
    
    @AfterMethod
    private void deleteListenThread() throws IOException {
        server.close();
        waitTime();
    }
    
    @Test
    public void testToString() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToString -----");
        String to_string = null;
        to_string = server.getListenThread().toString();
        Assert.assertNotEquals(to_string, null, "ListenThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "ListenThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToString COMPLETED -----");
    }
}
