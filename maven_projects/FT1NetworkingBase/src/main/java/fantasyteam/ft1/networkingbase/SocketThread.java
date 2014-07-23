package fantasyteam.ft1.networkingbase;

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
     * Valid state for {@link SocketThread}, used when the thread has just been
     * constructed but has not started run() yet.
     */
    public static final int NEW = 0;

    /**
     * Valid state for {@link SocketThread}, used when the thread is meant to be
     * running normally but has not received an acknowledgment from remote the
     * {@link Server} that has finished creating the connection on it's end.
     */
    public static final int RUNNING = 1;

    /**
     * Valid state for {@link SocketThread}, used when the thread is meant to be
     * running normally and has received an acknowledgment from remote the
     * {@link Server} that has finished creating the connection on it's end.
     */
    public static final int CONFIRMED = 2;

    /**
     * Valid state for {@link SocketThread}, used when there is an error reading
     * messages through the socket.
     */
    public static final int ERROR = 3;

    /**
     * Valid state for {@link SocketThread}, used when it is flagged to be
     * closed.
     */
    public static final int CLOSED = 4;

    /**
     * {@link Sock} used to hold Socket connection and interface with it.
     */
    private volatile Sock socket;
    /**
     * Instance of the {@link Server} class that created this thread.
     */
    private final Server server;
    /**
     * The hash String given to this socket by the {@link Server}.
     */
    private volatile String hash;
    /**
     * int used to determine the state of the class. Valid states are: 0 - NEW 1
     * - RUNNING 2 - CONFIRMED 3 - ERROR 4 - CLOSED
     */
    private volatile int state;

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
        state = NEW;
    }

    /**
     * Loop that blocks while it reads new messages from the socket. If a
     * message is received it passes the message back to the {@link Server} to
     * handle.
     */
    @Override
    public void run() {
        if (state == NEW) {
            state = RUNNING;
        }
        while (state == RUNNING || state == CONFIRMED) {
            boolean read = false;
            String message = "";
            // Block and wait for input from the socket
            try {
                message = socket.readMessage();
                read = true;
            } catch (IOException e) {
                if (state == RUNNING || state == CONFIRMED) {
                    if (server.getSocketList().containsKey(hash)) {
                        LOGGER.log(Level.SEVERE, "Could not read from socket. Hash {0}", hash);
                    }
                }
            }
            if (read) {
                if (message == null) {
                    LOGGER.log(Level.INFO, "Socket has been disconnected, attempting to close socket on Server. Hash {0}", hash);
                    message = "disconnect";
                } else {
                    LOGGER.log(Level.INFO, "Message received: {0}", message);
                }
                if (state != NEW) {
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
    private synchronized void close() throws IOException {
        if (socket != null) {
            try {
                unblock();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close Sock in SocketThread {0}", hash);
                throw new IOException("Failed to close Sock in SocketThread " + hash);
            }
        } else {
            state = CLOSED;
            LOGGER.log(Level.INFO, "SocketThread {0} has successfully closed", hash);
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
     * Sets the attribute hash, the unique identifier assigned to this
     * {@link SocketThread} by the {@link Server}. WARNING: You should avoid
     * running this function manually. Changing a {@link SocketThread}'s hash
     * without moving it to a new key corresponding to the new hash in
     * {@link Server}.socket_list will cause problems. If the
     * {@link SocketThread} is stored in a {@link Server}'s socket_list
     * attribute, you should use the function {@link Server}.replaceHash(String
     * old_hash, String new_hash) which will move the {@link SocketThread} to a
     * key corresponding to the new hash and also update the hash on the
     * {@link SocketThread}.
     *
     * @param hash String to set hash to.
     */
    public synchronized void setHash(String hash) {
        this.hash = hash;
        LOGGER.log(Level.INFO, "Changed hash: {0}", hash);
    }

    /**
     * Sets the attribute state, the current state of the {@link SocketThread}. Setting state not equal to 1 or 2 while the
     * thread is started will cause the thread to close.
     *
     * @param state int to set state to.
     */
    public synchronized void setRun(int state) {
        this.state = state;
    }

    /**
     * This function is used to unblock the run() function if it is blocked
     * waiting for input from the socket. It will also cause the
     * {@link SocketThread} to exit its loop and terminate by setting it's state
     * to 4.
     *
     * @throws IOException if an exception is encountered when closing the
     * {@link Sock}.
     */
    public synchronized void unblock() throws IOException {
        boolean running = false;
        if (state == RUNNING || state == CONFIRMED || state == ERROR) {
            running = true;
        }
        state = CLOSED;
        if (socket != null) {
            socket.close();
            socket = null;
            LOGGER.log(Level.INFO, "Successfully closed Sock");
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
        try {
            socket.sendMessage(message);
            LOGGER.log(Level.INFO, "Sent message {0} through socket with hash {1}", new Object[]{message, hash});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not send message through socket with hash: {0}\nMessage was: '{1}'\nSocket data:\n{2}", new Object[]{hash, message, toString()});
            LOGGER.log(Level.INFO, "Caught exception: {0}", e);
            server.disconnect(hash);
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
        String to_string = ch + "Hash: " + hash + "\n" + ch + "State: " + state;
        to_string += "\n" + ch + "Sock:";
        if (socket != null && socket.getSocket() != null) {
            to_string += "\n" + socket.toString(ch + "\t");
        } else {
            to_string += " Sock has been closed";
        }
        return to_string;
    }
}
