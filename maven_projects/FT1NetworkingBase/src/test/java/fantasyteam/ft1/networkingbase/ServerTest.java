package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

    private Server server1;
    private Server server2;
    private Game game;
    int port;
    boolean exception;
    Timing time = new Timing();
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    long wait = 20;

    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(ServerTest.class.getName());

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
        game = createMock(Game.class);
        LOGGER.log(Level.INFO,"Building Server1");
        server1 = new Server(game, port, true);
        LOGGER.log(Level.INFO,"Building Server2");
        server2 = new Server(game, port, false);
    }

    @AfterMethod
    private void deleteServer() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        time.waitTime(wait);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        time.waitTime(wait);
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
        } catch (IOException ex) {
            exception = true;
        }
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
        } catch (IOException ex) {
            exception = true;
        }
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
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server2 = new Server(game, 12457, true);
        } catch (IOException ex) {
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
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server2 = new Server(game, 12457, false);
        } catch (IOException ex) {
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
        server1.setPort(22223);
        Assert.assertEquals(server1.getPort(), 22223, "Port not changed");
        LOGGER.log(Level.INFO, "----- TEST testSetPort COMPLETED -----");
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
        server1.setUseMessageQueues(true);
        Assert.assertEquals(server1.getUseMessageQueues(), true, "Use Message Queues flag not changed");
        LOGGER.log(Level.INFO, "----- TEST testUseMessageQueues COMPLETED -----");
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
        if (server2.getState() == 1) {
            flag1 = true;
        }
        if (flag1) {
            server2.setPort(23231);
            try {
                server2.setListenThread();
            } catch (IOException ex) {
                exception = true;
            }
            if (server2.getState() == 0) {
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
        if (server1.getState() == 0) {
            flag1 = true;
        }
        if (flag1) {
            server1.setPort(23231);
            try {
                server1.setListenThread();
            } catch (IOException ex) {
                exception = true;
            }
            if (server1.getState() == 0) {
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
        if (server1.getState() == 0) {
            flag1 = true;
        }
        if (flag1) {
            try {
                server1.setListenThread();
            } catch (IOException ex) {
                exception = true;
            }
            if (server1.getState() == 0) {
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
        if (server2.getState() == 1) {
            flag1 = true;
        }
        if (flag1) {
            server2.setPort(23231);
            try {
                server2.setListenThread();
            } catch (IOException ex) {
                exception = true;
            }
            if (server2.getState() == 0) {
                flag2 = true;
                try {
                    server2.startThread();
                } catch(IOException e) {
                    exception = true;
                }
                time.waitTime(wait);
            }
        }
        if (flag2) {
            try {
                server1.addSocket("127.0.0.1", 23231);
            } catch (IOException ex) {
                exception = true;
            }
            time.waitTime(wait);
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
     * This test ensures that the removeDisconnectedSocket method removes the
     * specified String from the disconnected_sockets array increments all other
     * values back into order correctly.
     */
    @Test
    public void testRemoveDisconnectedSocket() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testRemoveDisconnectedSocket -----");
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("r");
        string_array.add("b");
        server1.setDisconnectedSockets(string_array);
        server1.removeDisconnectedSocket("r");
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), "b", "Hash not removed from disconnected_sockets changed");
        LOGGER.log(Level.INFO, "----- TEST testRemoveDisconnectedSocket COMPLETED -----");
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
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            client_hash = server2.addSocket(socket);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertFalse(client_hash.isEmpty(), "Client not connected");
        Assert.assertFalse(server2.getSocketList().isEmpty(), "SocketThread not added to socket_list");
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
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        Sock sock = null;
        try {
            sock = new Sock("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            client_hash = server2.addSocket(sock);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertFalse(client_hash.isEmpty(), "Client not connected");
        Assert.assertFalse(server2.getSocketList().isEmpty(), "SocketThread not added to socket_list");
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
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertFalse(client_hash.isEmpty(), "Client not connected");
        Assert.assertFalse(server2.getSocketList().isEmpty(), "SocketThread not added to socket_list");
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
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        if (server1.containsHash(client_hash) == true) {
            client_hash = "connected";
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(client_hash, "connected", "Client not connected");
        Assert.assertFalse(server2.getSocketList().isEmpty(), "SocketThread not added to socket_list");
        LOGGER.log(Level.INFO, "----- TEST testServerClientConnectIpPort COMPLETED -----");
    }
    
    @Test
    public void testServerClientConnectWithMessageQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientConnectWithMessageQueue -----");
        int runningServer = 0;
        int runningClient = 0;
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1", port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (MessageQueue queues : server1.getQueueList().values()) {
            runningServer = queues.getRun();
        }
        for (MessageQueue queues : server2.getQueueList().values()) {
            runningClient = queues.getRun();
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(runningServer, 1, "Server MessageQueue not started");
        Assert.assertEquals(runningClient, 1, "Client MessageQueue not started");
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
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        try {
            server2.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        if (server1.containsHash(client_hash) != true || server1.getSocketList().get(client_hash).getRun() == 4) {
            client_hash = "disconnected";
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(client_hash, "disconnected", "Connection not closed successfully");
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
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        try {
            server2.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), client_hash, "Disconnected sockets hash not logged");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectWithHash COMPLETED -----");
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
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        boolean flag1 = false;
        boolean flag2 = false;
        if (server1.getListenThread().getRun()) {
            flag2 = true;
        }
        if (flag2) {
            try {
                server1.close();
            } catch (IOException ex) {
                exception = true;
            }
            time.waitTime(wait);
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
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException ex) {
            exception = true;
        }
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for (SocketThread sockets : server1.getSocketList().values()) {
            string_array.add(sockets.getHash());
        }
        ArrayList<String> disconnected_socket = new ArrayList<String>();
        disconnected_socket.add(string_array.get(0));
        server1.setDisconnectedSockets(disconnected_socket);
        boolean flag = server1.connectDisconnectedSocket(string_array.get(1), string_array.get(0));
        time.waitTime(wait);
        Assert.assertTrue(flag, "Sockets hash not changed");
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testReplaceHash COMPLETED -----");
    }

    /**
     * Tests the pingSockets function to send a blank String to two connected
     * sockets and ensures neither of them are detected as disconnected after
     * sending the message.
     */
    @Test
    public void testPingSocket() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testPingSocket -----");
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server1.pingSockets();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        boolean not_disconnected = server1.getDisconnectedSockets().isEmpty();
        Assert.assertTrue(not_disconnected, "Could not ping sockets");
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
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
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Server server3 = null;
        try {
            server3 = new Server(game, port, true);
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertEquals(server3.getState(), 0, "Server not set as state 0. Port could not be listened on");
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
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
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server1.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Server server3 = null;
        try {
            LOGGER.log(Level.INFO,"Building Server3");
            server3 = new Server(game, port, true);
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertEquals(server3.getState(), 0, "Server not set as state 0. Port could not be listened on");
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
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
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        Assert.assertEquals(server2.getState(), 1, "server2 not set as client server");
        Assert.assertEquals(server2.getListenThread(), null, "ListenThread started on client server");
        LOGGER.log(Level.INFO, "----- TEST testStartThreadClient COMPLETED -----");
    }
    
    /**
     * Calls the listen method on a Server setup as a client. Ensures that
     * the call doesn't do anything.
     */
    @Test
    public void testListenClient() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testListenClient -----");
        String listen = "";
        try {
            listen = server2.listen();
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getState(), 1, "server2 not set as client server");
        Assert.assertEquals(listen, null, "Client server attempted to listen");
        LOGGER.log(Level.INFO, "----- TEST testListenClient COMPLETED -----");
    }

    @Test
    public void testServerUseMessageQueueSend() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerUseMessageQueueSend -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        server2.sendMessage("TEST",hash);
        time.waitTime(wait);
        ArrayList<String> test_queue = server2.getQueueList().get(hash).getMessages();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertTrue(test_queue.isEmpty(),"Message was not sent through MessageQueue");
        LOGGER.log(Level.INFO, "----- TEST testServerUseMessageQueueSend COMPLETED -----");
    }
    
    @Test
    public void testServerUseMessageQueueWithPauseAndResume() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerUseMessageQueueWithPauseAndResume -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        server2.getQueueList().get(hash).pauseQueue();
        time.waitTime(wait);
        server2.sendMessage("TEST",hash);
        server2.sendMessage("TEST2",hash);
        ArrayList<String> test_queue = server2.getQueueList().get(hash).getMessages();
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 3, "MessageQueue state was not changed to 3 (Paused)");
        Assert.assertEquals("TEST", test_queue.get(0), "MessageQueue was not paused");
        Assert.assertEquals("TEST2", test_queue.get(1), "MessageQueue was not paused");
        
        server2.getQueueList().get(hash).resumeQueue();
        time.waitTime(wait);
        test_queue = server2.getQueueList().get(hash).getMessages();
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 1, "MessageQueue state was not changed to 1 (Running)");
        Assert.assertTrue(test_queue.isEmpty(), "MessageQueue was not resumed");
        
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerUseMessageQueueWithPauseAndResume COMPLETED -----");
    }
    
    @Test
    public void testNetworkingEncodeParseMessage() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testNetworkingEncodeParseMessage -----");
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("ACTION");
        parameters.add("PARAM1");
        parameters.add("PARAM2");
        String hash = "hash";
        game.handleAction(parameters,hash);
        replay(game);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        String client_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String old_hash = "";
        for(SocketThread socket : server1.getSocketList().values()){
            old_hash = socket.getHash();
        }
        server1.replaceHash(old_hash,hash);
        time.waitTime(wait);
        String action = "ACTION";
        ArrayList<String> action_parameters = new ArrayList<String>();
        action_parameters.add("PARAM1");
        action_parameters.add("PARAM2");
        server2.sendAction(action, action_parameters, client_hash);
        time.waitTime(wait);
        verify(game);
        LOGGER.log(Level.INFO, "----- TEST testNetworkingEncodeParseMessage COMPLETED -----");
    }
    
    @Test
    public void testServerClientDisconnectAndReconnect() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectAndReconnect -----");
        server1.setUseMessageQueues(true);
        server1.setUseDisconnectedSockets(true);
        server2.setUseMessageQueues(true);
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String client_hash = "";
        String server_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash = socket.getHash();
        }
        server1.getQueueList().get(server_hash).pauseQueue();
        time.waitTime(wait);
        server1.sendMessage("TEST",server_hash);
        server1.sendMessage("TEST2",server_hash);
        time.waitTime(wait);
        server2.disconnect(client_hash);
        time.waitTime(wait);
        Assert.assertEquals(server1.getQueueList().get(server_hash).getRun(),4,"MessageQueue on server not set to sate 4 after client disconnection");
        Assert.assertTrue(server1.getQueueList().get(server_hash).getMessages().isEmpty(),"MessageQueue not cleared after state set to 4");
        Assert.assertTrue(server1.getDisconnectedSockets().contains(server_hash),"Disconnected hash was not put into disconnected_sockets list");
        try {
            client_hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String server_hash2 = "";
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash2 = socket.getHash();
        }
        boolean connected = server1.connectDisconnectedSocket(server_hash2, server_hash);
        time.waitTime(wait);
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash2 = socket.getHash();
        }
        Assert.assertEquals(server1.getQueueList().get(server_hash).getRun(),1,"MessageQueue on server not set to sate 1 after client reconnection");
        Assert.assertTrue(server1.getDisconnectedSockets().isEmpty(),"Disconnected hash was not removed from disconnected_sockets list");
        Assert.assertTrue(server1.getSocketList().containsKey(server_hash),"Reconnected sockt was not moved into old key in socket_list");
        Assert.assertEquals(server_hash2,server_hash,"Reconnected sockets hash was not set to saved hash");
        Assert.assertTrue(connected,"connectDisconnectedSocket did not return true after successful reconnection");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectAndReconnect COMPLETED -----");
    }
    
    @Test
    public void testServerClientDisconnectAndTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testServerClientDisconnectAndTimeout -----");
        long disconnect_timeout = 50;
        server1.setUseMessageQueues(true);
        server1.setUseDisconnectedSockets(true);
        server2.setUseMessageQueues(true);
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String client_hash = "";
        String server_hash = "";
        try {
            client_hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash = socket.getHash();
        }
        server1.getQueueList().get(server_hash).setTimeout(disconnect_timeout);
        server1.getQueueList().get(server_hash).pauseQueue();
        time.waitTime(wait);
        server1.sendMessage("TEST",server_hash);
        server1.sendMessage("TEST2",server_hash);
        time.waitTime(wait);
        server2.disconnect(client_hash);
        time.waitTime(wait);
        Assert.assertEquals(server1.getQueueList().get(server_hash).getRun(),4,"MessageQueue on server not set to state 4 after client disconnection");
        Assert.assertTrue(server1.getQueueList().get(server_hash).getMessages().isEmpty(),"MessageQueue not cleared after state set to 4");
        Assert.assertTrue(server1.getDisconnectedSockets().contains(server_hash),"Disconnected hash was not put into disconnected_sockets list");
        Timing timer = new Timing();
        timer.waitTime(disconnect_timeout);
        try {
            client_hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String server_hash2 = "";
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash2 = socket.getHash();
        }
        boolean connected = server1.connectDisconnectedSocket(server_hash2, server_hash);
        time.waitTime(wait);
        for(SocketThread socket : server1.getSocketList().values()) {
            server_hash2 = socket.getHash();
        }
        Assert.assertFalse(server1.getQueueList().containsKey(server_hash),"MeesageQueue did not close itself after timeout period");
        Assert.assertFalse(server1.getDisconnectedSockets().contains(server_hash),"Timed out sockets hash was not removed from disconnected_sockets list");
        Assert.assertFalse(server1.getSocketList().containsKey(server_hash),"socket_list should no longer contain a value for the timed out sockets hash");
        Assert.assertFalse(connected,"connectDisconnectedSocket did not return false after attempting reconnection for timed out socket");
        Assert.assertTrue(server1.getSocketList().containsKey(server_hash2),"New SocketThreaad was removed after attempting to reconnect with old SocketThreads hash");
        Assert.assertTrue(server1.getQueueList().containsKey(server_hash2),"New MessageQueue was removed after attempting to reconnect with old SocketThreads hash");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testServerClientDisconnectAndTimeout COMPLETED -----");
    }
    
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

    @Test
    public void testToStringServer() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServer -----");
        String to_string = null;
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
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

    @Test
    public void testToStringServerCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerCh -----");
        String to_string = null;
        server1.setUseDisconnectedSockets(true);
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        Server server3 = null;
        try {
            server3 = new Server(game, port, false);
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server3.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            server3.close();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
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

    @Test
    public void testToStringServerNotStarted() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerNotStarted -----");
        String to_string = null;
        to_string = server1.toString();
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringServerNotStarted COMPLETED -----");
    }

    @Test
    public void testToStringClientClosed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringClientClosed -----");
        String to_string = null;
        try {
            server2.close();
        } catch (IOException ex) {
            exception = true;
        }
        to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringClientClosed COMPLETED -----");
    }

    @Test
    public void testToStringServerClosed() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringClientClosed -----");
        String to_string = null;
        try {
            server1.close();
        } catch (IOException ex) {
            exception = true;
        }
        to_string = server1.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToStringClientClosed COMPLETED -----");
    }
    
    @Test
    private void testToStringServerMessageQueueEmpty() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueEmpty -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueEmpty COMPLETED -----");
    }
    
    @Test
    private void testToStringServerMessageQueueEmptyCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueEmptyCh -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        String to_string = server2.toString("\t");
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueEmptyCh COMPLETED -----");
    }
    
    @Test
    private void testToStringServerMessageQueueWithMessages() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueWithMessages -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST",hash);
        server2.sendMessage("TEST2",hash);
        server2.sendMessage("TEST3",hash);
        String to_string = server2.toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueWithMessages COMPLETED -----");
    }
    
    @Test
    private void testToStringServerMessageQueueWithMessagesCh() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToStringServerMessageQueueWithMessagesCh -----");
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
        String hash = "";
        try {
            server1.startThread();
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        time.waitTime(wait);
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST",hash);
        server2.sendMessage("TEST2",hash);
        server2.sendMessage("TEST3",hash);
        String to_string = server2.toString("\t");
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "Server data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "Server String details: \n{0}", to_string);
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testToStringServerMessageQueueWithMessagesCh COMPLETED -----");
    }
}
