package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import fantasyteam.ft1.networkingbase.exceptions.FeatureNotUsedException;
import fantasyteam.ft1.networkingbase.exceptions.HashNotFoundException;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import fantasyteam.ft1.networkingbase.exceptions.NullException;
import fantasyteam.ft1.networkingbase.exceptions.ServerSocketCloseException;
import fantasyteam.ft1.networkingbase.exceptions.TimeoutException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.easymock.EasyMock.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for base Server class. These tests also test the majority of the
 * functionality of the fantasyteam.ft1.networkingbase package, so any core
 * functionality of the other classes of this package are also tested in here.
 * Any outlying or utility methods associated with classes other than Server
 * will be tested on their own in that classes individual test package.
 *
 * @author javu
 */
public class ServerTest {

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
    private static final Logger LOGGER = Logger.getLogger(ServerTest.class.getName());

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
     * Test of a constructor for the Server class. Tests the Server(game,
     * listen) constructor to ensure the attributes port and state are set
     * correctly.
     */
    @Test
    public void testServerConstructor() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConstructor -----");
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        server2 = new Server(game);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getPort(), 0, "Server not constructed");
        Assert.assertEquals(server2.getState(), 1, "Server not set as client server");
        LOGGER.log(Level.INFO, "----- TEST testServerConstructor COMPLETED -----");
    }

    /**
     * Test of a constructor for the Server class. Tests the Server(game,
     * listen) constructor to ensure the attribute listen_thread is set
     * correctly.
     */
    @Test
    void testServerConstructorListen() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConstructorListen -----");
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        server2 = new Server(game);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getPort(), 0, "Server not constructed");
        Assert.assertEquals(server2.getListenThread(), null, "Server not constructed");
        LOGGER.log(Level.INFO, "----- TEST testServerConstructorListen COMPLETED -----");
    }

    /**
     * Test of a constructor for the Server class. Tests the Server(game, port,
     * listen) constructor to ensure the attribute port is set correctly.
     */
    @Test
    public void testServerConstructorPort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConstructorPort -----");
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        try {
            server2 = new Server(game, 12457, true);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getPort(), 12457, "Server not constructed");
        LOGGER.log(Level.INFO, "----- TEST testServerConstructorPort COMPLETED -----");
    }

    /**
     * Test of a constructor for the Server class. Tests the Server(game, port,
     * listen) constructor to ensure the attribute listen_thread is set
     * correctly.
     */
    @Test
    void testServerConstructorListenPort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerConstructorListenPort -----");
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        try {
            server2 = new Server(game, 12457, false);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getPort(), 12457, "Server not constructed");
        Assert.assertEquals(server2.getListenThread(), null, "Server not constructed");
        LOGGER.log(Level.INFO, "----- TEST testServerConstructorListenPort COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the port attribute.
     */
    @Test
    public void testSetPort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetPort -----");
        try {
            server1.setPort(22223);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server1.getPort(), 22223, "Port not changed");
        LOGGER.log(Level.INFO, "----- TEST testSetPort COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the timeout attribute.
     */
    @Test
    public void testSetTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetTimeout -----");
        try {
            server1.setTimeout(100);
        } catch (InvalidArgumentException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server1.getTimeout(), 100, "timeout not changed");
        LOGGER.log(Level.INFO, "----- TEST testSetTimeout COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the socket_timeout attribute.
     */
    @Test
    public void testSetSocketTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetSocketTimeout -----");
        String client_hash = "";
        String client_hash2 = "";
        String client_hash3 = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.setSocketTimeout(100);
        } catch (InvalidArgumentException | SocketException e) {
            exception = true;
        }
        try {
            client_hash2 = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadState(server2, client_hash2, SocketThread.CONFIRMED);
        try {
            server2.setUseSocketTimeout(true);
        } catch (SocketException e) {
            exception = true;
        }
        try {
            client_hash3 = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadState(server2, client_hash3, SocketThread.CONFIRMED);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketTimeout(), 100, "Socket timeout value not changed on server");
        Assert.assertEquals(server2.getSocketList().get(client_hash).getSocketTimeout(), 100, "Socket timeout value not changed on first SocketThread");
        Assert.assertEquals(server2.getSocketList().get(client_hash2).getSocketTimeout(), 100, "Socket timeout value not changed on second SocketThread");
        Assert.assertEquals(server2.getSocketList().get(client_hash3).getSocketTimeout(), 100, "Socket timeout value not changed on third SocketThread");
        LOGGER.log(Level.INFO, "----- TEST testSetSocketTimeout COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the socket_timeout_count attribute.
     */
    @Test
    public void testSetSocketTimeoutCount() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetSocketTimeoutCount -----");
        String client_hash = "";
        String client_hash2 = "";
        String client_hash3 = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        try {
            server2.setSocketTimeoutCount(50);
        } catch (InvalidArgumentException | SocketException e) {
            exception = true;
        }
        try {
            client_hash2 = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadState(server2, client_hash2, SocketThread.CONFIRMED);
        try {
            server2.setUseSocketTimeout(true);
        } catch (SocketException e) {
            exception = true;
        }
        try {
            client_hash3 = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadState(server2, client_hash3, SocketThread.CONFIRMED);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketTimeoutCount(), 50, "Socket timeout count value not changed on server");
        Assert.assertEquals(server2.getSocketList().get(client_hash).getSocketTimeoutCount(), 50, "Socket timeout count value not changed on first SocketThread");
        Assert.assertEquals(server2.getSocketList().get(client_hash2).getSocketTimeoutCount(), 50, "Socket timeout count value not changed on second SocketThread");
        Assert.assertEquals(server2.getSocketList().get(client_hash3).getSocketTimeoutCount(), 50, "Socket timeout count value not changed on third SocketThread");
        LOGGER.log(Level.INFO, "----- TEST testSetSocketTimeoutCount COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the use_disconnected_sockets attribute.
     */
    @Test
    public void testUseDisconnectSockets() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testUseDisconnectSockets -----");
        server1.setUseDisconnectedSockets(true);
        Assert.assertEquals(server1.getUseDisconnectedSockets(), true, "Use Disonnected Sockets flag not changed");
        LOGGER.log(Level.INFO, "----- TEST testUseDisconnectSockets COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the use_message_queues attribute.
     */
    @Test
    public void testUseMessageQueues() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testUseMessageQueues -----");
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        Assert.assertEquals(server1.getUseMessageQueues(), true, "Use Message Queues flag not changed");
        LOGGER.log(Level.INFO, "----- TEST testUseMessageQueues COMPLETED -----");
    }

    /**
     * This test ensures that the setuseMessageQueues function correctly
     * constructs and starts a {@link MessageQueue} for each existing
     * {@link SocketThread} in socket_list if true is passed as a parameter and
     * use_message_queues == false.
     */
    @Test
    public void testUseMessageQueuesTrueWithSockets() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testUseMessageQueuesTrueWithSockets -----");
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        String client_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        String server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        LOGGER.log(Level.INFO, "----- TEST testUseMessageQueuesTrueWithSockets COMPLETED -----");
    }

    /**
     * This test ensures that the setuseMessageQueues function correctly closes
     * and removes all {@link MessageQueue}s for each existing
     * {@link SocketThread} in socket_list if false is passed as a parameter and
     * use_message_queues == true.
     */
    @Test
    public void testUseMessageQueuesFalseWithSockets() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testUseMessageQueuesFalseWithSockets -----");
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        String client_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        String server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        try {
            server1.setUseMessageQueues(false);
        } catch (TimeoutException e) {
            exception = true;
        }
        waitMessageQueueRemoveEmpty(server1);
        LOGGER.log(Level.INFO, "----- TEST testUseMessageQueuesFalseWithSockets COMPLETED -----");
    }

    /**
     * This test ensures that the setUseSocketTimeout function correctly sets
     * the useSocketTimeout flag to false on both the {@link Server} and each
     * connected {@link SocketThread} if the flag was set to true and is then
     * set to false using this function.
     */
    @Test
    public void testSetUseSocketTimeoutFalse() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetUseSocketTimeoutFalse -----");
        String client_hash = "";
        try {
            server2.setUseSocketTimeout(true);
        } catch (SocketException e) {
            exception = true;
        }
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        Assert.assertTrue(server2.getUseSocketTimeout(), "Use socket timeout not set to true on Server");
        Assert.assertTrue(server2.getSocketList().get(client_hash).getUseSocketTimeout(), "Use socket timeout not set to true on SocketThread");
        try {
            server2.setUseSocketTimeout(false);
        } catch (SocketException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertFalse(server2.getUseSocketTimeout(), "Use socket timeout not set to false on Server");
        Assert.assertFalse(server2.getSocketList().get(client_hash).getUseSocketTimeout(), "Use socket timeout not set to false on SocketThread");
        LOGGER.log(Level.INFO, "----- TEST testSetUseSocketTimeoutFalse COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the socket_list attribute.
     */
    @Test
    public void testSetSocketList() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetSocketList -----");
        server2.setSocketList(server1.getSocketList());
        Assert.assertEquals(server2.getSocketList(), server1.getSocketList(), "Socket List not changed");
        LOGGER.log(Level.INFO, "----- TEST testSetSocketList COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the socket_list attribute.
     */
    @Test
    public void testSetQueueList() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetQueueList -----");
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        HashMap<String, MessageQueue> new_queue_list = new HashMap<String, MessageQueue>();
        new_queue_list.put("TEST", new MessageQueue(server1, "TEST"));
        server1.setQueueList(new_queue_list);
        Assert.assertEquals(server1.getQueueList(), new_queue_list, "Queue List not changed");
        Assert.assertEquals(server1.getQueueList().get("TEST").getHash(), "TEST", "Queue List values not set correctly");
        LOGGER.log(Level.INFO, "----- TEST testSetQueueList COMPLETED -----");
    }

    /**
     * Test of an attribute setter for the disconnected_sockets attribute.
     */
    @Test
    public void testSetDisconnectedSockets() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetDisconnectedSockets -----");
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("1");
        string_array.add("2");
        server2.setDisconnectedSockets(string_array);
        Assert.assertEquals(server2.getDisconnectedSockets(), string_array, "Disconnected Sockets array not changed");
        LOGGER.log(Level.INFO, "----- TEST testSetDisconnectedSockets COMPLETED -----");
    }

    /**
     * This test ensures that a Server set up as a client can be successfully
     * changed to a listen server and bound correctly to a port to listen.
     */
    @Test
    public void testSetListenThreadClient() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetListenThreadClient -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if (server2.getState() == Server.CLIENT) {
            flag1 = true;
        }
        if (flag1) {
            try {
                server2.setPort(23231);
            } catch (InvalidArgumentException e) {
                exception = true;
            }
            try {
                server2.setListenThread();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = true;
            }
            if (server2.getState() == Server.LISTEN) {
                flag2 = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(flag2, "Client server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO, "----- TEST testSetListenThreadClient COMPLETED -----");
    }

    /**
     * This test attempts to unbind a Server listening on a port and binds it to
     * a different port using the setListenThread method.
     */
    @Test
    public void testSetListenThreadServer() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetListenThreadServer -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if (server1.getState() == Server.LISTEN) {
            flag1 = true;
        }
        if (flag1) {
            try {
                server1.setPort(23231);
            } catch (InvalidArgumentException e) {
                exception = true;
            }
            try {
                server1.setListenThread();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = true;
            }
            if (server1.getState() == Server.LISTEN) {
                flag2 = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(flag2, "Server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO, "----- TEST testSetListenThreadServer COMPLETED -----");
    }

    /**
     * This test uses the setListenThread method to test whether a Server
     * already listening on a port can be unbound from the port and rebound to
     * the same port using the setListenThread method.
     */
    @Test
    public void testSetListenThreadServerOnSamePort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetListenThreadServerOnSamePort -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if (server1.getState() == Server.LISTEN) {
            flag1 = true;
        }
        if (flag1) {
            try {
                server1.setListenThread();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = true;
            }
            if (server1.getState() == Server.LISTEN) {
                flag2 = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(flag2, "Server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO, "----- TEST testSetListenThreadServerOnSamePort COMPLETED -----");
    }

    /**
     * This test changes a Server constructed as a client into a listen server
     * using the setListenThread method. It then ensures that this newly changed
     * listen server is capable of accepting new connections.
     */
    @Test
    public void testSetListenThreadAndListen() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetListenThreadAndListen -----");
        boolean flag1 = false;
        boolean flag2 = false;
        int count = 0;
        if (server2.getState() == Server.CLIENT) {
            flag1 = true;
        }
        if (flag1) {
            try {
                server2.setPort(23231);
            } catch (InvalidArgumentException e) {
                exception = true;
            }
            try {
                server2.setListenThread();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = true;
            }
            if (server2.getState() == Server.LISTEN) {
                flag2 = true;
                try {
                    server2.startThread();
                } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
                    exception = true;
                }
                waitListenThreadStart(server2);
            }
        }
        if (flag2) {
            String client_hash = "";
            try {
                client_hash = server1.addSocket("127.0.0.1", 23231);
            } catch (IOException | TimeoutException e) {
                exception = true;
            }
            waitSocketThreadAddNotEmpty(server1);
            waitSocketThreadState(server1, client_hash, SocketThread.CONFIRMED);
            waitSocketThreadAddNotEmpty(server2);
            if (server2.getSocketList() != null && !server2.getSocketList().isEmpty()) {
                for (SocketThread socket : server2.getSocketList().values()) {
                    count++;
                }
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(flag2, "server2 not set as a listen server");
        Assert.assertEquals(count, 1, "No connection was established");
        LOGGER.log(Level.INFO, "----- TEST testSetListenThreadAndListen COMPLETED -----");
    }

    /**
     * Test the function to set the attribute game on a {@link SocketThread}..
     */
    @Test
    public void testSetSocketGame() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetSocketGame -----");
        String client_hash = "";
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
        Assert.assertEquals(server2.getSocketGame(client_hash), 0, "Value of game not set to 0 on SocketThread");
        try {
            server2.setSocketGame(client_hash, 1);
        } catch(NullException | HashNotFoundException e) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketGame(client_hash), 1, "Value of game not set to 1 on SocketThread");
        LOGGER.log(Level.INFO, "----- TEST testSetSocketGame COMPLETED -----");
    }

    /**
     * Tests whether an IOException is correctly thrown when attempting to start
     * a new ListenThread on a port that is in use.
     */
    @Test
    public void testListenPortInUse() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testListenPortInUse -----");
        Server server3 = null;
        try {
            server3 = new Server(game, port, true);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
            LOGGER.log(Level.INFO, "{0}", e.toString());
        }
        Assert.assertTrue(exception, "Exception was not thrown for running Server(Game,port,true) when port was in use");
        if (server3 != null) {
            try {
                server3.close();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = false;
            }
            waitServerState(server3, Server.CLOSED);
            Assert.assertTrue(exception, "Exception found");
        }
        LOGGER.log(Level.INFO, "----- TEST testListenPortInUse COMPLETED -----");
    }

    /**
     * This test ensures that the removeDisconnectedSocket method removes the
     * specified String from the disconnected_sockets array increments all other
     * values back into order correctly.
     */
    @Test
    public void testRemoveDisconnectedSocket() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRemoveDisconnectedSocket -----");
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("r");
        string_array.add("b");
        server1.setDisconnectedSockets(string_array);
        try {
            server1.removeDisconnectedSocket("r");
        } catch (HashNotFoundException | NullException e) {
            exception = true;
        }
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), "b", "Hash not removed from disconnected_sockets changed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testRemoveDisconnectedSocket COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.removeQueue function using a hash that does not
     * exist.
     */
    @Test
    public void testRemoveQueueNotExist() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRemoveQueueNotExist -----");
        boolean queue_not_exist = false;
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
        Assert.assertTrue(server1.getListenThread().getRun(), "ListenThread did not start in time");
        String client_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadAddNotEmpty(server1);
        String server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        try {
            server1.removeQueue("TEST");
        } catch (HashNotFoundException e) {
            queue_not_exist = true;
        } catch (NullException e) {
            exception = true;
        }
        Assert.assertTrue(queue_not_exist, "Successfully removed queue, should have received a HashNotFoundException");
        Assert.assertFalse(server1.getQueueList().isEmpty(), "queue_list was emptied. No MessageQueues should have been removed");
        Assert.assertTrue(server1.getQueueList().containsKey(server_hash), "MessageQueue with hash " + server_hash + "should not have been removed from server");
        Assert.assertEquals(server1.getQueueList().get(server_hash).getRun(), MessageQueue.RUNNING, "MessageQueue with hash " + server_hash + "should not have stopped running");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testRemoveQueueNotExist COMPLETED -----");
    }

    /**
     * This test ensures the version of addSocket(Socket) correctly takes a
     * pre-constructed Socket class, builds it into a SocketThread and adds it
     * to the socket_list map.
     */
    @Test
    public void testServerClientConnectSocket() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectSocket -----");
        String client_hash = "";
        String server_hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            client_hash = server2.addSocket(socket);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectSocket COMPLETED -----");
    }

    /**
     * This test ensures the version of addSocket(Sock) correctly takes a
     * pre-constructed Sock class, builds it into a SocketThread and adds it to
     * the socket_list map.
     */
    @Test
    public void testServerClientConnectSock() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectSock -----");
        String client_hash = "";
        String server_hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        Sock sock = null;
        try {
            sock = new Sock("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            client_hash = server2.addSocket(sock);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, client_hash, SocketThread.CONFIRMED);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectSock COMPLETED -----");
    }

    /**
     * This test ensures the version of addSocket(String) correctly takes a
     * String representing an IP address, builds it into a SocketThread and adds
     * it to the socket_list map.
     */
    @Test
    public void testServerClientConnectIp() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectIp -----");
        String client_hash = "";
        String server_hash = "";
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
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectIp COMPLETED -----");
    }

    /**
     * This test ensures the version of addSocket(String, int) correctly takes a
     * String representing an IP address and an int representing a port number,
     * builds them into a SocketThread and adds it to the socket_list map.
     */
    @Test
    public void testServerClientConnectIpPort() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectIpPort -----");
        String client_hash = "";
        String server_hash = "";
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
        if (server1.containsHash(server_hash) == true) {
            client_hash = "connected";
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(client_hash, "connected", "Client not connected");
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectIpPort COMPLETED -----");
    }

    /**
     * Tests using the addSocket method with use_message_queues == true. Ensures
     * that a {@link SocketThread} will be constructed as well as a
     * {@link MessageQueue}, both will be given the same has value and placed in
     * their respective Maps.
     */
    @Test
    public void testServerClientConnectWithMessageQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectWithMessageQueue -----");
        String server_hash = "";
        String client_hash = "";
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
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, client_hash, MessageQueue.RUNNING);
        waitMessageQueueAddNotEmpty(server1);
        waitMessageQueueState(server1, server_hash, MessageQueue.RUNNING);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectWithMessageQueue COMPLETED -----");
    }

    /**
     * This test ensures that the disconnect method correctly removes the
     * SocketThread corresponding to the given hash String from the socket_list
     * map. It also ensures the Thread is gracefully ended.
     */
    @Test
    public void testServerClientDisconnect() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnect -----");
        String client_hash = "";
        String server_hash = "";
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
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        waitSocketThreadRemoveEmpty(server1);
        if (server1.containsHash(server_hash) != true || server1.getSocketList().get(server_hash).getRun() == SocketThread.CLOSED) {
            server_hash = "disconnected";
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server_hash, "disconnected", "Connection not closed successfully");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnect COMPLETED -----");
    }

    /**
     * This test ensures that the disconnect method correctly removes the
     * SocketThread corresponding to the given hash String from the socket_list
     * map and that the hash String is added to the disconnected_sockets array
     * if the use_disconnected_sockets attribute is set as true. It also ensures
     * the Thread is gracefully ended.
     */
    @Test
    public void testServerClientDisconnectWithHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectWithHash -----");
        String client_hash = "";
        String server_hash = "";
        server1.setUseDisconnectedSockets(true);
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
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (!server1.getDisconnectedSockets().isEmpty() || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertFalse(server1.getDisconnectedSockets().isEmpty(), "Hash not added to disconnected_sockets");
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), server_hash, "Disconnected sockets hash not logged");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectWithHash COMPLETED -----");
    }

    /**
     * This test ensures that the disconnect method correctly removes the
     * SocketThread corresponding to the given hash String from the socket_list
     * map. It also ensures the Thread is gracefully ended.
     */
    @Test
    public void testServerClientDisconnectNonRunningSocketThread() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectNonRunningSocketThread -----");
        String server_hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            Sock new_sock = new Sock("127.0.0.1", port);
            server2.getSocketList().put("TEST", new SocketThread(new_sock, server2, "TEST"));
        } catch (IOException ex) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        server2.disconnect("TEST");
        waitSocketThreadRemoveEmpty(server2);
        waitSocketThreadRemoveEmpty(server1);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectNonRunningSocketThread COMPLETED -----");
    }

    /**
     * This test ensures that the close() method of Server correctly closes all
     * attributes and threads, including closing the listen_thread attribute
     * only if the Server is set as a listen server. Ensures all Threads are
     * closed gracefully.
     */
    @Test
    public void testServerClose() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClose -----");
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        boolean flag1 = false;
        boolean flag2 = false;
        if (server1.getListenThread().getRun()) {
            flag2 = true;
        }
        if (flag2) {
            try {
                server1.close();
            } catch (IOException | ServerSocketCloseException | TimeoutException e) {
                exception = true;
            }
            waitServerState(server1, Server.CLOSED);
            if (!server1.getListenThread().getRun()) {
                flag1 = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(flag1, "Server not closed as a listen server");
        LOGGER.log(Level.INFO, "----- TEST testServerClose COMPLETED -----");
    }

    /**
     * This test uses the replaceHash method to ensure that a SocketThread
     * already added to the socket_list array can be moved to correspond to a
     * different key in the array.
     */
    @Test
    public void testReplaceHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testReplaceHash -----");
        ArrayList<String> string_array = new ArrayList<String>();
        ArrayList<String> disconnected_socket = new ArrayList<String>();
        String client_hash = "";
        String client_hash2 = "";
        String server_hash = "";
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        server1.setUseDisconnectedSockets(true);
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
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        waitSocketThreadState(server1, server_hash, SocketThread.CONFIRMED);
        try {
            client_hash2 = server3.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server3);
        waitSocketThreadState(server3, client_hash2, SocketThread.CONFIRMED);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getSocketList().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        Assert.assertEquals(server1.getSocketList().size(), 2, "SocketThread not constructed and added in time");
        for (SocketThread sockets : server1.getSocketList().values()) {
            string_array.add(sockets.getHash());
        }
        disconnected_socket.add(string_array.get(0));
        server1.setDisconnectedSockets(disconnected_socket);
        try {
            server1.connectDisconnectedSocket(string_array.get(1), string_array.get(0));
        } catch (HashNotFoundException | InvalidArgumentException | FeatureNotUsedException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testReplaceHash COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.replaceHash function when attempting to move old
     * socket data to a new hash that does not have any data currently stored.
     */
    @Test
    public void testReplaceHashNotExist() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testReplaceHashNotExist -----");
        String client_hash = "";
        String server_hash = "";
        String server_hash_new = "";
        try {
            server1.setUseMessageQueues(true);
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
            client_hash = server2.addSocket("127.0.0.1");
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
        server1.replaceHash(server_hash, "TEST");
        for (SocketThread socket : server1.getSocketList().values()) {
            server_hash_new = socket.getHash();
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server_hash_new, "TEST", "SocketThread not moved to the correct hash");
        Assert.assertFalse(server1.containsHash(server_hash), "Old hash not removed from list");
        LOGGER.log(Level.INFO, "----- TEST testReplaceHashNotExist COMPLETED -----");
    }

    /**
     * Tests the pingSockets function to send a blank String to two connected
     * sockets and ensures neither of them are detected as disconnected after
     * sending the message.
     */
    @Test
    public void testPingSocket() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testPingSocket -----");
        String client_hash = "";
        String client_hash2 = "";
        server1.setUseDisconnectedSockets(true);
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
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
            client_hash2 = server3.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server3);
        waitSocketThreadState(server3, client_hash2, SocketThread.CONFIRMED);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getSocketList().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        try {
            server1.pingSockets();
        } catch (IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        boolean not_disconnected = server1.getDisconnectedSockets().isEmpty();
        Assert.assertTrue(not_disconnected, "Could not ping sockets");
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testPingSocket COMPLETED -----");
    }

    /**
     * Tests whether the Server.close() method correctly unbinds any bound ports
     * used by the Server class that are not currently being listened on.
     */
    @Test
    public void testReleasePortNumber() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testReleasePortNumber -----");
        try {
            server1.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server1, Server.CLOSED);
        Server server3 = null;
        try {
            server3 = new Server(game, port, true);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        Assert.assertEquals(server3.getState(), Server.LISTEN, "Server not set as state LISTEN. Port could not be listened on");
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testReleasePortNumber COMPLETED -----");
    }

    /**
     * Tests whether the Server.close() method correctly unbinds any bound ports
     * used by the Server class that are currently being listened.
     */
    @Test
    public void testReleasePortNumberRunning() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testReleasePortNumberRunning -----");
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            server1.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server1, Server.CLOSED);
        Server server3 = null;
        try {
            LOGGER.log(Level.INFO, "Building Server3");
            server3 = new Server(game, port, true);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        Assert.assertEquals(server3.getState(), Server.LISTEN, "Server not set as state LISTEN. Port could not be listened on");
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testReleasePortNumberRunning COMPLETED -----");
    }

    /**
     * Calls the startThread method on a Server setup as a client. Ensures that
     * the call doesn't do anything and that listen_thread is not started.
     */
    @Test
    public void testStartThreadClient() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testStartThreadClient -----");
        try {
            server2.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertEquals(server2.getState(), Server.CLIENT, "server2 not set as client server");
        Assert.assertEquals(server2.getListenThread(), null, "ListenThread started on client server");
        LOGGER.log(Level.INFO, "----- TEST testStartThreadClient COMPLETED -----");
    }

    /**
     * Calls the listen method on a Server setup as a client. Ensures that the
     * call doesn't do anything.
     */
    @Test
    public void testListenClient() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testListenClient -----");
        String listen = "";
        try {
            listen = server2.listen();
        } catch (IOException | TimeoutException | FeatureNotUsedException | NullException e) {
            exception = true;
        }
        Assert.assertTrue(exception, "Successfully ran listen on client server, should have received an exception");
        Assert.assertEquals(server2.getState(), Server.CLIENT, "server2 not set as client server");
        LOGGER.log(Level.INFO, "----- TEST testListenClient COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.sendMessage(String, List(String)) when using the
     * {@link MessageQueue} feature. Ensures messages are sent through the
     * {@link MessageQueue} for each {@link SocketThread}.
     */
    @Test
    public void testSendMessageToListWithQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSendMessageToListWithQueue -----");
        String client_hash = "";
        String client_hash2 = "";
        String server_hash = "";
        String server_hash2 = "";
        try {
            server1.setUseMessageQueues(true);
        } catch (TimeoutException e) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        Assert.assertTrue(server1.getListenThread().getRun(), "ListenThread did not start in time");
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
            client_hash2 = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadState(server2, client_hash2, SocketThread.CONFIRMED);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getSocketList().size() == 2) {
                loop = false;
            } else if (new_timer.getTime() > 5000) {
                exception = true;
                loop = false;
            }
        }
        Assert.assertFalse(exception, "SocketThread was not added to server1");
        for (SocketThread socket : server1.getSocketList().values()) {
            if (socket.getHash().compareTo(server_hash) != 0) {
                server_hash2 = socket.getHash();
            }
        }
        waitSocketThreadState(server1, server_hash2, SocketThread.CONFIRMED);
        waitMessageQueueState(server1, server_hash2, MessageQueue.RUNNING);
        ArrayList<String> sockets = new ArrayList<String>();
        sockets.add(server_hash);
        sockets.add(server_hash2);
        server1.sendMessage("TEST", sockets);
        waitTime();
        Assert.assertTrue(server1.containsHash(server_hash), "Socket1 on server1 has closed");
        Assert.assertTrue(server1.containsHash(server_hash2), "Socket2 on server1 has closed");
        Assert.assertTrue(server2.containsHash(client_hash), "Socket1 on server2 has closed");
        Assert.assertTrue(server2.containsHash(client_hash2), "Socket2 on server2 has closed");
        Assert.assertFalse(server1.getQueueList().get(server_hash).getMessages().contains("TEST"), "Message was not sent on socket1");
        Assert.assertFalse(server1.getQueueList().get(server_hash2).getMessages().contains("TEST"), "Message was not sent on socket2");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testSendMessageToListWithQueue COMPLETED -----");
    }

    /**
     * Tests sending messages using the {@link Server}.sendMessage(String,
     * List(String)) function where the {@link SocketThread}s referenced in the
     * List(String) do not exist.
     */
    @Test
    public void testSendMessageToListNotExist() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSendMessageToListNotExist -----");
        ArrayList<String> sockets = new ArrayList<String>();
        sockets.add("TEST");
        server2.sendMessage("TEST", sockets);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testSendMessageToListNotExist COMPLETED -----");
    }

    /**
     * Tests sending messages using the {@link Server}.sendMessage(String,
     * String) function where the {@link SocketThread} referenced does not
     * exist.
     */
    @Test
    public void testSendMessageNotExist() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSendMessageToListNotExist -----");
        server2.sendMessage("TEST", "TEST");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testSendMessageToListNotExist COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if state is set to
     * {@link Server}.CLIENT. Check the output from LOGGER to assess human
     * readability.
     */
    @Test
    public void testToStringClient() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringClient -----");
        String to_string = null;
        server2.setUseDisconnectedSockets(true);
        to_string = server2.toString();
        boolean server_type = false;
        if (to_string != null) {
            String server_string = "";
            char[] ch = to_string.toCharArray();
            for (int i = 0; i < 6; i++) {
                server_string += ch[i];
            }
            if (server_string.compareTo("Server") == 0) {
                server_type = true;
            }
        }
        Assert.assertTrue(server_type, "Server not detected as a client server when generating String data");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringClient COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString(String ch) function if state is set to
     * {@link Server}.CLIENT. Check the output from LOGGER to assess human
     * readability.
     */
    @Test
    public void testToStringClientCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringClientCh -----");
        String to_string = null;
        server2.setUseDisconnectedSockets(true);
        to_string = server2.toString("\t");
        boolean server_type = false;
        if (to_string != null) {
            String server_string = "";
            char[] ch = to_string.toCharArray();
            for (int i = 1; i < 7; i++) {
                server_string += ch[i];
            }
            if (server_string.compareTo("Server") == 0) {
                server_type = true;
            }
        }
        Assert.assertTrue(server_type, "Server not detected as a client server when generating String data");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringClientCh COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if state is set to
     * {@link Server}.LISTEN. Check the output from LOGGER to assess human
     * readability.
     */
    @Test
    public void testToStringServer() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServer -----");
        String server_hash = "";
        String server_hash2 = "";
        String to_string = null;
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server3);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getSocketList().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        for (SocketThread socket : server1.getSocketList().values()) {
            if (socket.getHash().compareTo(server_hash) != 0) {
                server_hash2 = socket.getHash();
            }
        }
        waitSocketThreadState(server1, server_hash2, SocketThread.CONFIRMED);
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getDisconnectedSockets().contains(server_hash2) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        to_string = server1.toString();
        boolean server_type = false;
        if (to_string != null) {
            String server_string = "";
            char[] ch = to_string.toCharArray();
            for (int i = 0; i < 6; i++) {
                server_string += ch[i];
            }
            if (server_string.compareTo("Listen") == 0) {
                server_type = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(server_type, "Server not detected as a listen server when generating String data");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringServer COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString(String ch) function if state is set to
     * {@link Server}.CLIENT. Check the output from LOGGER to assess human
     * readability.
     */
    @Test
    public void testToStringServerCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerCh -----");
        String server_hash = "";
        String server_hash2 = "";
        String to_string = null;
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server1);
        server_hash = getServerLastSocketHash(server1);
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server3);
        boolean loop = true;
        Timing new_timer = new Timing();
        while (loop) {
            if (server1.getSocketList().size() == 2 || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        for (SocketThread socket : server1.getSocketList().values()) {
            if (socket.getHash().compareTo(server_hash) != 0) {
                server_hash2 = socket.getHash();
            }
        }
        waitSocketThreadState(server1, server_hash2, SocketThread.CONFIRMED);
        try {
            server3.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server3, Server.CLOSED);
        loop = true;
        new_timer.startTiming();
        while (loop) {
            if (server1.getDisconnectedSockets().contains(server_hash2) || new_timer.getTime() > timeout) {
                loop = false;
            }
        }
        to_string = server1.toString("\t");
        boolean server_type = false;
        if (to_string != null) {
            String server_string = "";
            char[] ch = to_string.toCharArray();
            for (int i = 1; i < 7; i++) {
                server_string += ch[i];
            }
            if (server_string.compareTo("Listen") == 0) {
                server_type = true;
            }
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(server_type, "Server not detected as a listen server when generating String data");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringServerCh COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if state is set to
     * {@link Server}.LISTEN and listen_thread has not been started. Check the
     * output from LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerNotStarted() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerNotStarted -----");
        String to_string = null;
        to_string = server1.toString();
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringServerNotStarted COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if state is set to
     * {@link Server}.CLOSED from {@link Server}.CLIENT. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToStringClientClosed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringClientClosed -----");
        String to_string = null;
        try {
            server2.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server2, Server.CLOSED);
        to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringClientClosed COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if state is set to
     * {@link Server}.CLOSED from {@link Server}.LISTEN. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerClosed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerClosed -----");
        String to_string = null;
        try {
            server1.close();
        } catch (IOException | ServerSocketCloseException | TimeoutException e) {
            exception = true;
        }
        waitServerState(server1, Server.CLOSED);
        to_string = server1.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringServerClosed COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if use_message_queues ==
     * true and {@link MessageQueue}.messages is empty. Check the output from
     * LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerMessageQueueEmpty() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueEmpty -----");
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        String to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueEmpty COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString(String ch) function if
     * use_message_queues == true and {@link MessageQueue}.messages is empty.
     * Check the output from LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerMessageQueueEmptyCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueEmptyCh -----");
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        waitMessageQueueState(server2, hash, MessageQueue.RUNNING);
        String to_string = server2.toString("\t");
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueEmptyCh COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString() function if use_message_queues ==
     * true and {@link MessageQueue}.messages is not empty. Check the output
     * from LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerMessageQueueWithMessages() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueWithMessages -----");
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST", hash);
        server2.sendMessage("TEST2", hash);
        server2.sendMessage("TEST3", hash);
        String to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueWithMessages COMPLETED -----");
    }

    /**
     * Tests the {@link Server}.toString(String ch) function if
     * use_message_queues == true and {@link MessageQueue}.messages is not
     * empty. Check the output from LOGGER to assess human readability.
     */
    @Test
    public void testToStringServerMessageQueueWithMessagesCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueWithMessagesCh -----");
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
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException | ServerSocketCloseException | FeatureNotUsedException e) {
            exception = true;
        }
        waitListenThreadStart(server1);
        try {
            hash = server2.addSocket("127.0.0.1", port);
        } catch (IOException | TimeoutException e) {
            exception = true;
        }
        waitSocketThreadAddNotEmpty(server2);
        waitSocketThreadState(server2, hash, SocketThread.CONFIRMED);
        waitMessageQueueAddNotEmpty(server2);
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST", hash);
        server2.sendMessage("TEST2", hash);
        server2.sendMessage("TEST3", hash);
        String to_string = server2.toString("\t");
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueWithMessagesCh COMPLETED -----");
    }
}
