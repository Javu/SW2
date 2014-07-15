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
 * Unit tests for the {@link SocketThread} class. These tests also test a lot of
 * functionality for the {@link Sock} class as well.
 *
 * @author javu
 */
public class SocketThreadTest {

    /**
     * This Server is built as a listen Server by the BeforeMethod.
     */
    private Server server1;
    /**
     * This Server is built as a client Server by the BeforeMethod. Use this
     * Server to create new connections to port for testing.
     */
    private Server server2;
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
    private static final Logger LOGGER = Logger.getLogger(SocketThreadTest.class.getName());

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
     * Sets port and constructs both {@link Server}s.
     *
     * @throws IOException if either {@link Server} fails to construct.
     */
    @BeforeMethod
    private void setupSocketThread() throws IOException {
        port = 22223;
        exception = false;
        Game game = EasyMock.createMock(Game.class);
        server1 = new Server(game, port, true);
        server2 = new Server(game, port, false);
    }

    /**
     * Closes both {@link Server}s and frees up port ready for the next test.
     *
     * @throws IOException if either {@link Server} fails to close.
     */
    @AfterMethod
    private void deleteSocketThread() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        time.waitTime(wait);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        time.waitTime(wait);
    }

    /**
     * Tests the {@link SocketThread}.setHash(String hash) functions and ensures
     * it sets the value of hash correctly.
     */
    @Test
    public void testSetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetHash -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String new_hash = "Hi";
        server2.getSocketList().get(hash).setHash(new_hash);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketList().get(hash).getHash(), new_hash, "Hash not changed correctly");
        server2.getSocketList().get(hash).setHash(hash);
        LOGGER.log(Level.INFO, "----- TEST testSetHash COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.getServer() function and ensures it
     * returns the correct value of server.
     */
    @Test
    public void testGetServer() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testGetServer -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketList().get(hash).getServer(), server2, "SocketThread " + hash + " did not return the correct value for Server");
        LOGGER.log(Level.INFO, "----- TEST testGetServer COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.setRun(int state) function and ensures it
     * sets the value of state correctly.
     */
    @Test
    public void testSetRun() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetRun -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        server2.getSocketList().get(hash).setRun(SocketThread.CONFIRMED);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketList().get(hash).getRun(), SocketThread.CONFIRMED, "SocketThread " + hash + " state was not set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSetRun COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.toString() function. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToString() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToString -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        String to_string = null;
        to_string = server2.getSocketList().get(hash).toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "SocketThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "SocketThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToString COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.toString() function with a closed
     * {@link Sock}. Check the output from LOGGER to assess human readability.
     */
    @Test
    public void testToStringSockClosed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringSockClosed -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        Sock sock = null;
        try {
            sock = new Sock("127.0.0.1", port);
        } catch (IOException e) {
            exception = true;
        }
        SocketThread new_socket_thread = new SocketThread(sock, server2, "TEST");
        time.waitTime(wait);
        try {
            new_socket_thread.getSocket().close();
        } catch (IOException e) {
            exception = true;
        }
        String to_string = null;
        to_string = new_socket_thread.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "SocketThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "SocketThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringSockClosed COMPLETED -----");
    }
}
