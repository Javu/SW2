package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
import fantasyteam.ft1.networkingbase.exceptions.FeatureNotUsedException;
import fantasyteam.ft1.networkingbase.exceptions.HashNotFoundException;
import fantasyteam.ft1.networkingbase.exceptions.InvalidArgumentException;
import fantasyteam.ft1.exceptions.FT1EngineError;
import fantasyteam.ft1.networkingbase.exceptions.NullException;
import fantasyteam.ft1.networkingbase.exceptions.ServerSocketCloseException;
import fantasyteam.ft1.networkingbase.exceptions.TimeoutException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link Server} class is used to create client and server modules for a
 * client/server framework. It contains all the methods needed to create and
 * accept multiple connections using IP addresses and port numbers. Any port
 * based connections will need to be port forwarded on the end of the acceptor.
 * When creating a client/server framework this class will need to be extended
 * and the handleMessage method will need to be overridden. This method is meant
 * to be used to process and correctly handle any string inputs received through
 * a connection.
 *
 * @author javu
 */
public class Server extends fantasyteam.ft1.Networking {

    /**
     * Valid state for {@link Server}, used when there is an error starting the
     * {@link Server}.
     */
    public static final int ERROR = -1;

    /**
     * Valid state for {@link Server}, used when it is set to use
     * {@link ListenThread} to listen on a port for new connections.
     */
    public static final int LISTEN = 0;

    /**
     * Valid state for {@link Server}, used when it is not set to use
     * {@link ListenThread} to listen on a port for new connections.
     */
    public static final int CLIENT = 1;

    /**
     * Valid state for {@link Server}, used when it has closed.
     */
    public static final int CLOSED = 2;

    /**
     * Current state of the server. Valid states are: -1 - ERROR 0 - LISTEN 1 -
     * CLIENT 2 - CLOSED
     */
    protected volatile int state;
    /**
     * Port number.
     */
    protected volatile int port;
    /**
     * Default timeout value to wait when waiting for critical tasks to
     * complete.
     */
    protected long timeout;
    /**
     * Default timeout value to use for the {@link SocketThread} socket timeout
     * feature. This will only be used if the feature is turned on. Turn the
     * feature on using setUseSocketTimeout.
     */
    protected int socket_timeout;
    /**
     * Default number of consecutive times each {@link SocketThread} should
     * receive a timeout on the socket before closing itself. This will only be
     * used if the {@link SocketThread} socket timeout feature is turned on.
     * Turn this feature on using setUseSocketTimeout.
     */
    protected int socket_timeout_count;
    /**
     * Boolean used to specify whether to keep the hashes of disconnected
     * sockets.
     */
    protected volatile boolean use_disconnected_sockets;
    /**
     * Boolean used to specify whether to construct {@link SocketThread}s with
     * {@link MessageQueue}s.
     */
    protected volatile boolean use_message_queues;
    /**
     * Boolean used to specify whether to use the connection confirmation
     * feature.
     */
    protected volatile boolean use_connection_confirmation;
    /**
     * Boolean used to specify whether to use the socket timeout feature.
     */
    protected volatile boolean use_socket_timeout;
    /**
     * Map used to hold {@link SocketThread}s and the keys to associate them
     * with.
     */
    protected volatile Map<String, SocketThread> socket_list;
    /**
     * Map used to hold {@link MessageQueue}s and the keys to associate them
     * with.
     */
    protected volatile Map<String, MessageQueue> queue_list;
    /**
     * ArrayList used to keep the hashes of disconnected sockets.
     */
    protected volatile ArrayList<String> disconnected_sockets;
    /**
     * The threaded class used to hold a ServerSocket based connection and
     * interface with it. Only used if true is passed through the constructor.
     */
    protected volatile ListenThread listen_thread;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Takes an instance of Game as a parameter.
     *
     * @param game An instance of the Game class utilising this Server Object.
     */
    public Server(Game game) {
        super(game);
        port = 0;
        timeout = 5000;
        socket_timeout = 1000;
        socket_timeout_count = 5;
        use_disconnected_sockets = false;
        use_message_queues = false;
        use_connection_confirmation = false;
        use_socket_timeout = false;
        socket_list = Collections.synchronizedMap(new HashMap<String, SocketThread>());
        queue_list = Collections.synchronizedMap(new HashMap<String, MessageQueue>());
        disconnected_sockets = new ArrayList<String>();
        listen_thread = null;
        state = CLIENT;
    }

    /**
     * Takes an instance of Game and an Integer for the port number.
     *
     * @param game An instance of the Game class utilising this Server Object.
     * @param port The port number used to connect to a server or listen for
     * clients on.
     * @param listen Boolean flag to set whether the Server will act as a server
     * or client.
     * @throws IOException if an exception is found when running
     * setListenThread().
     * @throws ServerSocketCloseException if the ServerSocket on
     * {@link ListenThread} fails to close when attempting to create a new
     * {@link ListenThread} when one is already running.
     * @throws TimeoutException if the ServerSocket on {@link ListenThread}
     * fails to close before timeout is reached when attempting to create a new
     * {@link ListenThread} when one is already running.
     */
    public Server(Game game, int port, boolean listen) throws IOException, ServerSocketCloseException, TimeoutException {
        super(game);
        this.port = port;
        timeout = 5000;
        socket_timeout = 1000;
        socket_timeout_count = 5;
        use_disconnected_sockets = false;
        use_message_queues = false;
        use_connection_confirmation = false;
        use_socket_timeout = false;
        socket_list = Collections.synchronizedMap(new HashMap<String, SocketThread>());
        queue_list = Collections.synchronizedMap(new HashMap<String, MessageQueue>());
        disconnected_sockets = new ArrayList<String>();
        if (listen) {
            try {
                setListenThread();
            } catch (IOException e) {
                throw new IOException("Failed to start ListenThread on server. Server state has been set as errored");
            } catch (TimeoutException e) {
                throw new TimeoutException("Failed to close already running ListenThread before timeout when attempting to create a new ListenThread. Server state has been set as errored");
            }
        } else {
            listen_thread = null;
            state = CLIENT;
        }
    }

