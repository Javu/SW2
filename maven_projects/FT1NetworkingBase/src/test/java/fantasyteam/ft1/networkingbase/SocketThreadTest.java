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
     * The time waited before asserting that a function did not work as
     * intended.
     */
    private long timeout = 5000;
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
     * Waits for listen_thread to set run to true. Use this when running
     * Server.startThread and you want to ensure the ListenThread is ready to
     * accept connections before continuing.
     *
     * @param server The Server to check the listen_thread on.
     */
    private void waitListenThreadStart(Server server) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getListenThread().getRun() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertTrue(server.getListenThread().getRun(), "ListenThread did not start in time");
    }

    /**
     * Ensures the socket_list attribute of Server is not empty.
     *
     * @param server The Server to check socket_list on.
     */
    private void waitSocketThreadAddNotEmpty(Server server) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (!server.getSocketList().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertFalse(server.getSocketList().isEmpty(), "SocketThread was not constructed");
    }

    /**
     * Checks the state of a SocketThread on a Server. Use this when waiting for
     * a new SocketThread to start before continuing.
     *
     * @param server The Server containing the SocketThread.
     * @param hash the hash of the SocketThread.
     * @param state the state expected on the SocketThread.
     */
    private void waitSocketThreadState(Server server, String hash, int state) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getSocketList().get(hash).getRun() == state || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertEquals(server.getSocketList().get(hash).getRun(), state, "SocketThread state was not set correctly");
    }

    /**
     * Checks the state of the specified Server. Use this when waiting for a
     * Server to finish closing.
     *
     * @param server The Server to check the state of.
     * @param state The state expected on the Server.
     */
    private void waitServerState(Server server, int state) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getState() == state || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertEquals(server.getState(), state, "Server state was not set in time");
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
        waitServerState(server2, Server.CLOSED);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitServerState(server1, Server.CLOSED);
    }

    /**
     * Tests the {@link SocketThread}.setHash(String hash) functions and ensures
     * it sets the value of hash correctly.
     */
    @Test
    public void testSetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetHash -----");
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.RUNNING);
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.RUNNING);
        server2.getSocketList().get(hash).setRun(SocketThread.CONFIRMED);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketList().get(hash).getRun(), SocketThread.CONFIRMED, "SocketThread " + hash + " state was not set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSetRun COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.unblock function on a {@link SocketThread}
     * that is not running and therefore not blocking awaiting input.
     */
    @Test
    public void testUnblockNotRunning() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testCloseBeforeUnblock -----");
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        Sock new_sock = null;
        try {
            new_sock = new Sock("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        SocketThread new_thread = new SocketThread(new_sock, server2, "TEST");
        try {
            new_thread.unblock();
        } catch (IOException e) {
            exception = true;
        }
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (new_thread.getRun() == SocketThread.CLOSED || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(new_thread.getRun(), SocketThread.CLOSED, "SocketThread " + new_thread.getHash() + " state was not set correctly");
        Assert.assertEquals(new_thread.getSocket(), null, "Sock was not closed and set to null");
        if (new_sock != null) {
            try {
                new_sock.close();
            } catch (IOException ex) {
                exception = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testCloseBeforeUnblock COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.toString() function. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToString() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToString -----");
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.RUNNING);
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
        waitListenThreadStart(server1);
        Sock sock = null;
        try {
            sock = new Sock("127.0.0.1", port);
        } catch (IOException e) {
            exception = true;
        }
        SocketThread new_socket_thread = new SocketThread(sock, server2, "TEST");
        try {
            new_socket_thread.getSocket().close();
        } catch (IOException e) {
            exception = true;
        }
        boolean loop = false;
        Timing new_timer = new Timing();
        while (loop) {
            if ((new_socket_thread.getSocket().getSocket() == null && new_socket_thread.getSocket().getIn() == null && new_socket_thread.getSocket().getOut() == null) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        String to_string = null;
        to_string = new_socket_thread.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "SocketThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "SocketThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringSockClosed COMPLETED -----");
    }
}
