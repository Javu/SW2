package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
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
    /**
     * This int is the parameter used when running the waitTime function in
     * these tests. Change this value to increase or decrease the time waited
     * when waitTime is called.
     */
    int wait = 30;

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
    private void setupServer() throws IOException {
        port = 22222;
        exception = false;
        Game game = EasyMock.createMock(Game.class);

//        game.runVoidMethod();
//        EasyMock.expect(game.parseAction()).andReturn("go fuck yoruself").anyTimes();
//        EasyMock.replay();
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
        try {
            hash = server2.addSocket("127.0.0.1",port);
        } catch (IOException ex) {
            exception = true;
        }
        waitTime();
//        EasyMock.verify();
    }
    
    @Test
    public void testMessageQueueSetMessages() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetMessages -----");
        ArrayList<String> string_array = new ArrayList<String>();
        string_array.add("TEST");
        server2.getSocketList().get(hash).getMessageQueue().setMessages(string_array);
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getMessages().get(0), "TEST", "MessageQueue.messages was not changed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetMessages COMPLETED -----");
    }
    
    @Test
    public void testMessageQueueSetRun() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueueSetRun -----");
        server2.getSocketList().get(hash).getMessageQueue().setRun(2);
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getRun(), 2, "MessageQueue.run was not set to 2");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueueSetRun COMPLETED -----");
    }

    @Test
    public void testMessageQueuePauseAndClearQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndClearQueue -----");
        server2.getSocketList().get(hash).getMessageQueue().pauseQueue();
        server2.getSocketList().get(hash).sendMessage("TEST");
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getRun(), 3, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getSocketList().get(hash).getMessageQueue().clearQueue();
        Assert.assertTrue(server2.getSocketList().get(hash).getMessageQueue().getMessages().isEmpty(), "MessageQueue was not cleared");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndClearQueue COMPLETED -----");
    }
    
    @Test
    public void testMessageQueuePauseAndResumeQueue() {
        LOGGER.log(Level.INFO, "----- STARTING TEST testMessageQueuePauseAndResumeQueue -----");
        server2.getSocketList().get(hash).getMessageQueue().pauseQueue();
        server2.getSocketList().get(hash).sendMessage("TEST");
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getRun(), 3, "MessageQueue.run was not set to 3 (paused)");
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getMessages().get(0), "TEST", "MessageQueue was not paused");
        server2.getSocketList().get(hash).getMessageQueue().resumeQueue();
        waitTime();
        Assert.assertEquals(server2.getSocketList().get(hash).getMessageQueue().getRun(), 1, "MessageQueue.run was not set to 1 (resume)");
        Assert.assertTrue(server2.getSocketList().get(hash).getMessageQueue().getMessages().isEmpty(), "Messages in MessageQueue were not sent after queue was resumed");
        Assert.assertFalse(exception, "Exception found");
        LOGGER.log(Level.INFO, "----- TEST testMessageQueuePauseAndResumeQueue COMPLETED -----");
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
}
