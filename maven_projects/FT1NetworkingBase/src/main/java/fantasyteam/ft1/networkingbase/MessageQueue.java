package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link MessageQueue} class is used to queue messages and other advanced
 * message handling logic sent through a Socket rather than just send them
 * straight through the Socket. Each {@link MessageQueue} is intended to be
 * coupled with a {@link SocketThread}, which then queues any messages sent
 * through its {@link Sock} on its {@link MessageQueue}, which will action the
 * messages depending on the queues state.
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
     * Valid state for {@link MessageQueue}, used when the queue has just been
     * constructed but has not started run() yet.
     */
    public static final int NEW = 0;

    /**
     * Valid state for {@link MessageQueue}, used when the queue is meant to be
     * running normally.
     */
    public static final int RUNNING = 1;

    /**
     * Valid state for {@link MessageQueue}, used when there is an error sending
     * messages through the socket.
     */
    public static final int ERROR = 2;

    /**
     * Valid state for {@link MessageQueue}, used when the queue is meant to be
     * paused.
     */
    public static final int PAUSED = 3;

    /**
     * Valid state for {@link MessageQueue}, used when its accompanying
     * {@link SocketThread} on the {@link Server} has been disconnected.
     */
    public static final int DISCONNECT = 4;

    /**
     * Valid state for {@link MessageQueue}, used when it is flagged to be
     * closed.
     */
    public static final int CLOSED = 5;

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
    /**
     * The long in milliseconds used when determining the timeout offset for all
     * default timing operations such as waiting for a reconnection after a
     * socket disconnects or error handling.
     */
    private volatile long timeout;
    /**
     * The unique identifier this {@link MessageQueue} shares with its
     * accompanying {@link SocketThread} given to it by its owning
     * {@link Server}.
     */
    private volatile String hash;
    /**
     * The current state of the MessageQueue. Valid states are: 0 - NEW 1 -
     * RUNNING 2 - ERROR 3 - PAUSED 4 - DISCONNECT 5 - CLOSED
     */
    private volatile int state;
    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(MessageQueue.class.getName());

    /**
     * Takes an instance of the {@link Server} that is creating this instance of
     * {@link MessageQueue} and the unique identifier given to the
     * {@link MessageQueue} by the {@link Server}. Sets the state to 0 (New
     * MessageQueue, not yet started).
     *
     * @param server The {@link Server} that created this {@link MessageQueue}.
     * @param hash The String used as this {@link MessageQueue}'s unique
     * identifier on its owning {@link Server}.
     */
    public MessageQueue(Server server, String hash) {
        messages = new ArrayList<String>();
        this.server = server;
        this.hash = hash;
        timer = null;
        timeout = 300000;
        state = NEW;
    }

    /**
     * Closes the {@link MessageQueue}. Sets the state to CLOSED.
     */
    public synchronized void close() {
        state = CLOSED;
    }

    /**
     * Loop used to handle messages in the messages ArrayList. If state is set
     * to CLOSED the loop will exit and the Thread will terminate.
     */
    @Override
    public void run() {
        state = RUNNING;
        while (state >= 1 && state <= 4) {
            if (state == RUNNING || state == ERROR) {
                if (!messages.isEmpty() && server.getSocketList() != null && server.getSocketList().containsKey(hash) && (socket().getRun() == SocketThread.RUNNING || socket().getRun() == SocketThread.CONFIRMED || socket().getRun() == SocketThread.ERROR)) {
                    try {
                        LOGGER.log(Level.INFO, "Attempting to send message {0} through MessageQueue for SocketThread {1}", new Object[]{messages.get(0), hash});
                        socket().getSocket().sendMessage(messages.get(0));
                        messages.remove(0);
                        state = RUNNING;
                        if (timer != null) {
                            timer = null;
                        }
                    } catch (IOException e) {
                        state = ERROR;
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
            } else if (state == DISCONNECT) {
                if (server.getUseDisconnectedSockets()) {
                    if (timer == null) {
                        LOGGER.log(Level.INFO, "SocketThread with hash {0} has been disconnected, waiting to re-establish connection", hash);
                        timer = new Timing();
                    } else if (timer.getTime() > timeout) {
                        LOGGER.log(Level.INFO, "SocketThread with hash {0} did not reconnect within the given timeout", hash);
                        server.removeDisconnectedSocket(hash);
                        server.removeQueue(hash);
                    } else {
                        messages.clear();
                    }
                } else {
                    state = RUNNING;
                    LOGGER.log(Level.INFO, "Server is not set to use disconnecting functionality. MessageQueue cannot be set to state DISCONNECT. State set to RUNNING");
                }
            }
        }
        messages.clear();
        messages = null;
        timer = null;
        LOGGER.log(Level.INFO, "MessageQueue successfully closed. State {0}", state);
    }

    private SocketThread socket() {
        return server.getSocketList().get(hash);
    }

    /**
     * Sets the attribute messages, the ArrayList used to hold all the queued
     * message for the socket.
     *
     * @param messages ArrayList(String) to set messages to.
     */
    public synchronized void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    /**
     * Sets the attribute timeout. Timeout is the default value in milliseconds
     * to wait on all timing functions such as disconnection timeouts or error
     * handling.
     *
     * @param timeout long to set timeout to.
     */
    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets the attribute hash, the unique identifier given to this
     * {@link MessageQueue}. This identifier is generally shared with its
     * accompanying {@link SocketThread} and is given to it by its owning
     * {@link Server}.
     *
     * @param hash Srring to set hash to.
     */
    public synchronized void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Sets the attribute state, the current state of the {@link MessageQueue}. Valid states are: 0 - NEW 1 - RUNNING 2 - ERROR
     * 3 - PAUSED 4 - DISCONNECT 5 - CLOSED.
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

    /**
     * Returns the attribute timeout.
     *
     * @return the long timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Returns the attribute hash.
     *
     * @return the String hash.
     */
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
    public synchronized void queueMessage(String message) {
        LOGGER.log(Level.INFO, "Queued message {0} for SocketThread {1}", new Object[]{message, hash});
        messages.add(message);
    }

    /**
     * Pauses the {@link MessageQueue} by settings its state to 3. When state ==
     * 3 the queue will still loop however it will not try to process any
     * messages.
     */
    public synchronized void pauseQueue() {
        state = PAUSED;
        LOGGER.log(Level.INFO, "Paused MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Resumes the {@link MessageQueue} by setting its state to 1. When state ==
     * 1 the queue will loop and process messages as normal.
     */
    public synchronized void resumeQueue() {
        state = RUNNING;
        LOGGER.log(Level.INFO, "Resumed MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Clears the ArrayList(String) messages. All messages still queued and not
     * handled will be discarded.
     */
    public synchronized void clearQueue() {
        messages.clear();
        LOGGER.log(Level.INFO, "Cleared MessageQueue for SocketThread {0}", hash);
    }

    /**
     * Sets the MessageQueue to state DISCONNECT and waits for a reconnection or
     * times out if timeout is reached before a reconnection is made.
     */
    public synchronized void queueDisconnected() {
        state = DISCONNECT;
        LOGGER.log(Level.INFO, "Set MessageQueue for SocketThread {0} to disconnected", hash);
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
