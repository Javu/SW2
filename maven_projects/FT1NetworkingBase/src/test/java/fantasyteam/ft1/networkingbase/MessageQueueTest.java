package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
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
 * Unit tests for the {@link MessageQueue} class. Please note, a lot of this
 * classes testing is also handled in the test class ServerTest.
 *
 * @author Javu
 */
public class MessageQueueTest {

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
    private static final Logger LOGGER = Logger.getLogger(MessageQueueTest.class.getName());

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
    private void setupQueue() throws IOException, ServerSocketCloseException, TimeoutException, FeatureNotUsedException {
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
        waitSocketThreadState(server2, hash, SocketThread.RUNNING);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
    }

    /**
     * Closes both {@link Server}s and frees up port ready for the next test.
     *
     * @throws IOException if either {@link Server} fails to close.
     */
    @AfterMethod
    private void deleteQueue() throws IOException, ServerSocketCloseException, TimeoutException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        waitServerState(server2, Server.CLOSED);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitServerState(server1, Server.CLOSED);
    }

    /**
     * Tests the {@link MessageQueue}.setMessages(ArrayList(String) messages)
     * function and ensures it changes the value of messages correctly.
     */
    @Test
    public void testMessageQueueSetMessages() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetMessages -----");
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("TEST");
        server2.getQueueList().get(hash).pauseQueue();
        server2.getQueueList().get(hash).setMessages(string_array);
        Assert.assertEquals(server2.getQueueList().get(hash).getMessages().get(0), "TEST", "MessageQueue.messages was not changed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetMessages COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.setRun(int state) function and ensures it
     * sets the value of state correctly.
     */
    @Test
    public void testMessageQueueSetRun() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetRun -----");
        try {
            server2.getQueueList().get(hash).setRun(MessageQueue.ERROR);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), MessageQueue.ERROR, "MessageQueue.run was not set to 2");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetRun COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.setHash(String hash) function and ensures
     * it sets the value of hash correctly.
     */
    @Test
    public void testMessageQueueSetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetHash -----");
        server2.getQueueList().get(hash).setHash("TEST");
        Assert.assertEquals(server2.getQueueList().get(hash).getHash(), "TEST", "MessageQueue.hash was not set to TEST");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetHash COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.getTimeout() function and ensures it
     * returns the correct value of timeout.
     */
    @Test
    public void testMessageQueueGetTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueGetTimeout -----");
        long current_timeout = server2.getQueueList().get(hash).getTimeout();
        Assert.assertEquals(current_timeout, 300000, "Did not return the correct timeout value");
        long new_timeout = 5;
        try {
            server2.getQueueList().get(hash).setTimeout(new_timeout);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        current_timeout = server2.getQueueList().get(hash).getTimeout();
        Assert.assertEquals(current_timeout, new_timeout, "Did not return the correct timeout value after changing it using MessageQueue.setTimeout(long timeout)");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueGetTimeout COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.getHash() function and ensures it returns
     * the correct value of hash.
     */
    @Test
    public void testMessageQueueGetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueGetHash -----");
        String current_hash = server2.getQueueList().get(hash).getHash();
        Assert.assertEquals(current_hash, hash, "MessageQueue.hash was is not set correctly");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueGetHash COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.pauseQueue() and
     * {@link MessageQueue}.clearQueue() functions and ensures that messages
     * will not be sent when state is set to {@link MessageQueue}.PAUSED and
     * that the messages ArrayList is cleared when clearQueue is run.
     */
    @Test
    public void testMessageQueuePauseAndClearQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndClearQueue -----");
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST", hash);
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), MessageQueue.PAUSED, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getQueueList().get(hash).getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getQueueList().get(hash).clearQueue();
        Assert.assertTrue(server2.getQueueList().get(hash).getMessages().isEmpty(), "MessageQueue was not cleared");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndClearQueue COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.pauseQueue() and
     * {@link MessageQueue}.resumeQueue() functions and ensures that messages
     * will not be sent when state is set to {@link MessageQueue}.PAUSED and
     * that messages will start sending again when state is set to
     * {@link MessageQueue}.RUNNING.
     */
    @Test
    public void testMessageQueuePauseAndResumeQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndResumeQueue -----");
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST", hash);
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), MessageQueue.PAUSED, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getQueueList().get(hash).getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getQueueList().get(hash).resumeQueue();
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server2.getQueueList().get(hash).getMessages().isEmpty() || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), MessageQueue.RUNNING, "MessageQueue.run was not set to 1 (resume)");
        Assert.assertTrue(server2.getQueueList().get(hash).getMessages().isEmpty(), "Messages in MessageQueue were not sent after queue was resumed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndResumeQueue COMPLETED -----");
    }

    /**
     * Tests the {@link MessageQueue}.toString() function. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToStringMessageQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringMessageQueue -----");
        String to_string = null;
        to_string = server2.getQueueList().get(hash).toString();
        Assert.assertNotEquals(to_string, null, "ListenThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "ListenThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringMessageQueue COMPLETED -----");
    }
}