    /**
     * Closes the {@link Server}.
     *
     * @throws IOException if an exception is encountered when running
     * closeListenThread().
     * @throws ServerSocketCloseException if the ServerSocket on ListenThread
     * fails to close.
     * @throws TimeoutException if the ServerSocket on {@link ListenThread}
     * fails to close before timeout is reached.
     */
    public synchronized void close() throws IOException, ServerSocketCloseException, TimeoutException {
        use_disconnected_sockets = false;
        closeListenThread();
        if (socket_list != null) {
            if (!socket_list.isEmpty()) {
                Set<String> socket_set = socket_list.keySet();
                Object[] socket_array = socket_set.toArray();
                for (Object socket_hash : socket_array) {
                    disconnect(String.valueOf(socket_hash));
                }
            }
            boolean not_disconnected = true;
            Timing timer = new Timing();
            while (not_disconnected) {
                if (socket_list.isEmpty() || timer.getTime() > timeout) {
                    not_disconnected = false;
                }
            }
        }
        if (queue_list != null) {
            if (!queue_list.isEmpty()) {
                Set<String> queue_set = queue_list.keySet();
                Object[] queue_array = queue_set.toArray();
                for (Object queue_hash : queue_array) {
                    try {
                        removeQueue(String.valueOf(queue_hash));
                    } catch (HashNotFoundException | NullException e) {
                        LOGGER.log(Level.INFO, e.getMessage());
                    }
                }
            }
            boolean not_disconnected = true;
            Timing timer = new Timing();
            while (not_disconnected) {
                if (queue_list.isEmpty() || timer.getTime() > timeout) {
                    not_disconnected = false;
                }
            }
        }
        socket_list = null;
        queue_list = null;
        disconnected_sockets = null;
        state = CLOSED;
        LOGGER.log(Level.INFO, "Successfully closed Server");
    }

    /**
     * Safely closes the {@link ListenThread} is the {@link Server} is a listen
     * server.
     *
     * @throws IOException if an exception is encountered when closing the
     * {@link ListenThread}.
     * @throws ServerSocketCloseException if the ServerSocket on
     * {@link ListenThread} fails to close.
     * @throws TimeoutException if (@link ListenThread) fails to close in time.
     */
    public synchronized void closeListenThread() throws IOException, ServerSocketCloseException, TimeoutException {
        if (state == LISTEN && listen_thread != null) {
            if (listen_thread.getRun()) {
                LOGGER.log(Level.INFO, "Attempting to close running listen_thread on port {0}", listen_thread.getPort());
                listen_thread.close();
                boolean running = true;
                Timing new_timer = new Timing();
                while (running) {
                    if (!listen_thread.getRun()) {
                        running = false;
                    } else if (new_timer.getTime() > timeout) {
                        throw new TimeoutException("Failed to close ListenThread in time");
                    }
                }
                LOGGER.log(Level.INFO, "Successfully closed running listen_thread");
            } else {
                LOGGER.log(Level.INFO, "Attempting to close non-running listen_thread on port {0}", listen_thread.getPort());
                listen_thread.close();
                LOGGER.log(Level.INFO, "Successfully closed non-running listen_thread");
            }
        }
    }

    /**
     * Sets the port number used for connections.
     *
     * @param port Port number to listen for connections on.
     * @throws IllegalArgumentException if the port parameter is not in the
     * range of valid port numbers. Must be between 0 and 65535 inclusive.
     */
    public synchronized void setPort(int port) throws IllegalArgumentException {
        if (port >= 0 && port <= 65535) {
            this.port = port;
            LOGGER.log(Level.INFO, "Changing port number: {0}", port);
        } else {
            throw new IllegalArgumentException("Port number " + port + "out of range. Must be between 0 and 65535 inclusive");
        }
    }

    /**
     * Set the timeout attribute, the default maximum time to wait when waiting
     * for a critical task to complete. Timeout is in milliseconds.
     *
     * @param timeout Number of milliseconds to wait for critical tasks to
     * complete.
     * @throws InvalidArgumentException if the parameter timeout is less than 0.
     */
    public synchronized void setTimeout(long timeout) throws InvalidArgumentException {
        if (timeout >= 0) {
            this.timeout = timeout;
        } else {
            throw new InvalidArgumentException("Value of timeout must be >= 0");
        }
    }

    /**
     * Sets the attribute socket_timeout, the amount of time (in milliseconds)
     * for each SocketThread to wait before breaking any blocking on its socket.
     * The default value for this is 1000. This function will only be used if
     * the {@link SocketThread} socket timeout feature is turned on. Turn the
     * feature on using setUseSocketTimeout.
     *
     * @param timeout Time in milliseconds to wait until breaking any blocking
     * on {@link SocketThread}s sockets.
     * @throws SocketException if there is a problem setting the timeout value
     * on the {@link SocketThread}s socket.
     * @throws InvalidArgumentException if the parameter timeout less than or
     * equal to 0.
     */
    public synchronized void setSocketTimeout(int timeout) throws SocketException, InvalidArgumentException {
        if (timeout > 0) {
            socket_timeout = timeout;
            if (use_socket_timeout && socket_list != null && !socket_list.isEmpty()) {
                for (SocketThread socket : socket_list.values()) {
                    try {
                        socket.setSocketTimeout(socket_timeout);
                    } catch (InvalidArgumentException e) {
                        throw new FT1EngineError("internal engine error: Caught InvalidArgumentException when running SocketThread.setSocketTimeout() from Server.setSocketTimeout()", e);
                    }
                }
            }
        } else {
            throw new InvalidArgumentException("Value timeout must be > 0. timeout = " + timeout);
        }
    }

    /**
     * Sets the attribute socket_timeout_count, the maximum consecutive times a
     * {@link SocketThread} will throw a SocketTimeoutException before closing
     * itself. The default value for this is 5. This function will only be used
     * if the {@link SocketThread} socket timeout feature is turned on. Turn the
     * feature on using setUseSocketTimeout.
     *
     * @param count Number of maximum consecutive times to throw a
     * SocketTimeoutException before closing the {@link SocketThread}.
     * @throws SocketException if there is a problem setting the timeout value
     * on the {@link SocketThread}s socket.
     * @throws InvalidArgumentException if the parameter count less than -1.
     */
    public synchronized void setSocketTimeoutCount(int count) throws SocketException, InvalidArgumentException {
        if (count >= -1) {
            socket_timeout_count = count;
            if (use_socket_timeout && socket_list != null && !socket_list.isEmpty()) {
                for (SocketThread socket : socket_list.values()) {
                    try {
                        socket.setSocketTimeoutCount(socket_timeout_count);
                    } catch (InvalidArgumentException e) {
                        throw new FT1EngineError("internal engine error: Caught InvalidArgumentException when running SocketThread.setSocketTimeoutCount() from Server.setSocketTimeoutCount()", e);
                    }
                }
            }
        } else {
            throw new InvalidArgumentException("Value count must be >= -1. count = " + count);
        }
    }

