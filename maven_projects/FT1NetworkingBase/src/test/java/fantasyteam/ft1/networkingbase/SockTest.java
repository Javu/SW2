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
 * Unit tests for the {@link Sock} class. Please note, a lot of this classes
 * testing is also handled in the test class ServerTest and SocketThreadTest.
 *
 * @author javu
 */
public class SockTest {

    /**
     * Sock class used for all Sock testing. This Sock is built in the
     * BeforeMethod.
     */
    private Sock sock;
    /**
     * IP address to connect to.
     */
    private String ip;
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
     * Server class used for all Sock testing. This Server is built in the
     * BeforeMethod and the ListenThread is started ready for testing.
     */
    private Server server;
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
    private static final Logger LOGGER = Logger.getLogger(SockTest.class.getName());

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
     * Ensures that the specified Sock is closed.
     *
     * @param sock The Sock to check if closed.
     */
    private void waitSockClose(Sock sock) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if ((sock.getSocket() == null && sock.getIn() == null && sock.getOut() == null) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertEquals(sock.getSocket(), null, "Sock socket not set to null");
        Assert.assertEquals(sock.getIn(), null, "Sock in not set to null");
        Assert.assertEquals(sock.getOut(), null, "Sock out not set to null");
    }

    /**
     * Constructs {@link Server}, starts the {@link ListenThread} and constructs
     * a new {@link Sock} connected to the {@link Server}.
     *
     * @throws IOException if {@link ListenThread} fails to start or the
     * connection fails to connect.
     */
    @BeforeMethod
    private void setupSock() throws IOException {
        ip = "127.0.0.1";
        port = 22227;
        Game game = EasyMock.createMock(Game.class);
        server = new Server(game, port, true);
        server.startThread();
        waitListenThreadStart(server);
        sock = new Sock(ip, port);
        waitSocketThreadAddNotEmpty(server);
        exception = false;
    }

    /**
     * Closes the {@link Sock} and the {@link Server} and frees up port ready
     * for the next test.
     *
     * @throws IOException if the {@link Sock} or the {@link Server} fail to
     * close.
     */
    @AfterMethod
    private void deleteSock() throws IOException {
        sock.close();
        waitSockClose(sock);
        server.close();
        waitServerState(server, Server.CLOSED);
    }

    /**
     * Tests the default constructor for {@link Sock} and ensures it sets the
     * attributes correctly.
     */
    @Test
    public void testDefaultConstructor() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testDefaultConstructor -----");
        Sock sock1 = new Sock();
        Assert.assertEquals(sock1.getSocket(), null, "Value of socket in Sock not set to null");
        Assert.assertEquals(sock1.getOut(), null, "Value of out in Sock not set to null");
        Assert.assertEquals(sock1.getIn(), null, "Value of in in Sock not set to null");
        LOGGER.log(Level.INFO, "----- TEST testDefaultConstructor COMPLETED -----");
        try {
            sock1.close();
        } catch (IOException ex) {
            exception = true;
        }
        waitSockClose(sock1);
        Assert.assertFalse(exception, "Exception found");
    }

    /**
     * Tests the {@link Sock}.toString() function. Check the output from LOGGER
     * to assess human readability.
     */
    @Test
    public void testToString() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToString -----");
        String to_string = null;
        to_string = sock.toString();
        Assert.assertNotEquals(to_string, null, "Sock data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Sock String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToString COMPLETED -----");
    }
}
