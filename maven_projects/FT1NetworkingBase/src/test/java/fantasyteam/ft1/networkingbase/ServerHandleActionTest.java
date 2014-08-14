package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import fantasyteam.ft1.exceptions.NetworkingIOException;
import fantasyteam.ft1.exceptions.NetworkingRuntimeException;
import fantasyteam.ft1.networkingbase.exceptions.FeatureNotUsedException;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import fantasyteam.ft1.networkingbase.exceptions.ServerSocketCloseException;
import fantasyteam.ft1.networkingbase.exceptions.TimeoutException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This set of tests are specifically used to test each valid action string that
 * can be passed to the {@link Server}.handleServerAction function to ensure
 * that the correct function is run and the action is identified correctly. For
 * a list of valid action strings and their uses see the table titled 'List of
 * valid Action Strings for Server class' in the documentation for the
 * {@link Server} class.
 *
 * @author Javu
 */
public class ServerHandleActionTest {

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
     * String used to store the identifier given to the SocketThread created in
     * BeforeMethod by running server2.addSocket(). Can be used to get the
     * MessageQueue stored on this Server.
     */
    private String hash;
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
    private static final Logger LOGGER = Logger.getLogger(ServerHandleActionTest.class.getName());

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
     * Ensures the queue_list attribute of Server is not empty.
     *
     * @param server The Server to check queue_list on.
     */
    private void waitMessageQueueAddNotEmpty(Server server) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (!server.getQueueList().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertFalse(server.getQueueList().isEmpty(), "MessageQueue was not constructed");
    }

    /**
     * Checks the state of a MessageQueue on a Server. Use this when waiting for
     * a new MessageQueue to start before continuing, or waiting for a
     * MessageQueue to register that it should be disconnected.
     *
     * @param server The Server containing the MessageQueue.
     * @param hash the hash of the MessageQueue.
     * @param state the state expected on the MessageQueue.
     */
    private void waitMessageQueueState(Server server, String hash, int state) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getQueueList().get(hash).getRun() == state || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertEquals(server.getQueueList().get(hash).getRun(), state, "MessageQueue state was not set correctly");
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
     * Constructs both {@link Server}s, starts the {@link ListenThread} and
     * creates a connection between the {@link Server}s.
     *
     * @throws IOException if {@link ListenThread} fails to start or the
     * connection fails to connect.
     */
    @BeforeMethod
    private void setupServer() throws IOException, ServerSocketCloseException, TimeoutException, FeatureNotUsedException {
        port = 22222;
        exception = false;
        Game game = EasyMock.createMock(Game.class);
        LOGGER.log(Level.INFO, "Building Server1");
        server1 = new Server(game, port, true);
        LOGGER.log(Level.INFO, "Building Server2");
        server2 = new Server(game, port, false);
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        server1.startThread();
        waitListenThreadStart(server1);
        hash = server2.addSocket("127.0.0.1", port);
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
    }

    /**
     * Closes both {@link Server}s and frees up port ready for the next test.
     *
     * @throws IOException if either {@link Server} fails to close.
     */
    @AfterMethod
    private void deleteServer() throws IOException, ServerSocketCloseException, TimeoutException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        waitServerState(server2, Server.CLOSED);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitServerState(server1, Server.CLOSED);
    }

    /**
     * Tests the action string setPort, ensuring it correctly changes the port
     * number on the {@link Server}.
     */
    @Test
    public void testSHMSetPort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSHMSetPort -----");
        String action = "setPort";
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("25467");
        try {
            server2.handleAction(action, parameters);
        } catch (NetworkingIOException | NetworkingRuntimeException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getPort(), 25467, "Value for port was not set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSHMSetPort COMPLETED -----");
    }

    /**
     * Exception test for setPort action string. Ensures that an
     * InvalidArgumentException is correctly wrapped inside a
     * NetworkingIOException and thrown when the number of parameters specified
     * is too small.
     */
    @Test
    public void testSHMSetPortParamEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSHMSetPortParamEx -----");
        boolean exception_correct = false;
        String action = "setPort";
        ArrayList<String> parameters = new ArrayList<String>();
        try {
            server2.handleAction(action, parameters);
        } catch (NetworkingIOException | NetworkingRuntimeException e) {
            try {
                throw e.getCause();
            } catch (InvalidArgumentException ex) {
                exception_correct = true;
                LOGGER.log(Level.INFO, "{0}", ex.getMessage());
            } catch (Throwable ex) {
                exception = true;
                LOGGER.log(Level.INFO, "{0}", ex.getMessage());
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(exception_correct, "Cause not correctly set as an InvalidArgumentException");
        LOGGER.log(Level.INFO, "----- TEST testSHMSetPortParamEx COMPLETED -----");
    }

    /**
     * Exception test for the setPort action string. Ensures that the setPort
     * correctly throws an InvalidArgumentException wrapped inside a
     * NetworkingRuntimeException if its the port number is outside its own self
     * validated range.
     */
    @Test
    public void testSHMSetPortInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSHMSetPortInvalidEx -----");
        boolean exception_correct = false;
        String action = "setPort";
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("70000");
        try {
            server2.handleAction(action, parameters);
        } catch (NetworkingIOException | NetworkingRuntimeException e) {
            try {
                throw e.getCause();
            } catch (InvalidArgumentException ex) {
                exception_correct = true;
                LOGGER.log(Level.INFO, "{0}", ex.getMessage());
            } catch (Throwable ex) {
                exception = true;
                LOGGER.log(Level.INFO, "{0}", ex.getMessage());
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(exception_correct, "Cause not correctly set as an InvalidArgumentException");
        LOGGER.log(Level.INFO, "----- TEST testSHMSetPortInvalidEx COMPLETED -----");
    }
}