    /**
     * Sets whether to keep the hashes of disconnected sockets. Please note: If
     * you wish to also timeout disconnections after a period of time so that
     * the client cannot reconnect after the timeout, you need to use the
     * use_message_queues feature as well. If you just use
     * use_disconnected_sockets the server will hold the hashes of disconnected
     * {@link SocketThread}s indefinitely allowing them to reconnect.
     *
     * @param use Boolean specifying whether to keep the hashes of disconnected
     * sockets.
     */
    public synchronized void setUseDisconnectedSockets(boolean use) {
        use_disconnected_sockets = use;
        LOGGER.log(Level.INFO, "Toggling flag use_disconnected_sockets: {0}", use);
    }

    /**
     * Sets whether to construct {@link SocketThread}s with
     * {@link MessageQueue}s.
     *
     * @param use Boolean specifying whether to construct new
     * {@link SocketThread}s with {@link MessageQueue}s.
     * @throws TimeoutException if a {@link MessageQueue} does not start before
     * timeout is reached.
     */
    public synchronized void setUseMessageQueues(boolean use) throws TimeoutException {
        LOGGER.log(Level.INFO, "Toggling flag use_message_queues: {0}", use);
        if (use && !use_message_queues) {
            if (socket_list != null && !socket_list.isEmpty()) {
                for (SocketThread socket : socket_list.values()) {
                    queue_list.put(socket.getHash(), new MessageQueue(this, socket.getHash()));
                    try {
                        startQueue(socket.getHash());
                    } catch (TimeoutException e) {
                        throw new TimeoutException("MessageQueue for SocketThread " + socket.getHash() + " failed to start in time");
                    } catch (HashNotFoundException | NullException e) {
                        throw new FT1EngineError("An internal engine error occurred when running Server.startQueue from Server.setUseMessageQueues");
                    }
                }
            }
        } else if (!use && use_message_queues) {
            if (queue_list != null && !queue_list.isEmpty()) {
                for (MessageQueue queue : queue_list.values()) {
                    try {
                        removeQueue(queue.getHash());
                    } catch (HashNotFoundException | NullException e) {
                        throw new FT1EngineError("An internal engine error occurred when running Server.removeQueue from Server.setUseMessageQueues");
                    }
                }
            }
        }
        use_message_queues = use;
    }

    /**
     * <p>Sets the attribute use_connection_confirmation, the flag specifying
     * whether to use the connection confirmation feature. This feature allows a
     * client to ensure that a remote server has successfully created a
     * corresponding socket and is ready to begin interacting with the client.
     * When the remote server receives a new connection it will send a
     * notification message to the client when it is ready to start interacting.
     * The client will ignore all messages until it receives this notification.
     * If it does not receive the notification within a timeout period it will
     * close. You can set this timeout by using setTimeout.</p><p>Please note: this
     * feature needs to be turned on on both the client and the server for it to
     * work. The server will not send the notification if the feature is not
     * turned on and the client will continue as normal without checking for the
     * notification.</p>
     *
     * @param use Boolean specifying whether to use the connection confirmation
     * feature.
     */
    public synchronized void setUseConnectionConfirmation(boolean use) {
        use_connection_confirmation = use;
    }

    /**
     * Sets the attribute use_socket_timeout, the flag specifying whether to use
     * the socket timeout feature of {@link SocketThread}. This feature allows
     * the {@link SocketThread} to break blocking on its socket if no response
     * is received after a given timeout. This allows it to handle other
     * features, increment any counters and check any timers it needs to. The
     * {@link SocketThread} will also close itself if it breaks blocking a
     * number of consecutive times. Specify this number by using
     * setSocketTimeoutCount (default value is 5). You can specify the timeout
     * value to use by running setSocketTimeout (default value is 1000).
     *
     * @param use Boolean specifying whether to turn the socket timeout feature
     * on or off.
     * @throws SocketException if the Server attempts to set the timeout value
     * on each {@link SocketThread}s socket but catches an error.
     */
    public synchronized void setUseSocketTimeout(boolean use) throws SocketException {
        if (socket_list != null && !socket_list.isEmpty()) {
            if (use_socket_timeout && !use) {
                for (SocketThread socket : socket_list.values()) {
                    socket.setUseSocketTimeout(false);
                }
            } else if (!use_socket_timeout && use) {
                for (SocketThread socket : socket_list.values()) {
                    socket.setUseSocketTimeout(true);
                }
            }
        }
        use_socket_timeout = use;
    }

    /**
     * Sets the list of connections to the {@link Server} using a
     * pre-constructed Map(String,{@link SocketThread}).
     *
     * @param socket_list Map(String,{@link SocketThread}) to use as list of
     * {@link Server} connections.
     */
    public synchronized void setSocketList(Map<String, SocketThread> socket_list) {
        this.socket_list = socket_list;
        LOGGER.log(Level.INFO, "Changing socket_list. New socket_list:\n{0}", socket_list.toString());
    }

    /**
     * Sets the list of {@link MessageQueue}s associated with each
     * {@link SocketThread} to the {@link Server} using a pre-constructed
     * Map(String,{@link MessageQueue}).
     *
     * @param queue_list Map(String,{@link MessageQueue}) to use as a list of
     * {@link MessageQueue}s on the {@link Server}.
     */
    public synchronized void setQueueList(Map<String, MessageQueue> queue_list) {
        this.queue_list = queue_list;
        if (this.queue_list != null) {
            LOGGER.log(Level.INFO, "Changing queue_list. New queue_list:\n{0}", queue_list.toString());
        } else {
            LOGGER.log(Level.INFO, "Changing queue_list. New queue_list: null");
        }
    }

    /**
     * Sets the list of disconnected sockets hashes.
     *
     * @param disconnected_sockets ArrayList{String) to use as list of hashes of
     * disconnected sockets.
     */
    public synchronized void setDisconnectedSockets(ArrayList<String> disconnected_sockets) {
        this.disconnected_sockets = disconnected_sockets;
        LOGGER.log(Level.INFO, "Changing disconnected_sockets: {0}", disconnected_sockets);
    }

