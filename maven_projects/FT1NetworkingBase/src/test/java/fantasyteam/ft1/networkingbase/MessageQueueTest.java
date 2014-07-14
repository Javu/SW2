package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
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
 *
 * @author Javu
 */
public class MessageQueueTest {
    
    private Server server1;
    private Server server2;
    int port;
    boolean exception;
    String hash;
    Timing time = new Timing();
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    long wait = 10;

    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(MessageQueueTest.class.getName());

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
    private void setupQueue() throws IOException {
        port = 22222;
        exception = false;
        Game game = EasyMock.createMock(Game.class);
        LOGGER.log(Level.INFO,"Building Server1");
        server1 = new Server(game, port, true);
        LOGGER.log(Level.INFO,"Building Server2");
        server2 = new Server(game, port, false);
        server1.setUseMessageQueues(true);
        server2.setUseMessageQueues(true);
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
    }
    
    @AfterMethod
    private void deleteQueue() throws IOException {
        LOGGER.log(Level.INFO, "+++++ CLOSING server2 (CLIENT SERVER) +++++");
        server2.close();
        time.waitTime(wait);
        LOGGER.log(Level.INFO, "+++++ CLOSING server1 (LISTEN SERVER) +++++");
        server1.close();
        time.waitTime(wait);
    }
    
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
    
    @Test
    public void testMessageQueueSetRun() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetRun -----");
        server2.getQueueList().get(hash).setRun(2);
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 2, "MessageQueue.run was not set to 2");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetRun COMPLETED -----");
    }
    
    @Test
    public void testMessageQueueSetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetHash -----");
        server2.getQueueList().get(hash).setHash("TEST");
        Assert.assertEquals(server2.getQueueList().get(hash).getHash(), "TEST", "MessageQueue.hash was not set to TEST");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetHash COMPLETED -----");
    }
    
    @Test
    public void testMessageQueueGetTimeout() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueGetTimeout -----");
        long timeout = server2.getQueueList().get(hash).getTimeout();
        Assert.assertEquals(timeout, 300000, "Did not return the correct timeout value");
        long new_timeout = 5;
        server2.getQueueList().get(hash).setTimeout(new_timeout);
        timeout = server2.getQueueList().get(hash).getTimeout();
        Assert.assertEquals(timeout, new_timeout, "Did not return the correct timeout value after changing it using MessageQueue.setTimeout(long timeout)");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueGetTimeout COMPLETED -----");
    }
    
    @Test
    public void testMessageQueueGetHash() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueGetHash -----");
        String current_hash = server2.getQueueList().get(hash).getHash();
        Assert.assertEquals(current_hash, hash, "MessageQueue.hash was is not set correctly");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueGetHash COMPLETED -----");
    }

    @Test
    public void testMessageQueuePauseAndClearQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndClearQueue -----");
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST",hash);
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 3, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getQueueList().get(hash).getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getQueueList().get(hash).clearQueue();
        Assert.assertTrue(server2.getQueueList().get(hash).getMessages().isEmpty(), "MessageQueue was not cleared");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndClearQueue COMPLETED -----");
    }
    
    @Test
    public void testMessageQueuePauseAndResumeQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndResumeQueue -----");
        server2.getQueueList().get(hash).pauseQueue();
        server2.sendMessage("TEST",hash);
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 3, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getQueueList().get(hash).getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getQueueList().get(hash).resumeQueue();
        boolean loop = true;
        Timing new_timer = new Timing();
        while(loop) {
            if(server2.getQueueList().get(hash).getMessages().isEmpty() || new_timer.getTime() > 5000) {
                loop = false;
            }
        }
        Assert.assertEquals(server2.getQueueList().get(hash).getRun(), 1, "MessageQueue.run was not set to 1 (resume)");
        Assert.assertTrue(server2.getQueueList().get(hash).getMessages().isEmpty(), "Messages in MessageQueue were not sent after queue was resumed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndResumeQueue COMPLETED -----");
    }
    
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
