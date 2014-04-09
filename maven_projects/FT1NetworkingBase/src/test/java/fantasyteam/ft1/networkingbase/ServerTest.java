package fantasyteam.ft1.networkingbase;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
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
    
    @AfterMethod
    private void deleteServer() throws IOException {
        server2.close();
        server1.close();
    }
    
    @Test
    public void testServerClientConnectIp() throws IOException {
        String client_hash = "";
        server1.startThread();
        server2.addSocket("127.0.0.1");
        for(SocketThread sockets : server1.getSocketList().values()){
            client_hash = sockets.getHash();
        }
        Assert.assertTrue(!client_hash.isEmpty(),"Client not connected");
    }
    
    @Test
    public void testServerClientConnectIpPort() throws IOException {
        String client_hash = "";
        server1.startThread();
        server2.addSocket("127.0.0.1",22222);
        for(SocketThread sockets : server1.getSocketList().values()){
            client_hash = sockets.getHash();
        }
        if(server1.getSocketList().containsKey(client_hash)==true){
            client_hash="connected";
        }
        Assert.assertEquals(client_hash,"connected","Connection successful");
    }
    /*
    @Test
    public void testServerClientDisconnect() throws IOException {
        String client_hash = "";
        int count =0;
        server1.startThread();
        server2.addSocketByIp("127.0.0.1");
        for(SocketThread sockets : server1.getSocketList().values()){
            client_hash = sockets.getHash();
        }
        server2.close();
        while(count < 10000){
            count++;
        }
        if(server1.getSocketList().containsKey(client_hash)!=true){
            client_hash="disconnected";
        }
        
        Assert.assertEquals(client_hash,"disconnected","Connection not closed successfully");
    }*/
}