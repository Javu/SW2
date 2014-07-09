package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link SocketThread} class is used to hold socket based connections and
 * control interaction with the socket. It extends the Thread class to allow
 * multiple {@link SocketThread}s to be run at once allowing for more seamless,
 * concurrent connections when multiple socket based connections need to be
 * made.
 *
 * @author javu
 */
public class SocketThread extends Thread {

    /**
     * {@link Sock} used to hold Socket connection and interface with it.
     */
    private volatile Sock socket;
    /**
     * Instance of the {@link Server} class that created this thread.
     */
    private Server server;
    /**
     * The hash String given to this socket by the {@link Server}.
     */
    private volatile String hash;
    /**
     * The {@link MessageQueue} used to handle messages sent on socket (if this
     * functionality is turned on).
     */
    private volatile MessageQueue messages;
    /**
     * int used to determine the state of the class. Valid states are: 0 =
     * Thread not started yet 1 = Thread started, connection not confirmed on
     * other end 2 = Thread started, connection confirmed on other end 3 =
     * Thread has finished.
     */
    private volatile int state;
    /**
     * boolean used to determine whether to use {@link MessageQueue} or not.
     */
    private volatile boolean use_message_queue;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(SocketThread.class.getName());

    /**
     * Takes the {@link Sock} instance of the socket to interface with, The
     * instance of {@link Server} that created this thread and the hash String
     * assigned to this thread by the {@link Server}.
     *
     * @param sock {@link Sock} used to hold the socket connection this
     * {@link SocketThread} interfaces with.
     * @param server {@link Server} that created this {@link SocketThread}.
     * Mainly used to handle/parse messages received into meaningful actions.
     * @param hash String representing the unique hash associated with this
     * {@link SocketThread} by the {@link Server} that created it.
     */
    public SocketThread(Sock sock, Server server, String hash) {
        // Initialise attributes
        socket = sock;
        this.server = server;
        this.hash = hash;
        state = 0;
        use_message_queue = false;
        messages = null;
    }

    /**
     * Takes the {@link Sock} instance of the socket to interface with, The
     * instance of {@link Server} that created this thread and the hash String
     * assigned to this thread by the {@link Server}. This constructor is used
     * to toggle advanced options on or off.
     *
     * @param sock {@link Sock} used to hold the socket connection this
     * {@link SocketThread} interfaces with.
     * @param server {@link Server} that created this {@link SocketThread}.
     * Mainly used to handle/parse messages received into meaningful actions.
     * @param hash String representing the unique hash associated with this
     * {@link SocketThread} by the {@link Server} that created it.
     * @param use_message_queue boolean flag used to determine whether to use a
     * {@link MessageQueue} or not.
     */
    public SocketThread(Sock sock, Server server, String hash, boolean use_message_queue) {
        // Initialise attributes
        socket = sock;
        this.server = server;
        this.hash = hash;
        state = 0;
        this.use_message_queue = use_message_queue;
        if (use_message_queue) {
            messages = new MessageQueue(this);
        } else {
            messages = null;
        }
    }

    /**
     * Loop that blocks while it reads new messages from the socket. If a
     * message is received it passes the message back to the {@link Server} to
     * handle.
     */
    @Override
    public void run() {
        if (use_message_queue) {
            messages.start();
            boolean started = false;
            Timing timer = new Timing();
            while (!started) {
                if (messages.getRun() < 1) {
                    timer.waitTime(5);
                    if (timer.getTime() > 5000) {
                        started = true;
                        LOGGER.log(Level.SEVERE, "MessageQueue was created but did not start in time");
                    }
                } else {
                    started = true;
                    LOGGER.log(Level.INFO, "Successfully started MessageQueue for SocketThread {0}", hash);
                }
            }
        }
        if (state == 0) {
            state = 1;
        }
        while (state == 1 || state == 2) {
            boolean read = false;
            String message = "";
            // Block and wait for input from the socket
            try {
                message = socket.readMessage();
                read = true;
            } catch (IOException e) {
                if (state == 1 || state == 2) {
                    if (server.getSocketList().containsKey(hash)) {
                        LOGGER.log(Level.SEVERE, "Could not read from socket. Hash {0}", hash);
                    }
                }
            }
            if (read) {
                if (message == null) {
                    LOGGER.log(Level.INFO, "Socket has been disconnected, attempting to close socket on Server. Hash {0}", hash);
                    message = "disconnect";
                    state = 4;
                } else {
                    LOGGER.log(Level.INFO, "Message received: {0}", message);
                }
                if (state > 0) {
                    server.receiveMessage(message, hash);
                }
            }
        }
        LOGGER.log(Level.INFO, "Socket loop has exited");
        try {
            close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close and interrupt SocketThread. Thread may not have terminated correctly and could be tieing up system resources. Exception:", e);
        }
    }

    /**
     * Closes the {@link SocketThread}.
     *
     * @throws IOException if an exception is encountered running unblock().
     */
    private void close() throws IOException {
        if (socket != null) {
            try {
                unblock();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close Sock socket in SocketThread");
                throw new IOException("Failed to close Sock socket in SocketThread");
            }
        } else {
            if (use_message_queue) {
                messages.close();
            }
            LOGGER.log(Level.INFO, "SocketThread has successfully closed");
        }
    }

