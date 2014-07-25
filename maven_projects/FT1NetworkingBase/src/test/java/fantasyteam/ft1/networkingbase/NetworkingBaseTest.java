package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import fantasyteam.ft1.networkingbase.exceptions.FeatureNotUsedException;
import fantasyteam.ft1.networkingbase.exceptions.HashNotFoundException;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import fantasyteam.ft1.networkingbase.exceptions.ServerSocketCloseException;
import fantasyteam.ft1.networkingbase.exceptions.TimeoutException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.easymock.EasyMock.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 *
 * @author javu
 */
public class NetworkingBaseTest {

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
    private static final Logger LOGGER = Logger.getLogger(NetworkingBaseTest.class.getName());

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
     * Tests sending a message when use_message_queues == true. Ensures that the
     * message is queued on the {@link MessageQueue} corresponding to the
     * correct hash and ensures the message is sent through the socket.
     */
    @Test
    public void testServerUseMessageQueueSend() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerUseMessageQueueSend -----");
        String client_hash = "";
        String server_hash = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        server2.sendMessage("TEST", client_hash);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server2.getQueueList().get(client_hash).getMessages().isEmpty() || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(server2.getQueueList().get(client_hash).getMessages().isEmpty(), "Message was not sent through MessageQueue");
        LOGGER.log(Level.INFO, "----- TEST testServerUseMessageQueueSend COMPLETED -----");
    }

    /**
     * Tests the pause and resume functionality of the {@link MessageQueue}
     * class and ensures that messages stay queued when paused and they begin
     * sending again when resumed.
     */
    @Test
    public void testServerUseMessageQueueWithPauseAndResume() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerUseMessageQueueWithPauseAndResume -----");
        String client_hash = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException ex) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        server2.getQueueList().get(client_hash).pauseQueue();
        waitMessageQueueState(server2, client_hash, MessageQueue.PAUSED);
        server2.sendMessage("TEST", client_hash);
        server2.sendMessage("TEST2", client_hash);
        ArrayList<String> test_queue = server2.getQueueList().get(client_hash).getMessages();
        Assert.assertEquals("TEST", test_queue.get(0), "MessageQueue was not paused");
        Assert.assertEquals("TEST2", test_queue.get(1), "MessageQueue was not paused");

        server2.getQueueList().get(client_hash).resumeQueue();
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server2.getQueueList().get(client_hash).getMessages().isEmpty() || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        test_queue = server2.getQueueList().get(client_hash).getMessages();
        Assert.assertTrue(test_queue.isEmpty(), "MessageQueue sending was not resumed");

        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerUseMessageQueueWithPauseAndResume COMPLETED -----");
    }

    /**
     * Tests the methods of {@link Netorking} encodeAction and parseAction to
     * ensure that they correctly convert a list of parameters into a String
     * that can be broken down into a list of parameters again by the
     * parseAction function.
     */
    @Test
    public void testNetworkingEncodeParseMessage() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingEncodeParseMessage -----");
        String client_hash = "";
        String server_hash = "";
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("ACTION");
        parameters.add("PARAM1");
        parameters.add("PARAM2");
        String hash = "hash";
        game.handleAction(parameters, hash);
        replay(game);
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        try {
            server1.replaceHash(server_hash, hash);
        } catch (HashNotFoundException | InvalidArgumentException e) {
            exception = true;
        }
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.containsHash(hash) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        String action = "ACTION";
        ArrayList<String> action_parameters = new ArrayList<String>();
        action_parameters.add("PARAM1");
        action_parameters.add("PARAM2");
        server2.sendAction(action, action_parameters, client_hash);
        time.waitTime(wait+wait);
        verify(game);
        LOGGER.log(Level.INFO, "----- TEST testNetworkingEncodeParseMessage COMPLETED -----");
    }

    /**
     * Tests the use of {@link MessageQueues} to aid in reconnection. Ensures
     * that when a {@link SocketThread} is disconnected its accompanying
     * {@link MessageQueue} (if use_message_queues == true) will be set to state
     * {@link MesaageQueue}.DISCONNECT. Ensures that if
     * connectDisconnectSocket() is run before the {@link MessageQueue} times
     * out the {@link MessageQueue}'s state will be set back to
     * {@link MessageQueue}.RUNNING.
     */
    @Test
    public void testServerClientDisconnectAndReconnect() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectAndReconnect -----");
        String client_hash = "";
        String server_hash = "";
        String server_hash2 = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        server1.setUseDisconnectedSockets(true);
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        server1.getQueueList().get(server_hash).pauseQueue();
        waitMessageQueueState(server1, server_hash, MessageQueue.PAUSED);
        server1.sendMessage("TEST", server_hash);
        server1.sendMessage("TEST2", server_hash);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getQueueList().get(server_hash).getMessages().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        server2.disconnect(client_hash);
        waitMessageQueueState(server1, server_hash, MessageQueue.DISCONNECT);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getQueueList().get(server_hash).getMessages().isEmpty() || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        Assert.assertTrue(server1.getQueueList().get(server_hash).getMessages().isEmpty(), "MessageQueue not cleared after state set to DISCONNECT");
        Assert.assertTrue(server1.getDisconnectedSockets().contains(server_hash), "Disconnected hash was not put into disconnected_sockets list");
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getQueueList().size() == 2 || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        for (SocketThread socket : server1.getSocketList().values()) {
            if (socket.getHash().compareTo(server_hash) != 0) {
                server_hash2 = socket.getHash();
            }
        }
        waitSocketThreadState(server1, server_hash2, SocketThread.CONFIRMED);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getQueueList().containsKey(server_hash2) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        waitMessageQueueState(server1, server_hash2, MessageQueue.RUNNING);
        try {
            server1.connectDisconnectedSocket(server_hash2, server_hash);
        } catch (HashNotFoundException | InvalidArgumentException | FeatureNotUsedException e) {
            exception = true;
        }
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getDisconnectedSockets().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        for (SocketThread socket : server1.getSocketList().values()) {
            server_hash2 = socket.getHash();
        }
        Assert.assertTrue(server1.getDisconnectedSockets().isEmpty(), "Disconnected hash was not removed from disconnected_sockets list");
        Assert.assertTrue(server1.getSocketList().containsKey(server_hash), "Reconnected sockt was not moved into old key in socket_list");
        Assert.assertEquals(server_hash2, server_hash, "Reconnected sockets hash was not set to saved hash");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectAndReconnect COMPLETED -----");
    }

    /**
     * Tests the use of {@link MessageQueues} to aid in timing out
     * reconnections. Ensures that when a {@link SocketThread} is disconnected
     * its accompanying {@link MessageQueue} (if use_message_queues == true)
     * will be set to state {@link MesaageQueue}.DISCONNECT. Ensures that if
     * connectDisconnectSocket() is run after the {@link MessageQueue} times out
     * that the reconnection attempt will be ignored. Also ensures that the
     * {@link MessageQueue} will close itself correctly.
     */
    @Test
    public void testServerClientDisconnectAndTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectAndTimeout -----");
        boolean timed_out = false;
        String client_hash = "";
        String server_hash = "";
        String server_hash2 = "";
        long disconnect_timeout = 50;
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        server1.setUseDisconnectedSockets(true);
        try {
            server2.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        try {
            server1.getQueueList().get(server_hash).setTimeout(disconnect_timeout);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        server1.getQueueList().get(server_hash).pauseQueue();
        waitMessageQueueState(server1, server_hash, MessageQueue.PAUSED);
        server1.sendMessage("TEST", server_hash);
        server1.sendMessage("TEST2", server_hash);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getQueueList().get(server_hash).getMessages().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        server2.disconnect(client_hash);
        new_timer.startTiming();
        waitMessageQueueState(server1, server_hash, MessageQueue.DISCONNECT);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getQueueList().get(server_hash).getMessages().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertTrue(server1.getQueueList().get(server_hash).getMessages().isEmpty(), "MessageQueue not cleared after state set to 4");
        Assert.assertTrue(server1.getDisconnectedSockets().contains(server_hash), "Disconnected hash was not put into disconnected_sockets list");
        Timing timer = new Timing();
        timer.waitTime(disconnect_timeout + wait);
        waitMessageQueueRemoveEmpty(server2);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadAddNotEmpty(server1);
        for (SocketThread socket : server1.getSocketList().values()) {
            if (socket.getHash().compareTo(server_hash) != 0) {
                server_hash2 = socket.getHash();
            }
        }
        waitSocketThreadState(server1, server_hash2, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash2, MessageQueue.RUNNING);
        try {
            server1.connectDisconnectedSocket(server_hash2, server_hash);
        } catch (HashNotFoundException | InvalidArgumentException | FeatureNotUsedException e) {
            timed_out = true;
        }
        time.waitTime(wait);

        Assert.assertFalse(server1.getQueueList().containsKey(server_hash), "MessageQueue did not close itself after timeout period");
        Assert.assertFalse(server1.getDisconnectedSockets().contains(server_hash), "Timed out sockets hash was not removed from disconnected_sockets list");
        Assert.assertFalse(server1.getSocketList().containsKey(server_hash), "socket_list should no longer contain a value for the timed out sockets hash");
        Assert.assertTrue(server1.getSocketList().containsKey(server_hash2), "New SocketThreaad was removed after attempting to reconnect with old SocketThreads hash");
        Assert.assertTrue(server1.getQueueList().containsKey(server_hash2), "New MessageQueue was removed after attempting to reconnect with old SocketThreads hash");
        Assert.assertTrue(timed_out, "Successfully connected disconnected socket, socket should have timed out");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectAndTimeout COMPLETED -----");
    }
    
    @Test
    public void testSocketTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSocketTimeout -----");
        Timing timer = new Timing();
        boolean client_timed_out = true;
        boolean server_timed_out = true;
        String client_hash = "";
        String server_hash = "";
        try {
            server2.setSocketTimeout(100);
            server2.setSocketTimeoutCount(3);
            server2.setUseSocketTimeout(true);
        } catch (SocketException | InvalidArgumentException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        waitSocketThreadRemoveEmpty(server2);
        waitSocketThreadRemoveEmpty(server1);
        if(server2.containsHash(client_hash)) {
            client_timed_out = false;
        }
        if(server1.containsHash(server_hash)) {
            server_timed_out = false;
        }
        Assert.assertTrue(client_timed_out, "Client did not disconnect after not receiveing input for specified time");
        Assert.assertTrue(server_timed_out, "Server did not disconnect itself after client timed out");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testSocketTimeout COMPLETED -----");
    }
}
