package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import fantasyteam.ft1.Timing;
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
     * Valid state for {@link Server}. Used when there is an error starting the
     * {@link Server}.
     */
    public static final int ERROR = -1;

    /**
     * Valid state for {@link Server}. Used when it is set to use
     * {@link ListenThread} to listen on a port for new connections.
     */
    public static final int LISTEN = 0;

    /**
     * Valid state for {@link Server}. Used when it is not set to use
     * {@link ListenThread} to listen on a port for new connections.
     */
    public static final int CLIENT = 1;

    /**
     * Valid state for {@link Server}. Used when it has closed.
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
        use_disconnected_sockets = false;
        use_message_queues = false;
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
     */
    public Server(Game game, int port, boolean listen) throws IOException {
        super(game);
        this.port = port;
        use_disconnected_sockets = false;
        use_message_queues = false;
        socket_list = new HashMap<>();
        queue_list = new HashMap<>();
        disconnected_sockets = new ArrayList<String>();
        if (listen) {
            try {
                setListenThread();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to start ListenThread on server. Server state has been set as errored", e);
                throw new IOException("Failed to start ListenThread on server. Server state has been set as errored");
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
     */
    public synchronized void close() throws IOException {
        use_disconnected_sockets = false;
        closeListenThread();
        if (socket_list != null && !socket_list.isEmpty()) {
            Set<String> socket_set = socket_list.keySet();
            Object[] socket_array = socket_set.toArray();
            for (Object socket_hash : socket_array) {
                disconnect(String.valueOf(socket_hash));
            }
        }
        boolean not_disconnected = true;
        Timing timer = new Timing();
        while (not_disconnected) {
            if (socket_list.isEmpty() || timer.getTime() > 5000) {
                not_disconnected = false;
            }
        }
        if (queue_list != null && !queue_list.isEmpty()) {
            Set<String> queue_set = queue_list.keySet();
            Object[] queue_array = queue_set.toArray();
            for (Object queue_hash : queue_array) {
                removeQueue(String.valueOf(queue_hash));
            }
        }
        not_disconnected = true;
        timer.startTiming();
        while (not_disconnected) {
            if (queue_list.isEmpty() || timer.getTime() > 5000) {
                not_disconnected = false;
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
     */
    public synchronized void closeListenThread() throws IOException {
        if (state == LISTEN && listen_thread != null) {
            if (listen_thread.getRun()) {
                LOGGER.log(Level.INFO, "Attempting to close running listen_thread on port {0}", listen_thread.getPort());
                listen_thread.setRun(false);
                boolean running = true;
                Timing new_timer = new Timing();
                while (running) {
                    if (!listen_thread.getRun() || new_timer.getTime() > 5000) {
                        running = false;
                    }
                }
                boolean not_closed = true;
                boolean attempt_disconnection = true;
                Socket socket = null;
                new_timer.startTiming();
                while (not_closed) {
                    if (listen_thread.getServerSocket() == null) {
                        not_closed = false;
                    } else if ((new_timer.getTime() > 5000 && listen_thread.getRun()) || (new_timer.getTime() > 5000 && listen_thread.getServerSocket() != null)) {
                        attempt_disconnection = true;
                        not_closed = false;
                    } else if (new_timer.getTime() > 5000) {
                        not_closed = false;
                    }
                    if (attempt_disconnection) {
                        LOGGER.log(Level.INFO, "Creating blank connection to break blocking on ListenThread. ListenThread running is set to {0}", listen_thread.getRun());
                        try {
                            socket = new Socket("127.0.0.1", port);
                        } catch (SocketException e) {
                            LOGGER.log(Level.INFO, "Connection refused on ListenThread");
                        }
                        attempt_disconnection = false;
                    }
                }
                if (socket != null) {
                    socket.close();
                    LOGGER.log(Level.INFO, "Closed blank connection to ListenThread");
                }
            } else {
                LOGGER.log(Level.INFO, "Attempting to close non-running listen_thread on port {0}", listen_thread.getPort());
                listen_thread.close();
            }
        }
    }

    /**
     * Sets the port number used for connections.
     *
     * @param port Port number to listen for connections on.
     */
    public synchronized void setPort(int port) {
        this.port = port;
        LOGGER.log(Level.INFO, "Changing port number: {0}", port);
    }

    /**
     * Sets whether to keep the hashes of disconnected sockets.
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
     */
    public synchronized void setUseMessageQueues(boolean use) {
        LOGGER.log(Level.INFO, "Toggling flag use_message_queues: {0}", use);
        if (use && !use_message_queues) {
            if (socket_list != null && !socket_list.isEmpty()) {
                for (SocketThread socket : socket_list.values()) {
                    queue_list.put(socket.getHash(), new MessageQueue(this, socket.getHash()));
                    startQueue(socket.getHash());
                }
            }
        } else if (!use && use_message_queues) {
            if (queue_list != null && !queue_list.isEmpty()) {
                for (MessageQueue queue : queue_list.values()) {
                    removeQueue(queue.getHash());
                }
            }
        }
        use_message_queues = use;
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
        LOGGER.log(Level.INFO, "Changing queue_list. New queue_list:\n{0}", queue_list.toString());
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
     * {@link ListenThread}.
     */
    public synchronized void setListenThread() throws IOException {
        closeListenThread();
        try {
            listen_thread = new ListenThread(this);
            state = LISTEN;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create ListenThread on port {0}", port);
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
     * @return the Map(String,{@link {MessageQueue}) queue_list, the list of {@link MessageQueue}s on the {@link Server}.
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
                    removeQueue(hash);
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
     */
    public synchronized void removeQueue(String hash) {
        LOGGER.log(Level.INFO, "Attempting to close MessageQueue with hash {0}", hash);
        if (queue_list != null && !queue_list.isEmpty()) {
            if (queue_list.containsKey(hash)) {
                queue_list.get(hash).close();
                queue_list.remove(hash);
                LOGGER.log(Level.INFO, "Removed MessageQueue {0} from queue_list", hash);
            } else {
                LOGGER.log(Level.INFO, "MessageQueue with hash {0} does not exist in socket_list", hash);
            }
        } else {
            LOGGER.log(Level.INFO, "use_message_queues is false or queue_list is empty. Will not attempt to close MessageQueue with hash {0}", hash);
        }
    }

    /**
     * Removes the specified hash from disconnected_sockets.
     *
     * @param hash String hash to remove.
     */
    public synchronized void removeDisconnectedSocket(String hash) {
        if (disconnected_sockets.contains(hash)) {
            disconnected_sockets.remove(hash);
            LOGGER.log(Level.INFO, "Removed hash from disconnected_sockets: {0}", hash);
        } else {
            LOGGER.log(Level.INFO, "Hash does not exist in disconnected_sockets: {0}", hash);
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
     * @throws IOException if an exception is encountered starting the new
     * {@link SocketThread}.
     */
    public synchronized void startSocket(String hash) throws IOException {
        socket_list.get(hash).start();
        boolean started = false;
        Timing timer = new Timing();
        while (!started && socket_list.containsKey(hash)) {
            if (socket_list.get(hash).getRun() == SocketThread.NEW) {
                if (timer.getTime() > 5000) {
                    started = true;
                    disconnect(hash);
                    removeDisconnectedSocket(hash);
                    LOGGER.log(Level.SEVERE, "Socket was created but did not start in time");
                    throw new IOException("Socket was created but did not start in time");
                }
            } else {
                started = true;
            }
        }
    }

    /**
     * Starts the {@link MessageQueue} at the location in queue_list specified
     * by hash. This function will wait up to 5 seconds for the
     * {@link MessageQueue} to start.
     *
     * @param hash the String key in queue_list corresponding to the
     * {@link MessageQueue} to start.
     */
    public synchronized void startQueue(String hash) {
        queue_list.get(hash).start();
        boolean started = false;
        Timing timer = new Timing();
        while (!started) {
            if (queue_list.get(hash).getRun() == MessageQueue.NEW) {
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

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Server}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param socket Pre-constructed {@link Server} to use for connection.
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when creating the new
     * {@link Sock} or starting the new {@link SocketThread}.
     */
    public synchronized String addSocket(Socket socket) throws IOException {
        String hash = null;
        try {
            Sock temp_socket = new Sock(socket);
            hash = generateUniqueHash();
            SocketThread new_socket = new SocketThread(temp_socket, this, hash);
            socket_list.put(hash, new_socket);
            addQueue(hash);
            startSocket(hash);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by Socket, connection was not established\nSocket details: {0}", socket.toString());
            LOGGER.log(Level.INFO, "Caught exception: {0}", e);
            throw new IOException("Failed to add or start SocketThread by Socket, connection was not established\\nSocket details:" + socket.toString());
        }
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
     */
    public synchronized String addSocket(Sock sock) throws IOException {
        String hash = null;
        try {
            hash = generateUniqueHash();
            SocketThread new_socket = new SocketThread(sock, this, hash);
            socket_list.put(hash, new_socket);
            addQueue(hash);
            startSocket(hash);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by Sock, connection was not established\nSock details: \n{0}", sock.toString());
            LOGGER.log(Level.INFO, "Caught exception: {0}", e);
            throw new IOException("Failed to add or start SocketThread by Sock, connection was not established\nSock details:" + sock.toString());
        }
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address.
     *
     * @param ip IP address of {@link Server}.
     * @return The hash assigned to the SocketThread.
     * @throws IOException if an exception is found when creating the new
     * {@link Sock} or starting the new {@link SocketThread}.
     */
    public synchronized String addSocket(String ip) throws IOException {
        String hash = null;
        try {
            Sock temp_socket = new Sock(ip, port);
            hash = generateUniqueHash();
            SocketThread new_socket = new SocketThread(temp_socket, this, hash);
            socket_list.put(hash, new_socket);
            addQueue(hash);
            startSocket(hash);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by IP, connection was not established\nIP: {0}", ip);
            LOGGER.log(Level.INFO, "Caught exception: {0}", e);
            throw new IOException("Failed to add or start SocketThread by IP, connection was not established\nIP:" + ip);
        }
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
     * {@link Sock} or starting the new {@link SocketThread}.
     */
    public synchronized String addSocket(String ip, int port) throws IOException {
        String hash = null;
        try {
            Sock temp_socket = new Sock(ip, port);
            hash = generateUniqueHash();
            SocketThread new_socket = new SocketThread(temp_socket, this, hash);
            socket_list.put(hash, new_socket);
            addQueue(hash);
            startSocket(hash);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by IP and port, connection was not established\nIP: {0}\nPort: {1}", new Object[]{ip, port});
            LOGGER.log(Level.INFO, "Caught exception: {0}", e);
            throw new IOException("Failed to add or start SocketThread by IP and port, connection was not established\nIP: " + ip + "\nPort: " + port);
        }
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
     */
    public synchronized void addQueue(String hash) {
        if (use_message_queues) {
            MessageQueue new_queue = new MessageQueue(this, hash);
            queue_list.put(hash, new_queue);
            startQueue(hash);
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
        boolean exists;
        exists = socket_list.containsKey(hash);
        return exists;
    }

    /**
     * Moves a value in socket_list from one key to another and removes the
     * previously used key. This function will disconnect the new key if a
     * {@link SocketThread} already exists at the new key.
     *
     * @param old_hash Current key associated with the desired value.
     * @param new_hash New Key to associate with the desired value.
     */
    public void replaceHash(String old_hash, String new_hash) {
        if (!socket_list.isEmpty() && socket_list.containsKey(old_hash)) {
            if (old_hash.compareTo(new_hash) != 0) {
                if (socket_list.containsKey(new_hash)) {
                    disconnect(new_hash);
                    if (use_disconnected_sockets) {
                        removeDisconnectedSocket(new_hash);
                    }
                }
                socket_list.put(new_hash, socket_list.get(old_hash));
                socket_list.get(new_hash).setHash(new_hash);
                socket_list.remove(old_hash);
                if (use_message_queues) {
                    if (!queue_list.containsKey(new_hash)) {
                        queue_list.put(new_hash, queue_list.get(old_hash));
                        queue_list.get(new_hash).setHash(new_hash);
                        queue_list.remove(old_hash);
                    } else {
                        removeQueue(old_hash);
                        boolean disconnected = false;
                        Timing new_timer = new Timing();
                        while (!disconnected) {
                            if (!queue_list.containsKey(old_hash) || new_timer.getTime() > 5000) {
                                disconnected = true;
                            }
                        }
                    }
                }
                LOGGER.log(Level.INFO, "Moved socket details from hash {0} to hash {1}", new Object[]{old_hash, new_hash});
            } else {
                LOGGER.log(Level.INFO, "old_hash is equal to new_hash. Cannot replace a hashes data with data from the same hash");
            }
        } else {
            LOGGER.log(Level.INFO, "SocketThread with hash {0} does not exist in socket_list", old_hash);
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
     * @return True if the hash was found in disconnected_sockets. False if it
     * was not found.
     */
    public boolean connectDisconnectedSocket(String current_hash, String saved_hash) {
        boolean exists = false;
        if (use_disconnected_sockets) {
            if (disconnected_sockets != null && !disconnected_sockets.isEmpty() && disconnected_sockets.contains(saved_hash)) {
                if (current_hash.compareTo(saved_hash) != 0) {
                    replaceHash(current_hash, saved_hash);
                    if (use_message_queues) {
                        queue_list.get(saved_hash).resumeQueue();
                        LOGGER.log(Level.INFO, "Reconnected MessageQueue {0} for reconnected socket", saved_hash);
                    }
                    if (disconnected_sockets.contains(saved_hash)) {
                        removeDisconnectedSocket(saved_hash);
                    }
                    exists = true;
                    LOGGER.log(Level.INFO, "Connected disconnected socket {1} using data from socket {0}. Hash {0} has been removed", new Object[]{current_hash, saved_hash});
                } else {
                    LOGGER.log(Level.INFO, "current_hash {0} is the same as saved_hash {1}. Cannot reconnect a socket with data from the same hash", new Object[]{current_hash, saved_hash});
                }

            } else {
                LOGGER.log(Level.INFO, "SocketThread with hash {0} is not registered as disconnected, will not run reconnection process", saved_hash);
            }
        }
        return exists;
    }

    /**
     * The function used to start the thread listen_thread. This method only
     * works if the server was specified as a listen Server by passing true in
     * the constructor.
     *
     * @throws IOException if an exception is found when closing the
     * {@link ListenThread}.
     */
    public void startThread() throws IOException {
        if (state == LISTEN) {
            listen_thread.start();
            boolean started = false;
            Timing timer = new Timing();
            while (!started) {
                if (!listen_thread.getRun()) {
                    timer.waitTime(5);
                    if (timer.getTime() > 5000) {
                        started = true;
                        try {
                            listen_thread.close();
                            LOGGER.log(Level.SEVERE, "listen_thread failed to start in time");
                            throw new IOException("listen_thread failed to start in time");
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "listen_thread failed to start in time. Attempted to close listen_thread but failed");
                            throw new IOException("listen_thread failed to start in time. Attempted to close listen_thread but failed");
                        }
                    }
                } else {
                    started = true;
                }
            }
            LOGGER.log(Level.INFO, "Started listen_thread");
        } else {
            LOGGER.log(Level.INFO, "Server is not set to listen. Will not start ListenThread");
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
     */
    public String listen() throws IOException {
        if (state == LISTEN) {
            String hash = "";
            Socket temp_socket = listen_thread.getServerSocket().accept();
            LOGGER.log(Level.INFO, "Connection detected on ListenThread. ListenThread running is set to {0}", listen_thread.getRun());
            if (listen_thread.getRun()) {
                Sock temp_sock = new Sock(temp_socket);
                hash = generateUniqueHash();
                SocketThread new_socket = new SocketThread(temp_sock, this, hash);
                socket_list.put(hash, new_socket);
                if (use_message_queues) {
                    addQueue(hash);
                }
                startSocket(hash);
                LOGGER.log(Level.INFO, "New client connected to ListenServer. Connection was stored in socket_list");
            } else {
                LOGGER.log(Level.INFO, "ListenThread is closing, new connection was ignored");
            }
            return hash;
        } else {
            LOGGER.log(Level.INFO, "Server is not set to listen. Will not attempt to listen for new connections. State {0}", state);
            return null;
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