    /**
     * Sets the attribute listen_thread.
     *
     * @throws IOException if an exception is found when creating a new
     * {@link ListenThread} or when closing an existing one before attempting to
     * create the new one.
     * @throws ServerSocketCloseException if the ServerSocket on ListenThread
     * fails to close when attempting to create a new {@link ListenThread} when
     * one is already running.
     * @throws TimeoutException if {@link ListenThread} fails to close before
     * reaching timeout when attempting to close the currently running
     * {@link ListenThread} before creating the new one.
     */
    public synchronized void setListenThread() throws IOException, ServerSocketCloseException, TimeoutException {
        closeListenThread();
        try {
            listen_thread = new ListenThread(this);
            state = LISTEN;
        } catch (IOException e) {
            state = ERROR;
            throw new IOException("Could not create ListenThread on port" + port + ". Exception received: " + e);
        }
    }

    /**
     * Returns the port number used for connections.
     *
     * @return the port number used to listen for connections.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the maximum wait time (in milliseconds) to wait for critical
     * tasks to complete.
     *
     * @return the maximum wait time (in ms) to wait for critical tasks.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Returns whether to keep the hashes of disconnected sockets or not.
     *
     * @return boolean specifying whether to keep the hashes of disconnected
     * sockets.
     */
    public boolean getUseDisconnectedSockets() {
        return use_disconnected_sockets;
    }

    /**
     * Returns whether to construct new {@link SocketThread}s with
     * {@link MessageQueue}s.
     *
     * @return boolean specifying whether to construct new {@link SocketThread}s
     * with {@link MessageQueue}s.
     */
    public boolean getUseMessageQueues() {
        return use_message_queues;
    }

    /**
     * Returns the list of hashes of disconnected sockets.
     *
     * @return the ArrayList(String) of hashes of disconnected sockets.
     */
    public ArrayList<String> getDisconnectedSockets() {
        return disconnected_sockets;
    }

    /**
     * Returns the list of connections to the {@link Server}.
     *
     * @return the Map(String,{@link SocketThread}) socket_list, the list of
     * connections to the {@link Server}.
     */
    public Map<String, SocketThread> getSocketList() {
        return socket_list;
    }

    /**
     * Returns the list of {@link MessageQueue}s associated with each
     * {@link SocketThread} on the {@link Server}.
     *
     * @return the Map(String,{@link MessageQueue}) queue_list, the list of
     * {@link MessageQueue}s on the {@link Server}.
     */
    public Map<String, MessageQueue> getQueueList() {
        return queue_list;
    }

    /**
     * Returns the {@link ListenThread} listen_thread. This thread is used to
     * accept new connections to the server. This attribute is only used if true
     * is passed into the constructor for the {@link Server}.
     *
     * @return the {@link ListenThread} listen_thread. This will return null if
     * false was passed into the constructor, meaning the {@link Server} is not
     * used for listening.
     */
    public ListenThread getListenThread() {
        return listen_thread;
    }

    /**
     * Returns the int representing the current state of the {@link Server}.
     * Valid states are: -1 - ERROR 0 - LISTEN 1 - CLIENT 2 - CLOSED
     *
     * @return the int representing the current state of the {@link Server}.
     */
    public int getState() {
        return state;
    }

    /**
     * Closes {@link SocketThread} at specified key of
     * HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param hash Hash Key corresponding to value to remove from socket_list.
     */
    @Override
    public synchronized void disconnect(String hash) {
        if (socket_list != null && socket_list.containsKey(hash)) {
            if (socket_list.get(hash).getRun() == SocketThread.RUNNING || socket_list.get(hash).getRun() == SocketThread.CONFIRMED || socket_list.get(hash).getRun() == SocketThread.ERROR) {
                LOGGER.log(Level.INFO, "Attempting to close running SocketThread with hash {0}", hash);
            } else {
                LOGGER.log(Level.INFO, "Attempting to close non-running SocketThread with hash {0}", hash);
            }
            try {
                socket_list.get(hash).unblock();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close SocketThread", e);
            }
            socket_list.remove(hash);
            LOGGER.log(Level.INFO, "Closed SocketThread has been interrupted, removing from socket_list on Server");
            if (use_disconnected_sockets) {
                disconnected_sockets.add(hash);
                if (use_message_queues && queue_list.containsKey(hash)) {
                    queue_list.get(hash).queueDisconnected();
                }
                LOGGER.log(Level.INFO, "Closed SocketThread's hash has been added to disconnected sockets list");
            } else {
                if (use_message_queues && queue_list.containsKey(hash)) {
                    try {
                        removeQueue(hash);
                    } catch (HashNotFoundException | NullException e) {
                        LOGGER.log(Level.INFO, e.getMessage());
                    }
                }
            }
        } else {
            LOGGER.log(Level.INFO, "Socket Thread with hash {0} does not exist on Server", hash);
        }
    }

    /**
     * This function is not used.
     *
     * @param action Not used.
     * @param clientId Not used.
     */
    @Override
    public synchronized void customNetwork1(List<String> action, String clientId) {
        LOGGER.log(Level.INFO, "Server does not have an implementation for Networking.customNetwork1");
    }

    /**
     * Removes the {@link MessageQueue} specified by the key hash from the
     * queue_list Map.
     *
     * @param hash String key to remove from the queue_list Map.
     * @throws HashNotFoundException if the parameter hash does not exist in
     * queue_list.
     * @throws NullException if attribute queue_list is set to null. This
     * typically indicates that the {@link MessageQueue} functionality is not in
     * use.
     */
    public synchronized void removeQueue(String hash) throws HashNotFoundException, NullException {
        LOGGER.log(Level.INFO, "Attempting to close MessageQueue with hash {0}", hash);
        if (queue_list != null) {
            if (!queue_list.isEmpty() && queue_list.containsKey(hash)) {
                queue_list.get(hash).close();
                queue_list.remove(hash);
                LOGGER.log(Level.INFO, "Removed MessageQueue {0} from queue_list", hash);
            } else {
                throw new HashNotFoundException("MessageQueue with hash " + hash + " does not exist in queue_list");
            }
        } else {
            throw new NullException("queue_list is set to null. Will not attempt to close MessageQueue with hash " + hash);
        }
    }

