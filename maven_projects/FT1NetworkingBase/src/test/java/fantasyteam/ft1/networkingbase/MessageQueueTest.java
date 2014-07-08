package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author Javu
 */
public class MessageQueueTest {
    
    private Server server1;
    private Server server2;
    int port;
    boolean exception;
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    int wait = 30;

    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(MessageQueueTest.class.getName());

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
    private void setupServer() throws IOException {
        port = 22222;
        exception = false;
        Game game = EasyMock.createMock(Game.class);

//        game.runVoidMethod();
//        EasyMock.expect(game.parseAction()).andReturn("go fuck yoruself").anyTimes();
//        EasyMock.replay();
        LOGGER.log(Level.INFO,"Building Server1");
        server1 = new Server(game, port, true);
        LOGGER.log(Level.INFO,"Building Server2");
        server2 = new Server(game, port, false);

//        EasyMock.verify();
    }

    @AfterMethod
    private void deleteServer() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        waitTime();
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitTime();
    }
}
