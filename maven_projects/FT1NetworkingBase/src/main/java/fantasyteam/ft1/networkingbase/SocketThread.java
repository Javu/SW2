package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Timing;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
     * Default timeout value to wait when waiting for critical tasks to
     * complete.
     */
    private volatile long timeout;
    /**
     * The time in milliseconds to wait while blocking for input on the socket
     * until a SocketTimeoutException is thrown to break blocking.
     */
    private int socket_timeout_response;
    /**
     * The number of consecutive times to handle a SocketTimeoutException before
     * closing the {@link SocketThread}.
     */
    private int socket_timeout_response_count;
    /**
     * The counter that is incremented every time no response is received and the
     * socket throws a SocketTimeoutException.
     */
    private int no_response_count;
    /**
     * Boolean specifying whether to use the no response timeout feature. While
     * blocking for input from the socket, if this feature is active the socket
     * will throw a SocketTimeoutException if it does not receive a response
     * within the timeout given by socket_timeout_response. It will also
     * increment a counter by 1 each time it receives a SocketTimeoutException.
     * If this counter becomes > socket_timeout_response_count this
     * {@link SocketThread} will close itself. If it successfully reads a
     * message from the socket it will set the counter back to 0.
     */
    private volatile boolean use_socket_timeout;

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
        timeout = 5000;
        socket_timeout_response = 1000;
        socket_timeout_response_count = 5;
        use_socket_timeout = false;
        no_response_count = 0;
    }

    /**
     * Loop that blocks while it reads new messages from the socket. If a
     * message is received it passes the message back to the {@link Server} to
     * handle.
     */
    @Override
    public void run() {
        Timing confirmation_timer = null;
        if (state == NEW) {
            state = RUNNING;
        }
        while (state == RUNNING || state == CONFIRMED) {
            if (state == RUNNING && confirmation_timer == null) {
                confirmation_timer = new Timing();
            } else if (state == CONFIRMED && confirmation_timer != null) {
                confirmation_timer = null;
            }
            if (state == RUNNING && confirmation_timer != null && confirmation_timer.getTime() > timeout) {
                LOGGER.log(Level.INFO, "Remote server did not verify connection before timeout was reached. Closing SocketThread. Hash {0}", hash);
                server.receiveMessage("disconnect", hash);
            } else if (use_socket_timeout && no_response_count > socket_timeout_response_count) {
                LOGGER.log(Level.INFO, "Have not received a response from the remote server within the given timeout. Closing SocketThread. Hash {0}", hash);
                server.receiveMessage("disconnect", hash);
            } else {
                boolean read = false;
                String message = "";
                try {
                    message = socket.readMessage();
                    read = true;
                    if (use_socket_timeout) {
                        no_response_count = 0;
                    }
                } catch (SocketTimeoutException e) {
                    if (socket_timeout_response_count != -1) {
                        no_response_count += 1;
                    }
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
                    if (state == RUNNING) {
                        if (message.compareTo("customnetwork1" + Character.toString((char) 31)) == 0) {
                            server.receiveMessage(message, hash);
                        }
                    } else if (state != NEW) {
                        server.receiveMessage(message, hash);
                    }
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
     * Returns the attribute timeout, the amount of time (in milliseconds) to
     * wait for critical tasks to complete.
     *
     * @return the long timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Returns the attribute socket_timeout_response, the amount of time to wait
     * (in milliseconds) for a response from the socket before breaking
     * blocking.
     *
     * @return the int socket_timeout_response.
     */
    public int getSocketTimeout() {
        return socket_timeout_response;
    }

    /**
     * Returns the attribute socket_timeout_response_count, the amount of
     * consecutive times to break blocking for no response until the
     * {@link SocketThread} closes itself.
     *
     * @return the int socket_timeout_no_response_count.
     */
    public int getSocketTimeoutCount() {
        return socket_timeout_response_count;
    }

    /**
     * Returns the attribute use_socket_timeout, a flag specifying whether to
     * use the socket timeout feature to break blocking on the socket if no
     * response is received within the given timeout. You can set this timeout
     * value by using setSocketTimout and can specify the number of consecutive
     * timeouts to accept before closing the {@link SocketThread} altogether by
     * using setSocketTimeoutCount.
     *
     * @return the boolean to set use_socket_timeout to.
     */
    public boolean getUseSocketTimeout() {
        return use_socket_timeout;
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
     * Sets the attribute state, the current state of the {@link SocketThread}.
     * Valid states are 0 - NEW 1 - RUNNING 2 - CONFIRMED 3 - ERROR 4 - CLOSED.
     * Setting state not equal to RUNNING or CONFIRMED while the thread is
     * started will cause the thread to close.
     *
     * @param state int to set state to.
     * @throws InvalidArgumentException if parameter state != NEW, RUNNING,
     * CONFIRMED, ERROR or CLOSED.
     */
    public synchronized void setRun(int state) throws InvalidArgumentException {
        if (state == NEW || state == RUNNING || state == CONFIRMED || state == ERROR || state == CLOSED) {
            this.state = state;
        } else {
            throw new InvalidArgumentException("State must equal NEW, RUNNING, CONFIRMED, ERROR or CLOSED. State equals " + state);
        }
    }

    /**
     * Set the value of timeout, the amount of time to wait (in milliseconds)
     * for critical tasks to complete.
     *
     * @param timeout long to set timeout to.
     * @throws InvalidArgumentException if the parameter timeout is less than 0.
     */
    public void setTimeout(long timeout) throws InvalidArgumentException {
        if (timeout >= 0) {
            this.timeout = timeout;
        } else {
            throw new InvalidArgumentException("Value of timeout must be >= 0. Timeout = " + timeout);
        }
    }

    /**
     * Sets the attribute socket_timeout_response, the amount of time to wait
     * before throwing a SocketTimeoutException if no response is received on
     * the socket. The timeout will only be applied if this feature is turned
     * on. You can turn the feature on using setUseSocketTimeout. You can also
     * set how many consecutive timeouts to accept before closing the
     * {@link SocketThread} using setSocketTimeoutCount.
     *
     * @param socket_timeout int to set socket_timeout_response to.
     * @throws InvalidArgumentException if the parameter socket_timeout less
     * than or equal to 0.
     * @throws SocketException if there is a problem setting the socket timeout
     * value.
     */
    public void setSocketTimeout(int socket_timeout) throws InvalidArgumentException, SocketException {
        if (socket_timeout > 0) {
            socket_timeout_response = socket_timeout;
            if (use_socket_timeout) {
                socket.getSocket().setSoTimeout(socket_timeout_response);
            }
        } else {
            throw new InvalidArgumentException("Value of socket_timeout must be > 0. socket_timeout = " + socket_timeout);
        }
    }

    /**
     * Sets the attribute socket_timeout_response_count, the maximum number of
     * consecutive SocketTimeoutExceptions to handle before closing the
     * {@link SocketThread}. Set this value to -1 to tell the
     * {@link SocketThread} to never close no matter how many consecutive
     * SocketTimeoutExceptions it receives. This will only work if the socket
     * timeout feature is used. You can turn on the feature using
     * setUseSocketTimeout. You can also set the amount of time (in
     * milliseconds) to wait while blocking on the socket to throw a
     * SocketTimeoutException.
     *
     * @param count int to change socket_timeout_response_count to.
     * @throws InvalidArgumentException if the parameter count is less than 0.
     */
    public void setSocketTimeoutCount(int count) throws InvalidArgumentException {
        if (count >= -1) {
            socket_timeout_response_count = count;
        } else {
            throw new InvalidArgumentException("Value of count must be >= -1. count = " + count);
        }
    }

    /**
     * Sets the attribute use_socket_timeout, this turns the socket timeout
     * feature on or off. This feature is used to cause the socket to break
     * blocking if a specified timeout is reached before receiving any input.
     * Use setSocketTimeout to set the wait time (in milliseconds) to wait
     * before breaking input blocking (the default value is 1000). Use
     * setSocketTimeoutCount to specify the number of consecutive times to break
     * blocking before closing the {@link SocketThread} (the default value is
     * 5). if this feature is turned on while the socket is blocking for input
     * then it will only become active the next time the socket begins to block
     * for input.
     *
     * @param use_socket_timeout boolean to set use_socket_timeout to.
     * @throws SocketException if an exception is found when trying to change
     * the value of timeout on the socket.
     */
    public void setUseSocketTimeout(boolean use_socket_timeout) throws SocketException {
        if (this.use_socket_timeout && !use_socket_timeout) {
            socket.getSocket().setSoTimeout(0);
        } else if (!this.use_socket_timeout && use_socket_timeout) {
            socket.getSocket().setSoTimeout(getSocketTimeout());
        }
        this.use_socket_timeout = use_socket_timeout;
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
        String to_string = ch + "Hash: " + hash + "\n" + ch + "State: " + state + "\n" + ch + "Use Socket Timeout: " + use_socket_timeout;
        if (use_socket_timeout) {
            to_string += "\n" + ch + "Socket Timeout: " + socket_timeout_response + "\n" + ch + "Maximum Cumulative Timeouts: " + socket_timeout_response_count + "\n" + ch + "Total Cumulative Timeouts: " + no_response_count;
        }
        to_string += "\n" + ch + "Sock:";
        if (socket != null && socket.getSocket() != null) {
            to_string += "\n" + socket.toString(ch + "\t");
        } else {
            to_string += " Sock has been closed";
        }
        return to_string;
    }
}
