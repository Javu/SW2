package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link ListenThread} class. Please note, a lot of this
 * classes testing is also handled in the test class ServerTest.
 *
 * @author javu
 */
public class ListenThreadTest {

    /**
     * Server class used for all ListenThread testing. This Server is built in
     * the BeforeMethod and the ListenThread is started ready for testing.
     */
    private Server server;
    /**
     * Port number used to listen on.
     */
    private int port;
    /**
     * This boolean is set to true in any test if an exception is found. The
     * test should the assert that this boolean is false to ensure no exceptions
     * were encountered during testing.
     */
    private boolean exception;
    /**
     * Global Timing for use by any test if real time testing is needed.
     */
    private Timing time = new Timing();
    /**
     * This long is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    private long wait = 10;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(ListenThreadTest.class.getName());

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

    /**
     * This is run before every test to ensure basic setup is done ready for
     * testing.
     *
     * @throws IOException if {@link ListenThread} fails to start.
     */
    @BeforeMethod
    private void setupListenThread() throws IOException {
        port = 22224;
        Game game = EasyMock.createMock(Game.class);
        server = new Server(game, port, true);
        server.startThread();
        time.waitTime(wait);
        exception = false;
    }

    /**
     * Closes the {@link Server} ready to be reconstructed for the next test and
     * freeing up the port number.
     *
     * @throws IOException if {@link Server} fails to close.
     */
    @AfterMethod
    private void deleteListenThread() throws IOException {
        server.close();
        time.waitTime(wait);
    }

    /**
     * Tests the {@link ListenThread}.toString() function. Check the output from
     * LOGGER to assess human readability.
     */
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