    /**
     * Returns the attribute socket.
     *
     * @return the {@link Sock} socket.
     */
    public Sock getSocket() {
        return socket;
    }

    /**
     * Returns the attribute server.
     *
     * @return the {@link Server} server.
     */
    public Server getServer() {
        return server;
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
     * Returns the attribute use_message_queue.
     *
     * @return the boolean use_message_queue.
     */
    public boolean getUseMessageQueue() {
        return use_message_queue;
    }

    /**
     * Returns the attribute messages.
     *
     * @return the {@link MessageQueue} messages.
     */
    public MessageQueue getMessageQueue() {
        return messages;
    }

    /**
     * Sets the attribute hash. WARNING: You should avoid running this function
     * manually. Changing a {@link SocketThread}'s hash without moving it to a
     * new key corresponding to the new hash in {@link Server}.socket_list will
     * cause problems. If the {@link SocketThread} is stored in a
     * {@link Server}'s socket_list attribute, you should use the function
     * {@link Server}.replaceHash(String old_hash, String new_hash) which will
     * move the {@link SocketThread} to a key corresponding to the new hash and
     * also update the hash on the {@link SocketThread}.
     *
     * @param hash String to set hash to.
     */
    public void setHash(String hash) {
        this.hash = hash;
        LOGGER.log(Level.INFO, "Changed hash: {0}", hash);
    }

    /**
     * Sets the attribute state. Setting state not equal to 1 or 2 while the thread is
     * started will cause the thread to close.
     *
     * @param state int to set state to.
     */
    public void setRun(int state) {
        this.state = state;
    }

    /**
     * Toggles the option to use a {@link MessageQueue}. Passing true will
     * construct a new {@link MessageQueue} if one hasn't already been
     * constructed and passing false will close the {@link MessageQueue} if it
     * has been constructed already.
     *
     * @param use boolean to set use_message_queue to.
     */
    public void setUseMessageQueue(boolean use) {
        use_message_queue = use;
        if (use_message_queue && messages == null) {
            messages = new MessageQueue(this);
            messages.start();
        } else if (!use_message_queue && messages != null) {
            messages.close();
            messages = null;
        }
    }

    /**
     * Sets the attribute {@link MessageQueue} to the one that is passed to this
     * method. The {@link MessageQueue}.socket attribute of the passed
     * {@link MessageQueue} should be changed to the {@link SocketThread}
     * calling this method.
     *
     * @param messages the {@link MessageQueue} to change messages to.
     */
    public void setMessageQueue(MessageQueue messages) {
        this.messages = messages;
    }

    /**
     * This function is used to unblock the run() function if it is blocked
     * waiting for input from the socket. It will also cause the
     * {@link SocketThread} to exit its loop and terminate by setting it's state
     * to 4.
     *
     * @throws IOException if an exception is encountered when closing the {@link Sock}.
     */
    public void unblock() throws IOException {
        boolean running = false;
        if (state >= 1 && state <= 3) {
            running = true;
        }
        state = 4;
        if (socket != null) {
            socket.close();
            socket = null;
            LOGGER.log(Level.INFO, "Successfully closed Sock socket");
        }
        if (!running) {
            close();
        }
    }

    /**
     * Sends a message through the {@link Sock}. If use_message_queue is true
     * and messages != null the {@link SocketThread} will queue the message to
     * it's {@link MessageQueue} to handle.
     *
     * @param message the String message to send through the {@link Sock}.
     */
    public void sendMessage(String message) {
        if (use_message_queue && messages != null) {
            messages.queueMessage(message);
        } else {
            try {
                socket.sendMessage(message);
                LOGGER.log(Level.INFO, "Sent message {0} through socket with hash {1}", new Object[]{message, hash});
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not send message through socket with hash: {0}\nMessage was: '{1}'\nSocket data:\n{2}", new Object[]{hash, message, toString()});
                LOGGER.log(Level.INFO, "Caught exception: {0}", e);
                server.disconnect(hash);
            }
        }
    }

    /**
     * Puts the attribute states of {@link SocketThread} in readable form.
     *
     * @return Attributes of {@link SocketThread} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = toString("");
        return to_string;
    }

    /**
     * Puts the attribute states of {@link SocketThread} in readable form. Takes
     * String input to assist formatting. Useful to add special characters to
     * assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link SocketThread} in a readable String form.
     */
    public String toString(String ch) {
        String to_string = ch + "Hash: " + hash + "\n" + ch + "State: " + state + "\n" + ch + "Use Message Queue: " + use_message_queue;
        to_string += "\n" + ch + "Sock:";
        if (socket != null && socket.getSocket() != null) {
            to_string += "\n" + socket.toString(ch + "\t");
        } else {
            to_string += " Sock has been closed";
        }
        if (use_message_queue && messages != null) {
            to_string += "\n" + ch + "MessageQueue details: " + "\n" + messages.toString(ch + "\t");
        }
        return to_string;
    }
}
