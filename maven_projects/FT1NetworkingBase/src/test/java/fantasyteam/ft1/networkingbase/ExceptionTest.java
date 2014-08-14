package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import fantasyteam.ft1.networkingbase.exceptions.FeatureNotUsedException;
import fantasyteam.ft1.networkingbase.exceptions.HashNotFoundException;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import fantasyteam.ft1.networkingbase.exceptions.NetworkingBaseIOException;
import fantasyteam.ft1.networkingbase.exceptions.NetworkingBaseRuntimeException;
import fantasyteam.ft1.networkingbase.exceptions.NullException;
import fantasyteam.ft1.networkingbase.exceptions.ServerSocketCloseException;
import fantasyteam.ft1.networkingbase.exceptions.TimeoutException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.easymock.EasyMock.createMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for any exception handling used throughout the networkingbase package,
 * with the exception of the {@link Server}.handleServerAction function. Tests
 * for exceptions thrown by the {@link Server}.handleServerAction function can
 * be found in the ServerHandleActionTest test file. This test class is also
 * used to test functions of custom exceptions.
 *
 * @author javu
 */
public class ExceptionTest {

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
     * The {@link Game} class that will be built as a mock class and passed to
     * both {@link Server} instances as a parameter of their constructors.
     */
    private Game game;
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
    private long wait = 20;
    /**
     * The time waited before asserting that a function did not work as
     * intended.
     */
    private long timeout = 5000;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(ExceptionTest.class.getName());

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
     * Gets the hash of the last SocketThread in the socket_list Map on Server.
     *
     * @param server The Server to check for the hash on.
     * @return the String hash for the last SocketThread
     */
    private String getServerLastSocketHash(Server server) {
        String server_hash = "";
        for (SocketThread socket : server.getSocketList().values()) {
            server_hash = socket.getHash();
        }
        return server_hash;
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
     * Ensures the socket_list attribute of Server is empty.
     *
     * @param server The Server to check socket_list on.
     */
    private void waitSocketThreadRemoveEmpty(Server server) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getSocketList().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertTrue(server.getSocketList().isEmpty(), "SocketThread was not removed");
    }

    /**
     * Ensures the queue_list attribute of Server is empty.
     *
     * @param server The Server to check queue_list on.
     */
    private void waitMessageQueueRemoveEmpty(Server server) {
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server.getQueueList().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertTrue(server.getQueueList().isEmpty(), "MessageQueue was not removed");
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
    private void setupServer() throws IOException, ServerSocketCloseException, TimeoutException {
        port = 22222;
        exception = false;
        game = createMock(Game.class);
        LOGGER.log(Level.INFO, "Building Server1");
        server1 = new Server(game, port, true);
        LOGGER.log(Level.INFO, "Building Server2");
        server2 = new Server(game, port, false);
    }

    /**
     * Closes both {@link Server}s and frees up port ready for the next test.
     *
     * @throws IOException if either {@link Server} fails to close.
     */
    @AfterMethod
    private void deleteServer() throws IOException, ServerSocketCloseException, TimeoutException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        if (server2.getState() != Server.CLOSED) {
            server2.close();
        }
        waitServerState(server2, Server.CLOSED);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        if (server1.getState() != Server.CLOSED) {
            server1.close();
        }
        waitServerState(server1, Server.CLOSED);
        LOGGER.log(Level.INFO, "+++++ CLOSING SERVERS COMPLETE +++++");
    }

