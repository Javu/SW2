package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
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
public class SocketThreadTest {

    private Server server1;
    private Server server2;
    private int port;
    private boolean exception;
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    int wait = 30;

    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(SocketThreadTest.class.getName());

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
    private void setupSocketThread() throws IOException {
        port = 22223;
        exception = false;
        Game game = EasyMock.createMock(Game.class);

//        game.runVoidMethod();
//        EasyMock.expect(game.parseAction()).andReturn("go fuck yoruself").anyTimes();
//        EasyMock.replay();
        server1 = new Server(game, port, true);
        server2 = new Server(game, port, false);

//        EasyMock.verify();
    }

    @AfterMethod
    private void deleteSocketThread() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        waitTime();
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        waitTime();
    }

    @Test
    public void testClose() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testClose -----");
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        try {
            server2.getSocketList().get(hash).close();
        } catch (IOException ex) {
            exception = true;
        }
        Assert.assertFalse(exception, "SocketThread not closed correctly");
        LOGGER.log(Level.INFO, "----- TEST testClose COMPLETED -----");
    }
    
    @Test
    public void testSetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testSetHash -----");
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        String new_hash = "Hi";
        server2.getSocketList().get(hash).setHash(new_hash);
        Assert.assertFalse(exception, "Exception found");
        Assert.assertEquals(server2.getSocketList().get(hash).getHash(), new_hash, "Hash not changed correctly");
        server2.getSocketList().get(hash).setHash(hash);
        LOGGER.log(Level.INFO, "----- TEST testSetHash COMPLETED -----");
    }
    
    @Test
    public void testToString() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testToString -----");
        try {
            server1.startThread();
        } catch(IOException e) {
            exception = true;
        }
        String hash = "";
        try {
            hash = server2.addSocket("127.0.0.1");
        } catch (IOException ex) {
            exception = true;
        }
        String to_string = null;
        to_string = server2.getSocketList().get(hash).toString();
        Assert.assertFalse(exception, "Exception found");
        Assert.assertNotEquals(to_string, null, "SocketThread data not generated into a readable String with added character");
        LOGGER.log(Level.INFO, "SocketThread String details: \n{0}", to_string);
        LOGGER.log(Level.INFO, "----- TEST testToString COMPLETED -----");
    }
}