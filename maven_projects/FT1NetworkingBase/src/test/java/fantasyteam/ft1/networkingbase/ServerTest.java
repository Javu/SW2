package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for base server class
 *
 * @author jamessemple
 */
public class ServerTest {

    private Server server1;
    private Server server2;
    int port = 22222;
    int wait = 10;
    
    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(ServerTest.class.getName());

    private void waitTime() {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @BeforeMethod
    private void setupServer() throws IOException {
        Game game = EasyMock.createMock(Game.class);

//        game.runVoidMethod();
//        EasyMock.expect(game.parseAction()).andReturn("go fuck yoruself").anyTimes();
//        EasyMock.replay();
        server1 = new Server(game, port, true);
        server2 = new Server(game, port, false);

//        EasyMock.verify();
    }

    @AfterMethod
    private void deleteServer() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        waitTime();
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitTime();
    }

    @Test
    public void testServerConstructor() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerConstructor -----");
        server2.close();
        Game game = EasyMock.createMock(Game.class);
        server2 = new Server(game,true);
        Assert.assertEquals(server2.getPort(), 0, "Server not constructed");
        Assert.assertEquals(server2.getState(), 0, "Server not set as listen server");
        LOGGER.log(Level.INFO,"----- TEST testServerConstructor COMPLETED -----");
    }
    
    @Test void testServerConstructorListen() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerConstructorListen -----");
        server2.close();
        Game game = EasyMock.createMock(Game.class);
        server2 = new Server(game,false);
        Assert.assertEquals(server2.getPort(), 0, "Server not constructed");
        Assert.assertEquals(server2.getListenThread(), null, "Server not constructed");
        LOGGER.log(Level.INFO,"----- TEST testServerConstructorListen COMPLETED -----");
    }
    
    @Test
    public void testServerConstructorPort() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerConstructorPort -----");
        server2.close();
        Game game = EasyMock.createMock(Game.class);
        server2 = new Server(game,12457,true);
        Assert.assertEquals(server2.getPort(), 12457, "Server not constructed");
        LOGGER.log(Level.INFO,"----- TEST testServerConstructorPort COMPLETED -----");
    }
    
    @Test void testServerConstructorListenPort() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerConstructorListenPort -----");
        server2.close();
        Game game = EasyMock.createMock(Game.class);
        server2 = new Server(game,12457,false);
        Assert.assertEquals(server2.getPort(), 12457, "Server not constructed");
        Assert.assertEquals(server2.getListenThread(), null, "Server not constructed");
        LOGGER.log(Level.INFO,"----- TEST testServerConstructorListenPort COMPLETED -----");
    }

    @Test
    public void testSetPort() {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetPort -----");
        server1.setPort(22223);
        Assert.assertEquals(server1.getPort(), 22223, "Port not changed");
        LOGGER.log(Level.INFO,"----- TEST testSetPort COMPLETED -----");
    }

    @Test
    public void testUseDisconnectSockets() {
        LOGGER.log(Level.INFO,"----- STARTING TEST testUseDisconnectSockets -----");
        server1.setUseDisconnectedSockets(true);
        Assert.assertEquals(server1.getUseDisconnectedSockets(), true, "Use Disonnected Sockets flag not changed");
        LOGGER.log(Level.INFO,"----- TEST testUseDisconnectSockets COMPLETED -----");
    }

    @Test
    public void testSetSocketList() {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetSocketList -----");
        server2.setSocketList(server1.getSocketList());
        Assert.assertEquals(server2.getSocketList(), server1.getSocketList(), "Socket List not changed");
        LOGGER.log(Level.INFO,"----- TEST testSetSocketList COMPLETED -----");
    }

    @Test
    public void testSetDisconnectedSockets() {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetDisconnectedSockets -----");
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("1");
        string_array.add("2");
        server2.setDisconnectedSockets(string_array);
        Assert.assertEquals(server2.getDisconnectedSockets(), string_array, "Disconnected Sockets array not changed");
        LOGGER.log(Level.INFO,"----- TEST testSetDisconnectedSockets COMPLETED -----");
    }

    @Test
    public void testSetListenThreadClient() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetListenThreadClient -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if(server2.getState() == 1) {
            flag1 = true;
        }
        if(flag1) {
            server2.setPort(23231);
            server2.setListenThread();
            if(server2.getState() == 0) {
                flag2 = true;
            }
        }
        Assert.assertTrue(flag2, "Client server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO,"----- TEST testSetListenThreadClient COMPLETED -----");
    }
    
    @Test
    public void testSetListenThreadServer() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetListenThreadServer -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if(server1.getState() == 0) {
            flag1 = true;
        }
        if(flag1) {
            server1.setPort(23231);
            server1.setListenThread();
            if(server1.getState() == 0) {
                flag2 = true;
            }
        }
        Assert.assertTrue(flag2, "Server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO,"----- TEST testSetListenThreadServer COMPLETED -----");
    }
    
    @Test
    public void testSetListenThreadServerOnSamePort() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testSetListenThreadServerOnSamePort -----");
        boolean flag1 = false;
        boolean flag2 = false;
        if(server1.getState() == 0) {
            flag1 = true;
        }
        if(flag1) {
            server1.setListenThread();
            if(server1.getState() == 0) {
                flag2 = true;
            }
        }
        Assert.assertTrue(flag2, "Server's state has not been set as listen Server. listen_thread may have not been created correctly. Server state is " + Integer.toString(server2.getState()));
        LOGGER.log(Level.INFO,"----- TEST testSetListenThreadServerOnSamePort COMPLETED -----");
    }
    
    @Test
    public void testRemoveDisconnectedSocket() {
        LOGGER.log(Level.INFO,"----- STARTING TEST testRemoveDisconnectedSocket -----");
        server1.startThread();
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("r");
        string_array.add("b");
        server1.setDisconnectedSockets(string_array);
        server1.removeDisconnectedSocket("r");
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), "b", "Hash not removed from disconnected_sockets changed");
        LOGGER.log(Level.INFO,"----- TEST testRemoveDisconnectedSocket COMPLETED -----");
    }

    @Test
    public void testServerClientConnectSocket() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientConnectSocket -----");
        String client_hash = "";
        server1.startThread();
        Socket socket = new Socket("127.0.0.1", port);
        client_hash = server2.addSocket(socket);
        waitTime();
        Assert.assertTrue(!client_hash.isEmpty(), "Client not connected");
        LOGGER.log(Level.INFO,"----- TEST testServerClientConnectSocket COMPLETED -----");
    }

    @Test
    public void testServerClientConnectSock() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientConnectSock -----");
        String client_hash = "";
        server1.startThread();
        Sock sock = new Sock("127.0.0.1", port);
        client_hash = server2.addSocket(sock);
        waitTime();
        Assert.assertTrue(!client_hash.isEmpty(), "Client not connected");
        LOGGER.log(Level.INFO,"----- TEST testServerClientConnectSock COMPLETED -----");
    }

    @Test
    public void testServerClientConnectIp() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientConnectIp -----");
        String client_hash = "";
        server1.startThread();
        server2.addSocket("127.0.0.1");
        waitTime();
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        Assert.assertTrue(!client_hash.isEmpty(), "Client not connected");
        LOGGER.log(Level.INFO,"----- TEST testServerClientConnectIp COMPLETED -----");
    }

    @Test
    public void testServerClientConnectIpPort() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientConnectIpPort -----");
        String client_hash = "";
        server1.startThread();
        server2.addSocket("127.0.0.1", port);
        waitTime();
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        if (server1.containsHash(client_hash) == true) {
            client_hash = "connected";
        }
        Assert.assertEquals(client_hash, "connected", "Client not connected");
        LOGGER.log(Level.INFO,"----- TEST testServerClientConnectIpPort COMPLETED -----");
    }

    @Test
    public void testServerClientDisconnect() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientDisconnect -----");
        String client_hash = "";
        server1.startThread();
        server2.addSocket("127.0.0.1");
        waitTime();
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        server2.close();
        waitTime();
        if (server1.containsHash(client_hash) != true || !server1.getSocketList().get(client_hash).getRun()) {
            client_hash = "disconnected";
        }

        Assert.assertEquals(client_hash, "disconnected", "Connection not closed successfully");
        LOGGER.log(Level.INFO,"----- TEST testServerClientDisconnect COMPLETED -----");
    }

    @Test
    public void testServerClientDisconnectWithHash() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClientDisconnectWithHash -----");
        String client_hash = "";
        server1.setUseDisconnectedSockets(true);
        server1.startThread();
        server2.addSocket("127.0.0.1");
        waitTime();
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        server2.close();
        waitTime();
        
        Assert.assertEquals(server1.getDisconnectedSockets().get(0), client_hash, "Disconnected sockets hash not logged");
        LOGGER.log(Level.INFO,"----- TEST testServerClientDisconnectWithHash COMPLETED -----");
    }
    
    @Test
    public void testServerClose() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testServerClose -----");
        server1.startThread();
        waitTime();
        boolean flag1 = false;
        boolean flag2 = false;
        if(server1.getListenThread().getRun()){
            flag2 = true;
        }
        if(flag2){
            server1.close();
            waitTime();
            if(!server1.getListenThread().getRun()){
                flag1 = true;
            }
        }
        Assert.assertTrue(flag1, "Server not closed as a listen server");
        LOGGER.log(Level.INFO,"----- TEST testServerClose COMPLETED -----");
    }
    
    @Test
    public void testReplaceHash() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testReplaceHash -----");
        String client_hash = "";
        Game game = EasyMock.createMock(Game.class);
        Server server3 = new Server(game,port,false);
        server1.setUseDisconnectedSockets(true);
        server1.startThread();
        server2.addSocket("127.0.0.1");
        waitTime();
        ArrayList<String> string_array = new ArrayList<String>();
        for (SocketThread sockets : server1.getSocketList().values()) {
            string_array.add(sockets.getHash());
        }
        server1.setDisconnectedSockets(string_array);
        server3.addSocket("127.0.0.1");
        waitTime();
        for (SocketThread sockets : server1.getSocketList().values()) {
            client_hash = sockets.getHash();
        }
        boolean flag = server1.connectDisconnectedSocket(client_hash, string_array.get(0));
        waitTime();
        Assert.assertTrue(flag, "Sockets hash not changed");
        server3.close();
        LOGGER.log(Level.INFO,"----- TEST testReplaceHash COMPLETED -----");
    }
    
    @Test
    public void testPingSocket() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testPingSocket -----");
        Game game = EasyMock.createMock(Game.class);
        Server server3 = new Server(game,port,false);
        server1.startThread();
        server2.addSocket("127.0.0.1");
        server3.addSocket("127.0.0.1");
        waitTime();
        server1.pingSockets();
        waitTime();
        boolean not_disconnected = server1.getDisconnectedSockets().isEmpty();
        Assert.assertTrue(not_disconnected, "Could not ping sockets");
        server3.close();
        LOGGER.log(Level.INFO,"----- TEST testPingSocket COMPLETED -----");
    }
    
    @Test
    public void testReleasePortNumber() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testReleasePortNumber -----");
        Game game = EasyMock.createMock(Game.class);
        server1.close();
        waitTime();
        Server server3 = new Server(game,port,true);
        Assert.assertEquals(server3.getState(),0,"Server not set as state 0. Port could not be listened on");
        server3.close();
        LOGGER.log(Level.INFO,"----- TEST testReleasePortNumber COMPLETED -----");
    }
    
    @Test
    public void testReleasePortNumberRunning() throws IOException {
        LOGGER.log(Level.INFO,"----- STARTING TEST testReleasePortNumberRunning -----");
        Game game = EasyMock.createMock(Game.class);
        server1.startThread();
        server1.close();
        waitTime();
        Server server3 = new Server(game,port,true);
        Assert.assertEquals(server3.getState(),0,"Server not set as state 0. Port could not be listened on");
        server3.close();
        LOGGER.log(Level.INFO,"----- TEST testReleasePortNumberRunning COMPLETED -----");
    }
}