    /**
     * Removes the specified hash from disconnected_sockets.
     *
     * @param hash String hash to remove.
     * @throws HashNotFoundException if parameter hash does not exist in the
     * disconnected_sockets list.
     * @throws NullException if the disconnected_sockets list is set to null.
     */
    public synchronized void removeDisconnectedSocket(String hash) throws HashNotFoundException, NullException {
        if (disconnected_sockets != null) {
            if (disconnected_sockets.contains(hash)) {
                disconnected_sockets.remove(hash);
                LOGGER.log(Level.INFO, "Removed hash from disconnected_sockets: {0}", hash);
            } else {
                throw new HashNotFoundException("Hash " + hash + " does not exist in disconnected_sockets");
            }
        } else {
            throw new NullException("disconnected_sockets list is set to null. Will not attempt to remove hash " + hash);
        }
    }

    /**
     * Starts the {@link SocketThread} at the location in socket_list specified
     * by hash. This function will wait up to 5 seconds for the
     * {@link SocketThread} to start. if the {@link SocketThread} does not start
     * in time this function will throw an IOException.
     *
     * @param hash the String key in socket_list corresponding to the
     * {@link SocketThread} to start.
     * @throws TimeoutException if the {@link SocketThread} does not start
     * before Timeout is reached.
     * @throws NullException if attribute socket_list is set to null.
     * @throws HashNotFoundException if the parameter hash does not exist in
     * attribute socket_list.
     * @throws FT1EngineError if a NullException or HashNotFoundException is
     * caught when running removeDisconnectedSocket. This is an internal engine
     * error and does not need to be handled.
     */
    public synchronized void startSocket(String hash) throws TimeoutException, NullException, HashNotFoundException, FT1EngineError {
        if (socket_list != null) {
            if (socket_list.containsKey(hash)) {
                socket_list.get(hash).start();
                boolean started = false;
                Timing timer = new Timing();
                while (!started && socket_list.containsKey(hash)) {
                    if (socket_list.get(hash).getRun() == SocketThread.NEW) {
                        if (timer.getTime() > timeout) {
                            started = true;
                            disconnect(hash);
                            if (use_disconnected_sockets) {
                                try {
                                    removeDisconnectedSocket(hash);
                                } catch (NullException | HashNotFoundException e) {
                                    throw new FT1EngineError("Internal enginer error: Caught a NullException or HashNotFoundException when trying to run Server.removeDisconnectedSocket from Server.startSocket");
                                }
                            }
                            throw new TimeoutException("SocketThread was created but did not start in time");
                        }
                    } else {
                        started = true;
                    }
                }
            } else {
                throw new HashNotFoundException("Hash " + hash + " does not exist in attribute socket_list");
            }
        } else {
            throw new NullException("Attribute socket_list is set to null");
        }
    }

    /**
     * Starts the {@link MessageQueue} at the location in queue_list specified
     * by hash. This function will wait up to 5 seconds for the
     * {@link MessageQueue} to start.
     *
     * @param hash the String key in queue_list corresponding to the
     * {@link MessageQueue} to start.
     * @throws TimeoutException if the {@link MessageQueue} does not start
     * before Timeout is reached.
     * @throws NullException if attribute queue_list is set to null.
     * @throws HashNotFoundException if the parameter hash does not exist in
     * attribute queue_list.
     */
    public synchronized void startQueue(String hash) throws HashNotFoundException, NullException, TimeoutException {
        if (queue_list != null) {
            if (queue_list.containsKey(hash)) {
                queue_list.get(hash).start();
                boolean started = false;
                Timing timer = new Timing();
                while (!started) {
                    if (queue_list.get(hash).getRun() == MessageQueue.NEW) {
                        timer.waitTime(5);
                        if (timer.getTime() > timeout) {
                            started = true;
                            throw new TimeoutException("MessageQueue was created but did not start in time");
                        }
                    } else {
                        started = true;
                        LOGGER.log(Level.INFO, "Successfully started MessageQueue for SocketThread {0}", hash);
                    }
                }
            } else {
                throw new HashNotFoundException("Hash " + hash + " does not exist in attribute queue_list");
            }
        } else {
            throw new NullException("Attribute queue_list is set to null");
        }
    }

