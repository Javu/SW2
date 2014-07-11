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
 *
 * @author javu
 */
public class SockTest {
    
    private Sock sock;
    private String ip;
    private int port;
    private boolean exception;
    private Server server;
    Timing time = new Timing();
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    long wait = 10;
    
    protected static final Logger LOGGER = Logger.getLogger(SockTest.class.getName());
    
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
    private void setupSock() throws IOException {
        ip = "127.0.0.1";
        port = 22227;
        Game game = EasyMock.createMock(Game.class);
        server = new Server(game,port,true);
        time.waitTime(wait);
        server.startThread();
        time.waitTime(wait);
        sock = new Sock(ip,port);
        time.waitTime(wait);
        exception = false;
    }
    
    @AfterMethod
    private void deleteSock() throws IOException {
        sock.close();
        time.waitTime(wait);
        server.close();
        time.waitTime(wait);
    }
    
    @Test
    public void testDefaultConstructor() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testDefaultConstructor -----");
        Sock sock1 = new Sock();
        Assert.assertEquals(sock1.getSocket(), null, "Value of socket in Sock not set to null");
        Assert.assertEquals(sock1.getOut(), null, "Value of out in Sock not set to null");
        Assert.assertEquals(sock1.getIn(), null, "Value of in in Sock not set to null");
        LOGGER.log(Level.INFO, "----- TEST testDefaultConstructor COMPLETED -----");
        try {
            sock.close();
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse(exception, "Exception found");
    }
    
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
