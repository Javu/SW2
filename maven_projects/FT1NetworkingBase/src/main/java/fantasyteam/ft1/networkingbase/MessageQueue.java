package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link MessageQueue} class is used to queue messages sent through a
 * {@link Sock} rather than just send them straight through the {@link Sock}.
 * Each {@link MessageQueue} is intended to be coupled with a
 * {@link SocketThread}, which then queues any messages sent through its
 * {@link Sock} on its {@link MessageQueue}, which will action the messages
 * depending on the queues state.
 *
 * The implementation currently only has options to pause, resume and clear the
 * queue. It will be fleshed out to include error handling and a system for
 * acknowledging whether each message is received on the other end and resending
 * messages if they are not acknowledged within a specified timeout.
 *
 * @author javu
 */
public class MessageQueue extends Thread {

    /**
     * ArraList<String> used to queue messages to be sent on the {@link Sock}.
     */
    private volatile ArrayList<String> messages;
    /**
     * The instance of {@link Server} that created this instance of
     * {@link MessageThread}.
     */
    private volatile Server server;
    /**
     * {@link fantasyteam.ft1.Timing} used to track timeouts for global errors
     * on the socket (affecting the entire socket, not just an individual
     * message).
     */
    private Timing timer;
    private volatile long timeout;
    private volatile String hash;
    /**
     * The current state of the MessageQueue. Valid states are: 0 - New 1 -
     * Running 2 - Errored 3 - Paused 4 - Disconnected 5 - Closed
     */
    private volatile int state;
    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(MessageQueue.class.getName());

    /**
     * Takes an instance of the {@link SocketThread} that is creating this
     * instance of {@link MessageQueue}. Sets the state to 0 (New MessageQueue,
     * not yet started).
     *
     * @param server The {@link Server} that created this {@link MessageQueue}.
     */
    public MessageQueue(Server server, String hash) {
        messages = new ArrayList<String>();
        this.server = server;
        this.hash = hash;
        timer = null;
        timeout = 300000;
        state = 0;
    }

    /**
     * Closes the {@link MessageQueue}. Sets the state to 4 (Closed).
     */
    public synchronized void close() {
        state = 5;
    }

    /**
     * Loop used to handle messages in the messages ArrayList. If state is set
     * to 5 (Closed) the loop will exit and the Thread will terminate.
     */
    @Override
    public void run() {
        state = 1;
        while (state >= 1 && state <= 4) {
            if (state < 3) {
                if (!messages.isEmpty() && server.getSocketList() != null && server.getSocketList().containsKey(hash) && socket().getRun() >= 1 && socket().getRun() <= 3) {
                    try {
                        LOGGER.log(Level.INFO, "Attempting to send message {0} through MessageQueue for SocketThread {1}", new Object[]{messages.get(0), hash});
                        socket().getSocket().sendMessage(messages.get(0));
                        messages.remove(0);
                        state = 1;
                        if (timer != null) {
                            timer = null;
                        }
                    } catch (IOException e) {
                        state = 2;
                        if (timer == null) {
                            timer = new Timing();
                        }
                        LOGGER.log(Level.SEVERE, "Could not send message: {0}. Exception: {1}", new Object[]{messages.get(0), e});
                        if (timer.getTime() > 5000) {
                            LOGGER.log(Level.SEVERE, "Sending message has timed out. Disconnecting socket. Message: ", messages.get(0));
                            server.disconnect(hash);
                        }
                    }
                }
            } else if(state == 4) {
                if(server.getUseDisconnectedSockets()) {
                    if(timer == null) {
                        LOGGER.log(Level.INFO,"SocketThread with hash {0} has been disconnected, waiting to re-establish connection", hash);
                        timer = new Timing();
                    } else if(timer.getTime() > timeout) {
                        LOGGER.log(Level.INFO,"SocketThread with hash {0} did not reconnect within the given timeout", hash);
                        server.removeDisconnectedSocket(hash);
                        server.removeQueue(hash);
                    } else {
                        messages.clear();
                    }
                } else {
                    state = 1;
                    LOGGER.log(Level.INFO,"Server is not set to use disconnecting functionality. MessageQueue cannot be set to state 4. State set to 1");
                }   
            }
        }
        messages.clear();
        messages = null;
        timer = null;
        LOGGER.log(Level.INFO, "MessageQueue successfully closed");
    }

    private SocketThread socket() {
        return server.getSocketList().get(hash);
    }

    /**
     * Sets the attribute messages.
     *
     * @param messages ArrayList(String) to set messages to.
     */
    public synchronized void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public synchronized void setHash(String hash) {
        this.hash = hash;
    }
    
    /**
     * Sets the attribute state. Valid states are: 0 - New 1 - Running 2 -
     * Errored 3 - Paused 4 - Disconnected 5 - Closed
     *
     * @param state the int to set state to.
     */
    public synchronized void setRun(int state) {
        this.state = state;
    }

    /**
     * Returns the attribute messages.
     *
     * @return the ArrayList(String) messages.
     */
    public ArrayList<String> getMessages() {
        return messages;
    }

    public long getTimeout() {
        return timeout;
    }
    
    public String getHash() {
        return hash;
    }
    
    /**
     * Returns the attribute state.
     *
     * @return the int state.
     */
    public int getRun() {
        return state;
    }

    /**
     * Adds a String message to the ArrayList(String) messages. This is the
     * method used to queue messages for handling by the {@link MessageQueue}.
     *
     * @param message the String to queue in messages.
     */
    public void queueMessage(String message) {
        LOGGER.log(Level.INFO, "Queued message {0} for SocketThread {1}", new Object[]{message, hash});
        messages.add(message);
    }

    /**
     * Pauses the {@link MessageQueue} by settings its state to 3. When state ==
     * 3 the queue will still loop however it will not try to process any
     * messages.
     */
    public void pauseQueue() {
        state = 3;
        LOGGER.log(Level.INFO, "Paused MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Resumes the {@link MessageQueue} by setting its state to 1. When state ==
     * 1 the queue will loop and process messages as normal.
     */
    public void resumeQueue() {
        state = 1;
        LOGGER.log(Level.INFO, "Resumed MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Clears the ArrayList(String) messages. All messages still queued and not
     * handled will be discarded.
     */
    public void clearQueue() {
        messages.clear();
        LOGGER.log(Level.INFO, "Cleared MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Puts the attribute states of {@link MessageQueue} in readable form.
     *
     * @return Attributes of {@link MessageQueue} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = toString("");
        return to_string;
    }

    /**
     * Puts the attribute states of {@link MessageQueue} in readable form. Takes
     * String input to assist formatting. Useful to add special characters to
     * assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link MessageQueue} in a readable String form.
     */
    public String toString(String ch) {
        String to_string = ch + "Owning SocketThread hash: " + hash + "\n" + ch + "State: " + state;
        if (!messages.isEmpty()) {
            to_string += "\n" + ch + "Queued messages:";
            for (String message : messages) {
                to_string += "\n" + ch + "\t" + message;
            }
        } else {
            to_string += "\n" + ch + "There are no queued messages";
        }
        return to_string;
    }
}