    /**
     * Tests the {@link Server}.setPort function to ensure it correctly throws
     * an InvalidArgumentException if the parameter passed fails the input
     * validation.
     */
    @Test
    public void testServerSetPortEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetPortEx -----");
        try {
            server2.setPort(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully ran setPort on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetPortEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setTimeout function to ensure it correctly
     * throws an InvalidArgumentException if the parameter passed fails the
     * input validation.
     */
    @Test
    public void testServerSetTimeoutEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetTimeoutEx -----");
        try {
            server2.setTimeout(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully ran setTimeout on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetTimeoutEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setSocketTimeout function to ensure it correctly
     * throws an InvalidArgumentException if the parameter passed fails the
     * input validation.
     */
    @Test
    public void testServerSetSocketTimeoutEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetSocketTimeoutEx -----");
        boolean exception_socket = false;
        try {
            server2.setSocketTimeout(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (SocketException e) {
            exception_socket = true;
        }
        Assert.assertFalse(exception_socket, "SocketException received");
        Assert.assertTrue(exception, "Successfully ran setSocketTimeout on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetSocketTimeoutEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setSocketTimeoutCount function to ensure it
     * correctly throws an InvalidArgumentException if the parameter passed
     * fails the input validation.
     */
    @Test
    public void testServerSetSocketTimeoutCountEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetSocketTimeoutCountEx -----");
        boolean exception_socket = false;
        try {
            server2.setSocketTimeoutCount(-2);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (SocketException e) {
            exception_socket = true;
        }
        Assert.assertFalse(exception_socket, "SocketException received");
        Assert.assertTrue(exception, "Successfully ran setSocketTimeoutCount on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetSocketTimeoutCountEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setSocketGame function to ensure it correctly
     * throws a NullException if socket_list is set to null.
     */
    @Test
    public void testServerSetSocketGameNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetSocketGameNullEx -----");
        server2.setSocketList(null);
        boolean exception_socket = false;
        try {
            server2.setSocketGame("TEST", 1);
        } catch (HashNotFoundException e) {
            exception_socket = true;
        } catch (NullException e) {
            exception = true;
        }
        Assert.assertFalse(exception_socket, "HashNotFoundException received");
        Assert.assertTrue(exception, "Successfully ran setSocketGame on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetSocketGameNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setSocketGame function to ensure it correctly
     * throws a HashNotFoundException if the String parameter passed does not
     * exist as a key in socket_list.
     */
    @Test
    public void testServerSetSocketGameHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetSocketGameHashEx -----");
        boolean exception_socket = false;
        try {
            server2.setSocketGame("TEST", 1);
        } catch (NullException e) {
            exception_socket = true;
        } catch (HashNotFoundException e) {
            exception = true;
        }
        Assert.assertFalse(exception_socket, "NullException received");
        Assert.assertTrue(exception, "Successfully ran setSocketGame on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetSocketGameHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getSocketGame function to ensure it correctly
     * throws a NullException if socket_list is set to null.
     */
    @Test
    public void testServerGetSocketGameNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetSocketGameNullEx -----");
        server2.setSocketList(null);
        boolean exception_socket = false;
        try {
            server2.getSocketGame("TEST");
        } catch (HashNotFoundException e) {
            exception_socket = true;
        } catch (NullException e) {
            exception = true;
        }
        Assert.assertFalse(exception_socket, "HashNotFoundException received");
        Assert.assertTrue(exception, "Successfully ran getSocketGame on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetSocketGameNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getSocketGame function to ensure it correctly
     * throws a HashNotFoundException if the String parameter passed does not
     * exist as a key in socket_list.
     */
    @Test
    public void testServerGetSocketGameHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetSocketGameHashEx -----");
        boolean exception_socket = false;
        try {
            server2.getSocketGame("TEST");
        } catch (NullException e) {
            exception_socket = true;
        } catch (HashNotFoundException e) {
            exception = true;
        }
        Assert.assertFalse(exception_socket, "NullException received");
        Assert.assertTrue(exception, "Successfully ran getSocketGame on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetSocketGameHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutError function to ensure it
     * correctly throws an InvalidArgumentException if the value passed does not
     * pass input validation.
     */
    @Test
    public void testServerSetQueueTimeoutErrorInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutErrorInvalidEx -----");
        try {
            server2.setQueueTimeoutError(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutError on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutErrorInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutDisconnect function to ensure it
     * correctly throws an InvalidArgumentException if the value passed does not
     * pass input validation.
     */
    @Test
    public void testServerSetQueueTimeoutDisconnectInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutDisconnectInvalidEx -----");
        try {
            server2.setQueueTimeoutDisconnect(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutDisconnect on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutDisconnectInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a FeatureNotUsedException if the
     * {@link MessageQueue} feature has not been activated.
     */
    @Test
    public void testServerSetQueueTimeoutErrorIndividualFeatureEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutErrorIndividualFeatureEx -----");
        boolean exception_other = false;
        try {
            server2.setQueueTimeoutErrorIndividual("TEST", -1);
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (NullException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutErrorIndividualFeatureEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerSetQueueTimeoutErrorIndividualNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutErrorIndividualNullEx -----");
        boolean exception_other = false;
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        server2.setQueueList(null);
        try {
            server2.setQueueTimeoutErrorIndividual("TEST", -1);
        } catch (NullException e) {
            exception = true;
        } catch (FeatureNotUsedException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutErrorIndividualNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a HashNotFoundException if the value specified
     * does not correspond to a key in queue_list.
     */
    @Test
    public void testServerSetQueueTimeoutErrorIndividualHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutErrorIndividualHashEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.setQueueTimeoutErrorIndividual("TEST", -1);
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutErrorIndividualHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutErrorIndividual function to
     * ensure it correctly throws an InvalidArgumentException if the value
     * passed does not pass input validation.
     */
    @Test
    public void testServerSetQueueTimeoutErrorIndividualInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutErrorIndividualInvalidEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.setQueueTimeoutErrorIndividual(client_hash, -1);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutErrorIndividualInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a FeatureNotUsedException if the
     * {@link MessageQueue} feature has not been activated.
     */
    @Test
    public void testServerSetQueueTimeoutDisconnectIndividualFeatureEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutDisconnectIndividualFeatureEx -----");
        boolean exception_other = false;
        try {
            server2.setQueueTimeoutDisconnectIndividual("TEST", -1);
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (NullException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutDisconnectIndividualFeatureEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerSetQueueTimeoutDisconnectIndividualNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutDisconnectIndividualNullEx -----");
        boolean exception_other = false;
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        server2.setQueueList(null);
        try {
            server2.setQueueTimeoutDisconnectIndividual("TEST", -1);
        } catch (NullException e) {
            exception = true;
        } catch (FeatureNotUsedException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutDisconnectIndividualNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a HashNotFoundException if the value specified
     * does not correspond to a key in queue_list.
     */
    @Test
    public void testServerSetQueueTimeoutDisconnectIndividualHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutDisconnectIndividualHashEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.setQueueTimeoutDisconnectIndividual("TEST", -1);
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutDisconnectIndividualHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.setQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws an InvalidArgumentException if the value
     * passed does not pass input validation.
     */
    @Test
    public void testServerSetQueueTimeoutDisconnectIndividualInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSetQueueTimeoutDisconnectIndividualInvalidEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.setQueueTimeoutDisconnectIndividual(client_hash, -1);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran setQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerSetQueueTimeoutDisconnectIndividualInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a FeatureNotUsedException if the
     * {@link MessageQueue} feature has not been activated.
     */
    @Test
    public void testServerGetQueueTimeoutErrorIndividualFeatureEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutErrorIndividualFeatureEx -----");
        boolean exception_other = false;
        try {
            server2.getQueueTimeoutErrorIndividual("TEST");
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (NullException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutErrorIndividualFeatureEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerGetQueueTimeoutErrorIndividualNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutErrorIndividualNullEx -----");
        boolean exception_other = false;
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        server2.setQueueList(null);
        try {
            server2.getQueueTimeoutErrorIndividual("TEST");
        } catch (NullException e) {
            exception = true;
        } catch (FeatureNotUsedException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutErrorIndividualNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutErrorIndividual function to
     * ensure it correctly throws a HashNotFoundException if the value specified
     * does not correspond to a key in queue_list.
     */
    @Test
    public void testServerGetQueueTimeoutErrorIndividualHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutErrorIndividualHashEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.getQueueTimeoutErrorIndividual("TEST");
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutErrorIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutErrorIndividualHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a FeatureNotUsedException if the
     * {@link MessageQueue} feature has not been activated.
     */
    @Test
    public void testServerGetQueueTimeoutDisconnectIndividualFeatureEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutDisconnectIndividualFeatureEx -----");
        boolean exception_other = false;
        try {
            server2.getQueueTimeoutDisconnectIndividual("TEST");
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (NullException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutDisconnectIndividualFeatureEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerGetQueueTimeoutDisconnectIndividualNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutDisconnectIndividualNullEx -----");
        boolean exception_other = false;
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        server2.setQueueList(null);
        try {
            server2.getQueueTimeoutDisconnectIndividual("TEST");
        } catch (NullException e) {
            exception = true;
        } catch (FeatureNotUsedException | HashNotFoundException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutDisconnectIndividualNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.getQueueTimeoutDisconnectIndividual function to
     * ensure it correctly throws a HashNotFoundException if the value specified
     * does not correspond to a key in queue_list.
     */
    @Test
    public void testServerGetQueueTimeoutDisconnectIndividualHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerGetQueueTimeoutDisconnectIndividualHashEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        try {
            server2.getQueueTimeoutDisconnectIndividual("TEST");
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (FeatureNotUsedException | NullException | InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Caught an unexpected exception");
        Assert.assertTrue(exception, "Successfully ran getQueueTimeoutDisconnectIndividual on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerGetQueueTimeoutDisconnectIndividualHashEx COMPLETED -----");
    }
    
    /**
     * Tests the {@link Server}.removeDisconnectedSocket function to ensure it
     * correctly throws a NullException if disconnected_sockets is set to null.
     */
    @Test
    public void testServerRemoveDisconnectedSocketEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerRemoveDisconnectedSocketEx -----");
        boolean exception_hash = false;
        server1.setDisconnectedSockets(null);
        try {
            server1.removeDisconnectedSocket("a");
        } catch (NullException e) {
            exception = true;
        } catch (HashNotFoundException e) {
            exception_hash = true;
        }
        Assert.assertFalse(exception_hash, "HashNotFoundException received");
        Assert.assertTrue(exception, "Successfully ran removeDisconnectedSocket on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerRemoveDisconnectedSocketEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startSocket function to ensure it correctly
     * throws a HashNotFoundException if the String passed does not exist as a
     * key in socket_list.
     */
    @Test
    public void testServerStartSocketHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerStartSocketHashEx -----");
        boolean exception_other = false;
        try {
            server1.startSocket("a");
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (TimeoutException | NullException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a HashNotFoundException");
        Assert.assertTrue(exception, "Successfully ran startSocket on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerStartSocketHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startSocket function to ensure it correctly
     * throws a NullException if socket_list is set to null.
     */
    @Test
    public void testServerStartSocketNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerStartSocketNullEx -----");
        boolean exception_other = false;
        server1.setSocketList(null);
        try {
            server1.startSocket("a");
        } catch (NullException e) {
            exception = true;
        } catch (TimeoutException | HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a NullException");
        Assert.assertTrue(exception, "Successfully ran startSocket on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerStartSocketNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startQueue function to ensure it correctly
     * throws a HashNotFoundException if the String passed does not exist as a
     * key in queue_list.
     */
    @Test
    public void testServerStartQueueHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerStartQueueHashEx -----");
        boolean exception_other = false;
        try {
            server1.startQueue("a");
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (TimeoutException | NullException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a HashNotFoundException");
        Assert.assertTrue(exception, "Successfully ran startQueue on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerStartQueueHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startQueue function to ensure it correctly
     * throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerStartQueueNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerStartQueueNullEx -----");
        boolean exception_other = false;
        server1.setQueueList(null);
        try {
            server1.startQueue("a");
        } catch (NullException e) {
            exception = true;
        } catch (TimeoutException | HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a NullException");
        Assert.assertTrue(exception, "Successfully ran startQueue on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerStartQueueNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startQueue function to ensure it correctly
     * throws a FeatureNotUsedException if the {@link MessageQueue} feature is
     * turned off by setting use_message_queues to false.
     */
    @Test
    public void testServerAddQueueNotUsedEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerAddQueueNotUsedEx -----");
        boolean exception_other = false;
        try {
            server1.addQueue("a");
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (TimeoutException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a FeatureNotUsedException");
        Assert.assertTrue(exception, "Successfully ran addQueue on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerAddQueueNotUsedEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.replaceHash function to ensure it correctly
     * throws an InvalidArgumentException if the parameters passed fail the
     * input validation.
     */
    @Test
    public void testServerReplaceHashInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerReplaceHashInvalidEx -----");
        boolean exception_other = false;
        String client_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.replaceHash(client_hash, client_hash);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not an InvalidArgumentException");
        Assert.assertTrue(exception, "Successfully ran replaceHash on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerReplaceHashInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.replaceHash function to ensure it correctly
     * throws a HashNotFoundException if the String passed as old_hash does not
     * exist as a key in socket_list.
     */
    @Test
    public void testServerReplaceHashHashEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerReplaceHashHashEx -----");
        boolean exception_other = false;
        try {
            server2.replaceHash("TEST", "TEST");
        } catch (HashNotFoundException e) {
            exception = true;
        } catch (InvalidArgumentException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a HashNotFoundException");
        Assert.assertTrue(exception, "Successfully ran replaceHash on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerReplaceHashHashEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.connectDisconnectedSocket function to ensure it
     * correctly throws an InvalidArgumentException if the parameters passed
     * fail the input validation.
     */
    @Test
    public void testServerConnectDisconnectedSocketInvalidEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConnectDisconnectedSocketInvalidEx -----");
        boolean exception_feature = false;
        boolean exception_hash = false;
        String test_string = "TEST";
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add(test_string);
        server2.setUseDisconnectedSockets(true);
        server2.setDisconnectedSockets(string_array);
        Assert.assertEquals(server2.getDisconnectedSockets().get(0), test_string, "Disconnected Sockets not set correctly");
        try {
            server2.connectDisconnectedSocket(test_string, test_string);
        } catch (InvalidArgumentException e) {
            exception = true;
        } catch (FeatureNotUsedException e) {
            exception_feature = true;
        } catch (HashNotFoundException e) {
            exception_hash = true;
        }
        Assert.assertFalse(exception_feature, "Caught a FeatureNotUsedException, expected InvalidArgumentException");
        Assert.assertFalse(exception_hash, "Caught a HashNotFoundException, expected InvalidArgumentException");
        Assert.assertTrue(exception, "Successfully ran connectDisconnectedSocket on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerConnectDisconnectedSocketInvalidEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.connectDisconnectedSocket function to ensure it
     * correctly throws a FeatureNotUsedException if the disconnection feature
     * is turned off by setting use_disconnected_sockets to false.
     */
    @Test
    public void testServerConnectDisconnectedSocketFeatureEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConnectDisconnectedSocketFeatureEx -----");
        boolean exception_other = false;
        try {
            server2.connectDisconnectedSocket("TEST", "TEST");
        } catch (FeatureNotUsedException e) {
            exception = true;
        } catch (InvalidArgumentException | HashNotFoundException e) {
            exception_other = true;
        }
        Assert.assertFalse(exception_other, "Exception caught was not a FeatureNotUsedException");
        Assert.assertTrue(exception, "Successfully ran connectDisconnectedSocket on server, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerConnectDisconnectedSocketFeatureEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.removeQueue function to ensure it correctly
     * throws a NullException if queue_list is set to null.
     */
    @Test
    public void testServerRemoveQueueNullEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerRemoveQueueNullEx -----");
        boolean queue_null = false;
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        server1.setQueueList(null);
        try {
            server1.removeQueue("TEST");
        } catch (NullException e) {
            queue_null = true;
        } catch (HashNotFoundException e) {
            exception = true;
        }
        Assert.assertTrue(queue_null, "Successfully removed queue, should have received a NullException error");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerRemoveQueueNullEx COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.startQueue function to ensure it correctly
     * throws an exception if the String passed does not exist in
     * disconnected_sockets.
     */
    @Test
    public void testServerRemoveDisconnectedSocketNotExistEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerRemoveDisconnectedSocketNotExistEx -----");
        try {
            server1.removeDisconnectedSocket("TEST");
        } catch (HashNotFoundException | NullException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully removed disconnected socket, should have received an exception");
        LOGGER.log(Level.INFO, "----- TEST testServerRemoveDisconnectedSocketNotExistEx COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.setTimeoutError function to ensure it
     * correctly throws an InvalidArgumentException if the parameter passed
     * fails the input validation.
     */
    @Test
    public void testMessageQueueSetTimeoutErrorEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetTimeoutErrorEx -----");
        boolean exception_other = false;
        String hash = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
        try {
            server2.getQueueList().get(hash).setTimeoutError(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertFalse(exception_other, "Unexpected exception found");
        Assert.assertTrue(exception, "Exception not found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetTimeoutErrorEx COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.setTimeoutDisconnect function to ensure it
     * correctly throws an InvalidArgumentException if the parameter passed
     * fails the input validation.
     */
    @Test
    public void testMessageQueueSetTimeoutDisconnectEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetTimeoutDisconnectEx -----");
        boolean exception_other = false;
        String hash = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
        try {
            server2.getQueueList().get(hash).setTimeoutDisconnect(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertFalse(exception_other, "Unexpected exception found");
        Assert.assertTrue(exception, "Exception not found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetTimeoutDisconnectEx COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.setRun function to ensure it correctly
     * throws an InvalidArgumentException if the parameter passed fails the
     * input validation.
     */
    @Test
    public void testMessageQueueSetRunEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetRunEx -----");
        boolean exception_other = false;
        String hash = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception_other = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception_other = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception_other = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
        try {
            server2.getQueueList().get(hash).setRun(-1);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertFalse(exception_other, "Unexpected exception found");
        Assert.assertTrue(exception, "Exception not found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetRunEx COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.setRun function to ensure it correctly
     * throws an InalidArgumentException if a correct state is not passed to the
     * function.
     */
    @Test
    public void testSocketThreadSetRunEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSocketThreadSetRunEx -----");
        String client_hash = "";
        boolean exception_expected = false;
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.getSocketList().get(client_hash).setRun(-10);
        } catch (InvalidArgumentException e) {
            exception_expected = true;
        }
        Assert.assertFalse(exception, "Unexpected exception found");
        Assert.assertTrue(exception_expected, "Successfully ran setRun, expected an exception");
        LOGGER.log(Level.INFO, "----- TEST testSocketThreadSetRunEx COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.setTimeout function to ensure it correctly
     * throws an InalidArgumentException if a correct timeout value is not
     * passed to the function.
     */
    @Test
    public void testSocketThreadSetTimeoutEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSocketThreadSetTimeoutEx -----");
        String client_hash = "";
        boolean exception_expected = false;
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.getSocketList().get(client_hash).setTimeout(-10);
        } catch (InvalidArgumentException e) {
            exception_expected = true;
        }
        Assert.assertFalse(exception, "Unexpected exception found");
        Assert.assertTrue(exception_expected, "Successfully ran setTimeout, expected an exception");
        LOGGER.log(Level.INFO, "----- TEST testSocketThreadSetTimeoutEx COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.setSocketTimeout function to ensure it
     * correctly throws an InalidArgumentException if a correct timeout value is
     * not passed to the function.
     */
    @Test
    public void testSocketThreadSetSocketTimeoutEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSocketThreadSetSocketTimeoutEx -----");
        String client_hash = "";
        boolean exception_expected = false;
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.getSocketList().get(client_hash).setSocketTimeout(-10);
        } catch (InvalidArgumentException e) {
            exception_expected = true;
        } catch (SocketException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Unexpected exception found");
        Assert.assertTrue(exception_expected, "Successfully ran setSocketTimeout, expected an exception");
        LOGGER.log(Level.INFO, "----- TEST testSocketThreadSetSocketTimeoutEx COMPLETED -----");
    }

    /**
     * Tests the {@link SocketThread}.setSocketTimeoutCount function to ensure
     * it correctly throws an InalidArgumentException if a correct count value
     * is not passed to the function.
     */
    @Test
    public void testSocketThreadSetSocketTimeoutCountEx() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSocketThreadSetSocketTimeoutCountEx -----");
        String client_hash = "";
        boolean exception_expected = false;
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.getSocketList().get(client_hash).setSocketTimeoutCount(-10);
        } catch (InvalidArgumentException e) {
            exception_expected = true;
        }
        Assert.assertFalse(exception, "Unexpected exception found");
        Assert.assertTrue(exception_expected, "Successfully ran setSocketTimeoutCount, expected an exception");
        LOGGER.log(Level.INFO, "----- TEST testSocketThreadSetSocketTimeoutCountEx COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link FeatureNotUsedException}.
     */
    @Test
    public void testFeatureNotUsedException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testFeatureNotUsedException -----");
        try {
            throw new FeatureNotUsedException("TEST");
        } catch (FeatureNotUsedException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testFeatureNotUsedException COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link HashNotFoundException}.
     */
    @Test
    public void testHashNotFoundException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testHashNotFoundException -----");
        try {
            throw new HashNotFoundException("TEST");
        } catch (HashNotFoundException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testHashNotFoundException COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link InvalidArgumentException}.
     */
    @Test
    public void testInvalidArgumentException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testInvalidArgumentException -----");
        try {
            throw new InvalidArgumentException("TEST");
        } catch (InvalidArgumentException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testInvalidArgumentException COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link NullException}.
     */
    @Test
    public void testNullException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNullException -----");
        try {
            throw new NullException("TEST");
        } catch (NullException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNullException COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link ServerSocketCloseException}.
     */
    @Test
    public void testServerSocketCloseException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSocketCloseException -----");
        try {
            throw new ServerSocketCloseException("TEST");
        } catch (ServerSocketCloseException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testServerSocketCloseException COMPLETED -----");
    }

    /**
     * Tests the message only constructor of {@link TimeoutException}.
     */
    @Test
    public void testTimeoutException() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testTimeoutException -----");
        try {
            throw new TimeoutException("TEST");
        } catch (TimeoutException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testTimeoutException COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link FeatureNotUsedException}.
     */
    @Test
    public void testFeatureNotUsedExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testFeatureNotUsedExceptionThrowable -----");
        try {
            throw new FeatureNotUsedException("TEST", new Throwable());
        } catch (FeatureNotUsedException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testFeatureNotUsedExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link HashNotFoundException}.
     */
    @Test
    public void testHashNotFoundExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testHashNotFoundExceptionThrowable -----");
        try {
            throw new HashNotFoundException("TEST", new Throwable());
        } catch (HashNotFoundException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testHashNotFoundExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link InvalidArgumentException}.
     */
    @Test
    public void testInvalidArgumentExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testInvalidArgumentExceptionThrowable -----");
        try {
            throw new InvalidArgumentException("TEST", new Throwable());
        } catch (InvalidArgumentException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testInvalidArgumentExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of {@link NullException}.
     */
    @Test
    public void testNullExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNullExceptionThrowable -----");
        try {
            throw new NullException("TEST", new Throwable());
        } catch (NullException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNullExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link ServerSocketCloseException}.
     */
    @Test
    public void testServerSocketCloseExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSocketCloseExceptionThrowable -----");
        try {
            throw new ServerSocketCloseException("TEST", new Throwable());
        } catch (ServerSocketCloseException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testServerSocketCloseExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of {@link TimeoutException}.
     */
    @Test
    public void testTimeoutExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testTimeoutExceptionThrowable -----");
        try {
            throw new TimeoutException("TEST", new Throwable());
        } catch (TimeoutException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testTimeoutExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message only constructor of
     * {@link testNetworkingBaseRuntimeExceptionMessage}.
     */
    @Test
    public void testNetworkingBaseRuntimeExceptionMessage() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseRuntimeExceptionMessage -----");
        try {
            throw new NetworkingBaseRuntimeException("TEST");
        } catch (NetworkingBaseRuntimeException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseRuntimeExceptionMessage COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link testNetworkingBaseRuntimeExceptionMessage}.
     */
    @Test
    public void testNetworkingBaseRuntimeExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseRuntimeExceptionThrowable -----");
        try {
            throw new NetworkingBaseRuntimeException("TEST", new Throwable());
        } catch (NetworkingBaseRuntimeException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseRuntimeExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the message only constructor of
     * {@link testNetworkingBaseIOExceptionMessage}.
     */
    @Test
    public void testNetworkingBaseIOExceptionMessage() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseIOExceptionMessage -----");
        try {
            throw new NetworkingBaseIOException("TEST");
        } catch (NetworkingBaseIOException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseIOExceptionMessage COMPLETED -----");
    }

    /**
     * Tests the message and throwable constructor of
     * {@link testNetworkingBaseIOExceptionMessage}.
     */
    @Test
    public void testNetworkingBaseIOExceptionThrowable() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseIOExceptionThrowable -----");
        try {
            throw new NetworkingBaseIOException("TEST", new Throwable());
        } catch (NetworkingBaseIOException e) {
            LOGGER.log(Level.INFO, "{0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseIOExceptionThrowable COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for NetworkingBaseRuntimeException to
     * ensure it is correct.
     */
    @Test
    public void testNetworkingBaseRuntimeExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseRuntimeExceptionGetExceptionType -----");
        try {
            throw new NetworkingBaseRuntimeException("TEST");
        } catch (NetworkingBaseRuntimeException e) {
            Assert.assertEquals(e.getExceptionType(), "NetworkingBaseRuntimeException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseRuntimeExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for NetworkingBaseIOException to ensure
     * it is correct.
     */
    @Test
    public void testNetworkingBaseIOExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingBaseIOExceptionGetExceptionType -----");
        try {
            throw new NetworkingBaseIOException("TEST");
        } catch (NetworkingBaseIOException e) {
            Assert.assertEquals(e.getExceptionType(), "NetworkingBaseIOException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testNetworkingBaseIOExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for FeatureNotUsedException to ensure
     * it is correct.
     */
    @Test
    public void testFeatureNotUsedExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testFeatureNotUsedExceptionGetExceptionType -----");
        try {
            throw new FeatureNotUsedException("TEST");
        } catch (FeatureNotUsedException e) {
            Assert.assertEquals(e.getExceptionType(), "FeatureNotUsedException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testFeatureNotUsedExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for HashNotFoundException to ensure it
     * is correct.
     */
    @Test
    public void testHashNotFoundExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testHashNotFoundExceptionGetExceptionType -----");
        try {
            throw new HashNotFoundException("TEST");
        } catch (HashNotFoundException e) {
            Assert.assertEquals(e.getExceptionType(), "HashNotFoundException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testHashNotFoundExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for InvalidArgumentException to ensure
     * it is correct.
     */
    @Test
    public void testInvalidArgumentExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testInvalidArgumentExceptionGetExceptionType -----");
        try {
            throw new InvalidArgumentException("TEST");
        } catch (InvalidArgumentException e) {
            Assert.assertEquals(e.getExceptionType(), "InvalidArgumentException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testInvalidArgumentExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for NullException to ensure it is
     * correct.
     */
    @Test
    public void testNullExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNullExceptionGetExceptionType -----");
        try {
            throw new NullException("TEST");
        } catch (NullException e) {
            Assert.assertEquals(e.getExceptionType(), "NullException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testNullExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for ServerSocketCloseException to
     * ensure it is correct.
     */
    @Test
    public void testServerSocketCloseExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerSocketCloseExceptionGetExceptionType -----");
        try {
            throw new ServerSocketCloseException("TEST");
        } catch (ServerSocketCloseException e) {
            Assert.assertEquals(e.getExceptionType(), "ServerSocketCloseException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testServerSocketCloseExceptionGetExceptionType COMPLETED -----");
    }

    /**
     * Tests the value of exception_type for TimeoutException to ensure it is
     * correct.
     */
    @Test
    public void testTimeoutExceptionGetExceptionType() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testTimeoutExceptionGetExceptionType -----");
        try {
            throw new TimeoutException("TEST");
        } catch (TimeoutException e) {
            Assert.assertEquals(e.getExceptionType(), "TimeoutException", "Exception type not set correctly");
            LOGGER.log(Level.INFO, "{0}", e.getExceptionType());
        }
        LOGGER.log(Level.INFO, "----- TEST testTimeoutExceptionGetExceptionType COMPLETED -----");
    }
}
