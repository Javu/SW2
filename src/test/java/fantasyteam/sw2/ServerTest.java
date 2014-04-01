package fantasyteam.sw2;

import fantasyteam.sw2.networking.ListenServer;
import fantasyteam.sw2.networking.Server;
import fantasyteam.sw2.networking.SocketThread;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for base server class
 * @author jamessemple
 */
public class ServerTest {
    
    private Server server1;
    private Server server2;
    
    @BeforeMethod
    private void setupServer() throws IOException {
        server1 = new ListenServer(22222);
        server2 = new Server(22222);
    }
    
    @Test
    public void testServerClientConnect() throws IOException {
        String server_hash = "";
        String client_hash = "";
        server1.startThread();
        server_hash = server2.addSocketByIp("127.0.0.1");
        for(SocketThread sockets : server1.getSocketList().values()){
            client_hash = sockets.getHash();
        }
        if(server1.getSocketList().containsKey(client_hash)==true){
            client_hash="connected";
        }
        Assert.assertEquals(client_hash,"connected","Connection successful");
    }
    
    @Test
    public void testServerClientDisconnect() throws IOException {
        String server_hash = "";
        String client_hash = "";
        server1.startThread();
        server_hash = server2.addSocketByIp("127.0.0.1");
        for(SocketThread sockets : server1.getSocketList().values()){
            client_hash = sockets.getHash();
        }
        server2.close();
        if(server1.getSocketList().containsKey(client_hash)!=true){
            client_hash="disconnected";
        }
        Assert.assertEquals(client_hash,"disconnected","Connection not closed successfully");
    }
}