    private void addSocketThread(String hash, SocketThread new_socket) throws TimeoutException, FT1EngineError, SocketException {
        try {
            new_socket.setTimeout(timeout);
        } catch (InvalidArgumentException e) {
            throw new FT1EngineError("Internal engine error: Caught InvalidArgumentException when running SocketThread.setTimeout() from Server.addSocketThread()", e);
        }
        if (state == LISTEN || !use_connection_confirmation) {
            try {
                new_socket.setRun(SocketThread.CONFIRMED);
            } catch (InvalidArgumentException e) {
                throw new FT1EngineError("Internal engine error: Caught InvalidArgumentException when running SocketThread.setRun() from Server.addSocketThread()", e);
            }
        }
        if (use_socket_timeout) {
            try {
                new_socket.setSocketTimeout(socket_timeout);
            } catch (InvalidArgumentException e) {
                throw new FT1EngineError("Internal engine error: Caught InvalidArgumentException when running SocketThread.setSocketTimeout() from Server.addSocketThread()", e);
            }
            try {
                new_socket.setSocketTimeoutCount(socket_timeout_count);
            } catch (InvalidArgumentException e) {
                throw new FT1EngineError("Internal engine error: Caught InvalidArgumentException when running SocketThread.setSocketTimeoutCount() from Server.addSocketThread()", e);
            }
        }
        socket_list.put(hash, new_socket);
        if (use_message_queues) {
            try {
                addQueue(hash);
            } catch (FeatureNotUsedException e) {
                throw new FT1EngineError("Internal engine error: Caught a FeatureNotUsedException while running Server.addQueue from Server.addSocketThread");
            }
        }
        try {
            startSocket(hash);
        } catch (NullException | HashNotFoundException e) {
            throw new FT1EngineError("Internal engine error: Caught a NullException or HashNotFoundException while running Server.startSocket() from Server.addSocketThread");
        }
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Server}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param socket Pre-constructed {@link Server} to use for connection.
     * @return The hash assigned to the SocketThread.
     * @throws TimeoutException if the new SocketThread did not finish starting
     * before timeout was reached.
     * @throws IOException if an exception is found when creating the new
     * {@link Sock} or starting the new {@link SocketThread}.
     */
    public synchronized String addSocket(Socket socket) throws TimeoutException, IOException {
        Sock temp_socket = new Sock(socket);
        String hash = generateUniqueHash();
        SocketThread new_socket = new SocketThread(temp_socket, this, hash);
        addSocketThread(hash, new_socket);
        return hash;
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Sock}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param sock Pre-constructed {@link Sock} to use for connection.
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when starting the new
     * {@link SocketThread}.
     * @throws TimeoutException if the new SocketThread did not finish starting
     * before timeout was reached.
     */
    public synchronized String addSocket(Sock sock) throws IOException, TimeoutException {
        String hash = generateUniqueHash();
        SocketThread new_socket = new SocketThread(sock, this, hash);
        addSocketThread(hash, new_socket);
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address.
     *
     * @param ip IP address of {@link Server}.
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when creating the new
     * {@link Sock} or starting the new {@link SocketThread}.
     * @throws TimeoutException if the new SocketThread did not finish starting
     * before timeout was reached.
     */
    public synchronized String addSocket(String ip) throws IOException, TimeoutException {
        Sock temp_socket = new Sock(ip, port);
        String hash = generateUniqueHash();
        SocketThread new_socket = new SocketThread(temp_socket, this, hash);
        addSocketThread(hash, new_socket);
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address and port
     * number.
     *
     * @param ip IP address of {@link Server}.
     * @param port Port number of {@link Server}.
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when creating the new
     * {@link Sock} or starting the new {@link SocketThread}
     * @throws TimeoutException if the new SocketThread did not finish starting
     * before timeout was reached.
     */
    public synchronized String addSocket(String ip, int port) throws IOException, TimeoutException {
        Sock temp_socket = new Sock(ip, port);
        String hash = generateUniqueHash();
        SocketThread new_socket = new SocketThread(temp_socket, this, hash);
        addSocketThread(hash, new_socket);
        return hash;
    }

    /**
     * Creates a new {@link MessageQueue} and places it in the queue_list Map at
     * the key specified by hash if use_message_queues == true. This functions
     * also starts the {@link MessageQueue} using the startQueue(String hash)
     * function.
     *
     * @param hash The String key to associate with the new
     * {@link MessageQueue}.
     * @throws TimeoutException if the {@link MessageQueue} does not start
     * before Timeout is reached.
     * @throws FeatureNotUsedException if use_message_queues is set to false.
     * This functions can only be used if the {@link MessageQueue} feature is
     * turned on. Use setUseMessageQueues(true) to turn this feature on.
     * @throws FT1EngineError if a HashNotFoundException or NullException is
     * caught when running startQueue. This function should ensure neither of
     * these exceptions are received. If one is received there is an error in
     * the engine code.
     */
    public synchronized void addQueue(String hash) throws TimeoutException, FeatureNotUsedException, FT1EngineError {
        if (use_message_queues) {
            MessageQueue new_queue = new MessageQueue(this, hash);
            queue_list.put(hash, new_queue);
            try {
                startQueue(hash);
            } catch (HashNotFoundException | NullException e) {
                throw new FT1EngineError("An internal engine error occurred when running Server.startQueue from Server.startQueue");
            }
        } else {
            throw new FeatureNotUsedException("MessageQueue feature is not turned on, cannot use addQueue function");
        }
    }

    /**
     * Generates a random hash key.
     *
     * @return the generated hash in String form.
     */
    public String generateHash() {
        String hash = "";
        do {
            double seed = Math.random();
            if (seed < 0.1) {
                seed += 0.1;
            } else if (seed >= 1.0) {
                seed -= 0.1;
            }
            seed = seed * 100;
            long seed_round = Math.round(seed);
            seed = seed_round;
            hash = hash + Double.toString(seed);
        } while (hash.length() < 8);
        LOGGER.log(Level.INFO, "Generated new hash {0}", hash);
        return hash;
    }

    /**
     * Generates a unique, random hash key.
     *
     * @return the generated unique hash in String form.
     */
    public String generateUniqueHash() {
        String hash = "";
        boolean exists = true;
        int count = 0;
        while (exists) {
            hash = generateHash();
            exists = containsHash(hash);
            if (disconnected_sockets != null && !disconnected_sockets.isEmpty()) {
                exists = disconnected_sockets.contains(hash);
            }
            if (queue_list != null && !queue_list.isEmpty()) {
                exists = queue_list.containsKey(hash);
            }
            count++;
        }
        LOGGER.log(Level.INFO, "{0} hashes created before unique hash was found", count);
        return hash;
    }

    /**
     * Checks a key to see if it exists in socket_list.
     *
     * @param hash Key to check whether it exists.
     * @return true or false depending on whether the Key exists in socket_list.
     */
    public boolean containsHash(String hash) {
        boolean exists = false;
        if (socket_list != null) {
            exists = socket_list.containsKey(hash);
        }
        return exists;
    }

    /**
     * Moves a value in socket_list from one key to another and removes the
     * previously used key. This function will disconnect the new key if a
     * {@link SocketThread} already exists at the new key.
     *
     * @param old_hash Current key associated with the desired value.
     * @param new_hash New Key to associate with the desired value.
     * @throws HashNotFoundException if the parameter old_hash does not exist in
     * the attribute socket_list.
     * @throws InvalidArgumentException if parameters old_hash and new_hash are
     * the same.
     */
    public void replaceHash(String old_hash, String new_hash) throws HashNotFoundException, InvalidArgumentException {
        if (!socket_list.isEmpty() && socket_list.containsKey(old_hash)) {
            if (old_hash.compareTo(new_hash) != 0) {
                if (socket_list.containsKey(new_hash)) {
                    disconnect(new_hash);
                    if (use_disconnected_sockets) {
                        try {
                            removeDisconnectedSocket(new_hash);
                        } catch (NullException e) {
                            throw new FT1EngineError("Internal engine error: Caught a NullException when running Server.removeDisconnectedSocket from Server.replaceHash");
                        }
                    }
                }
                socket_list.put(new_hash, socket_list.get(old_hash));
                socket_list.get(new_hash).setHash(new_hash);
                socket_list.remove(old_hash);
                if (use_message_queues) {
                    if (queue_list.containsKey(old_hash)) {
                        if (!queue_list.containsKey(new_hash)) {
                            queue_list.put(new_hash, queue_list.get(old_hash));
                            queue_list.get(new_hash).setHash(new_hash);
                            queue_list.remove(old_hash);
                        } else {
                            try {
                                removeQueue(old_hash);
                            } catch (HashNotFoundException | NullException e) {
                                throw new FT1EngineError("Internal engine error: Caught a HashNotFoundException or NullException when running Server.removeQueue from Server.replaceHash");
                            }
                            boolean disconnected = false;
                            Timing new_timer = new Timing();
                            while (!disconnected) {
                                if (!queue_list.containsKey(old_hash) || new_timer.getTime() > timeout) {
                                    disconnected = true;
                                }
                            }
                        }
                    } else {
                        throw new FT1EngineError("Internal engine error: No value at queue_list.get(" + old_hash + ") when there is a value at socket_list.get(" + old_hash + ") and use_message_queues equals true. Value expected");
                    }
                }
                LOGGER.log(Level.INFO, "Moved socket details from hash {0} to hash {1}", new Object[]{old_hash, new_hash});
            } else {
                throw new InvalidArgumentException("old_hash is equal to new_hash. Cannot replace a hashes data with data from the same hash");
            }
        } else {
            throw new HashNotFoundException("SocketThread with hash " + old_hash + " does not exist in socket_list");
        }

    }

    /**
     * Function used to check if a particular hash is contained in the
     * disconnected_sockets array. If it is the SocketThread is moved from its
     * current hash to a new hash matching the one found in the
     * disconnect_sockets array. It then returns true so any game specific data
     * associated with the SocketThreads old hash can be re-mapped to the new
     * hash. This function is intended to facilitate seamless reconnection to
     * game servers and allow players to pick up where they left off if a
     * disconnect occurs.
     *
     * @param current_hash The hash currently associated with the SocketThread.
     * @param saved_hash The hash to check for in the list of
     * disconnected_sockets.
     * @throws HashNotFoundException if the parameter saved_hash does not exist
     * in the attribute disconnected_sockets.
     * @throws InvalidArgumentException if parameters current_hash and
     * saved_hash are the same.
     * @throws FeatureNotUsedException if use_disconnected_sockets is set to
     * false. This function can only be used as part of the Disconnected Sockets
     * feature, use setUseDisconnectedSockets(true) to turn this feature on.
     */
    public void connectDisconnectedSocket(String current_hash, String saved_hash) throws HashNotFoundException, InvalidArgumentException, FeatureNotUsedException {
        boolean exists = false;
        if (use_disconnected_sockets) {
            if (disconnected_sockets != null && !disconnected_sockets.isEmpty() && disconnected_sockets.contains(saved_hash)) {
                if (current_hash.compareTo(saved_hash) != 0) {
                    try {
                        replaceHash(current_hash, saved_hash);
                    } catch (HashNotFoundException | InvalidArgumentException e) {
                        throw new FT1EngineError("Internal engine error: Caught HashNotFoundException or InvalidArgumentException when running Server.replaceHash from Server.connectDisconnectedSocket", e);
                    }
                    if (use_message_queues) {
                        queue_list.get(saved_hash).resumeQueue();
                        LOGGER.log(Level.INFO, "Reconnected MessageQueue {0} for reconnected socket", saved_hash);
                    }
                    if (disconnected_sockets.contains(saved_hash)) {
                        try {
                            removeDisconnectedSocket(saved_hash);
                        } catch (NullException | HashNotFoundException e) {
                            throw new FT1EngineError("Internal engine error: Caught NullException when running Server.removeDisconnectedSocket from server.connectDisconnectedSocket", e);
                        }
                    }
                    LOGGER.log(Level.INFO, "Connected disconnected socket {1} using data from socket {0}. Hash {0} has been removed", new Object[]{current_hash, saved_hash});
                } else {
                    throw new InvalidArgumentException("current_hash " + current_hash + " is the same as saved_hash " + saved_hash + ". Cannot reconnect a socket with data from the same hash");
                }

            } else {
                throw new HashNotFoundException("SocketThread with hash " + saved_hash + " is not registered as disconnected, will not run reconnection process");
            }
        } else {
            throw new FeatureNotUsedException("Disconnected Sockets feature is not turned on. Cannot use the connectDisconnectedSockets function. Use setUseDisconnectedSockets(true) to turn this feature on");
        }
    }

    /**
     * The function used to start the thread listen_thread. This method only
     * works if the server was specified as a listen Server by passing true in
     * the constructor.
     *
     * @throws IOException if an exception is found when closing the
     * {@link ListenThread}.
     * @throws ServerSocketCloseException if the ServerSocket on ListenThread
     * fails to close.
     * @throws FeatureNotUsedException if state is not set to LISTEN. This
     * function cannot be used if state is not set to LISTEN, use
     * setRun(Server.LISTEN) to turn this feature on.
     */
    public void startThread() throws IOException, ServerSocketCloseException, FeatureNotUsedException {
        if (state == LISTEN) {
            listen_thread.start();
            boolean started = false;
            Timing timer = new Timing();
            while (!started) {
                if (!listen_thread.getRun()) {
                    timer.waitTime(5);
                    if (timer.getTime() > timeout) {
                        started = true;
                        try {
                            listen_thread.close();
                        } catch (ServerSocketCloseException e) {
                            throw new ServerSocketCloseException("listen_thread failed to start in time. Attempted to close listen_thread but failed", e);
                        }
                    }
                } else {
                    started = true;
                }
            }
            LOGGER.log(Level.INFO, "Started listen_thread");
        } else {
            throw new FeatureNotUsedException("Server is not set to listen. Will not attempt to start ListenThread");
        }
    }

    /**
     * Blocks while waiting for a new client connection. Once the client
     * connection is received a new {@link SocketThread} is created and
     * initialized to allow interfacing with the newly connected client. This
     * method only works if the server was specified as a listen Server by
     * passing true in the constructor.
     *
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when accepting a new
     * connection through the ServerSocket or when the new {@link SocketThread}
     * is started.
     * @throws TimeoutException if the new {@link SocketThread} or
     * {@link MessageQueue} (if use_message_queues equals true) fails to start
     * before timeout is reached.
     * @throws FeatureNotUsedException if state is not set to LISTEN this
     * feature cannot be used.
     * @throws NullException if listen_thread equals null.
     */
    public String listen() throws IOException, TimeoutException, FeatureNotUsedException, NullException {
        if (state == LISTEN) {
            if (listen_thread != null) {
                String hash = "";
                Socket temp_socket = listen_thread.getServerSocket().accept();
                LOGGER.log(Level.INFO, "Connection detected on ListenThread. ListenThread running is set to {0}", listen_thread.getRun());
                if (listen_thread.getRun()) {
                    Sock temp_sock = new Sock(temp_socket);
                    hash = generateUniqueHash();
                    SocketThread new_socket = new SocketThread(temp_sock, this, hash);
                    addSocketThread(hash, new_socket);
                    if (use_connection_confirmation) {
                        sendMessage("customnetworking1", hash);
                    }
                    LOGGER.log(Level.INFO, "New client connected to ListenServer. Connection was stored in socket_list");
                } else {
                    LOGGER.log(Level.INFO, "ListenThread is closing, new connection was ignored");
                }
                return hash;
            } else {
                throw new NullException("listen_thread is set to null. Cannot run listen");
            }
        } else {
            throw new FeatureNotUsedException("Server is not set to listen. Will not attempt to listen for new connections. State " + state);
        }
    }

    /**
     * Sends a blank String to every connected socket. If an exception is caught
     * when sending the String it removes the {@link SocketThread} from the
     * socket_list. Useful for checking whether any sockets have disconnected
     * but were not detected as disconnected automatically by the
     * {@link Server}.
     *
     * @throws IOException if an exception is encountered when running
     * sendMessage().
     */
    public void pingSockets() throws IOException {
        ArrayList<String> hash_list = new ArrayList<>();
        for (SocketThread socket : socket_list.values()) {
            hash_list.add(socket.getHash());
        }
        sendMessage("", hash_list);
    }

    /**
     * Puts the attribute states of {@link Server} in readable form.
     *
     * @return Attributes of {@link Server} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = toString("");
        return to_string;
    }

    /**
     * Puts the attribute states of {@link Server} in readable form. Takes
     * String input to assist formatting. Useful to add special characters to
     * assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link Server} in a readable String form.
     */
    public String toString(String ch) {
        String to_string = ch;
        if (state == LISTEN) {
            to_string += "Listen Server ";
        } else if (state == CLOSED) {
            to_string += "Closed Server ";
        } else {
            to_string += "Server ";
        }
        to_string += "attribute values:\n" + ch + "\tState: " + state + "\n" + ch + "\tPort: " + port + "\n" + ch + "\tUse disconnected sockets: " + use_disconnected_sockets + "\n" + ch + "\tUse message queues: " + use_message_queues;
        if (use_disconnected_sockets) {
            to_string += "\n" + ch + "\tDisconnected Sockets";
            if (!disconnected_sockets.isEmpty()) {
                String disconnects = "";
                int number_of_disconnects = 0;
                for (String hash : disconnected_sockets) {
                    number_of_disconnects++;
                    disconnects += "\n" + ch + "\t\t" + number_of_disconnects + ". " + hash;
                }
                to_string += " (total disconnects - " + number_of_disconnects + "):" + disconnects;
            } else {
                to_string += ": No hashes in disconnected sockets list";
            }
        }
        if (listen_thread != null) {
            to_string += "\n" + ch + "\tListenThread:\n" + listen_thread.toString(ch + "\t\t");
        }
        to_string += "\n" + ch + "\tSocket List";
        if (socket_list != null && !socket_list.isEmpty()) {
            String sockets = "";
            int number_of_sockets = 0;
            for (SocketThread socket : socket_list.values()) {
                number_of_sockets++;
                sockets += "\n" + ch + "\t\t" + number_of_sockets + ". SocketThread attribute values:";
                sockets += "\n" + socket.toString(ch + "\t\t\t");
            }
            to_string += " (total sockets - " + number_of_sockets + "):" + sockets;
        } else {
            to_string += ": No sockets are connected";
        }
        to_string += "\n" + ch + "\tQueue List";
        if (queue_list != null && !queue_list.isEmpty()) {
            String queues = "";
            int number_of_queues = 0;
            for (MessageQueue queue : queue_list.values()) {
                number_of_queues++;
                queues += "\n" + ch + "\t\t" + number_of_queues + ". MessageQueue attribute values:";
                queues += "\n" + queue.toString(ch + "\t\t\t");
            }
            to_string += " (total queues - " + number_of_queues + "):" + queues;
        } else {
            to_string += ": No queues are running";
        }
        return to_string;
    }

    /**
     * Used to send a message to a list of sockets. Takes the String to send and
     * a List of the hashes associated with the sockets to send to as input.
     *
     * @param message The String to send.
     * @param clientIds The List of hashes to send to.
     */
    @Override
    protected void sendMessage(String message, List<String> clientIds) {
        for (String hash : clientIds) {
            if (socket_list.containsKey(hash) && (socket_list.get(hash).getRun() == SocketThread.RUNNING || socket_list.get(hash).getRun() == SocketThread.CONFIRMED || socket_list.get(hash).getRun() == SocketThread.ERROR)) {
                if (use_message_queues) {
                    queue_list.get(hash).queueMessage(message);
                } else {
                    socket_list.get(hash).sendMessage(message);
                }
            } else {
                LOGGER.log(Level.INFO, "Socket with hash {0} does not exist or is not running", hash);
            }
        }
    }

    /**
     * Used to send a message to one socket. Takes the String to send and the
     * hash associated with the socket to send to as input.
     *
     * @param message The String to send.
     * @param clientId The hash to send to.
     */
    @Override
    protected void sendMessage(String message, String clientId) {
        if (socket_list.containsKey(clientId) && (socket_list.get(clientId).getRun() == SocketThread.RUNNING || socket_list.get(clientId).getRun() == SocketThread.CONFIRMED || socket_list.get(clientId).getRun() == SocketThread.ERROR)) {
            if (use_message_queues) {
                queue_list.get(clientId).queueMessage(message);
            } else {
                socket_list.get(clientId).sendMessage(message);
            }
        } else {
            LOGGER.log(Level.INFO, "Socket with hash {0} does not exist or is not running", clientId);
        }
    }
}
