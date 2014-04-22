package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Port number.
     */
    protected int port;
    /**
     * Boolean used to specify whether to keep the hashes of disconnected
     * sockets.
     */
    protected boolean use_disconnected_sockets;
    /**
     * HashMap used to hold {@link SocketThread}s and the hashed keys to
     * associate them with.
     */
    protected HashMap<String, SocketThread> socket_list;
    /**
     * ArrayList used to keep the hashes of disconnected sockets.
     */
    protected ArrayList<String> disconnected_sockets;

    /**
     * Logger for logging important actions and exceptions.
     */
    protected static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Takes an instance of Game as a parameter.
     *
     * @param game An instance of the Game class utilising this Server Object.
     */
    public Server(Game game) {
        super(game);
        port = 0;
        use_disconnected_sockets = false;
        socket_list = new HashMap<>();
        disconnected_sockets = new ArrayList<String>();
    }

    /**
     * Takes an instance of Game and an Integer for the port number.
     *
     * @param game An instance of the Game class utilising this Server Object.
     * @param port The port number used to connect to a server or listen for
     * clients on.
     */
    public Server(Game game, int port) {
        super(game);
        this.port = port;
        use_disconnected_sockets = false;
        socket_list = new HashMap<>();
        disconnected_sockets = new ArrayList<String>();
    }

    /**
     * Closes the {@link Server}.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        for (SocketThread socket : socket_list.values()) {
            socket.close();
        }
        socket_list = null;
        disconnected_sockets = null;
    }

    /**
     * Sets the port number used for connections.
     *
     * @param port Port number to listen for connections on.
     */
    public void setPort(int port) {
        this.port = port;
        LOGGER.log(Level.INFO, "Changing port number: {0}", port);
    }

    /**
     * Sets whether to keep the hashes of disconnected sockets.
     *
     * @param use Boolean specifying whether to keep the hashes of disconnected
     * sockets.
     */
    public void setUseDisconnectedSockets(boolean use) {
        use_disconnected_sockets = use;
        LOGGER.log(Level.INFO, "Toggling flag use_disconnected_scokets: {0}", use);
    }

    /**
     * Sets the list of connections to the {@link Server} using a
     * pre-constructed HashMap(String,{@link SocketThread}).
     *
     * @param socket_list HashMap({@link SocketThread}) to use as list of
     * {@link Server} connections.
     */
    public void setSocketList(HashMap<String, SocketThread> socket_list) {
        this.socket_list = socket_list;
        LOGGER.log(Level.INFO, "Changing socket_list: {0}", socket_list);
    }

    /**
     * Sets the list of disconnected sockets hashes.
     *
     * @param disconnected_sockets ArrayList{String) to use as list of hashes of
     * disconnected sockets.
     */
    public void setDisconnectedSockets(ArrayList<String> disconnected_sockets) {
        this.disconnected_sockets = disconnected_sockets;
        LOGGER.log(Level.INFO, "Changing disconnected_sockets: {0}", disconnected_sockets);
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
     * @return the HashMap(String,{@link SocketThread}) socket_list, the list of
     * connections to the {@link Server}.
     */
    public HashMap<String, SocketThread> getSocketList() {
        return socket_list;
    }

    /**
     * Closes {@link SocketThread} at specified key of
     * HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param hash Hash Key corresponding to value to remove from socket_list.
     */
    public void removeSocket(String hash) {
        try {
            socket_list.get(hash).close();
            socket_list.remove(hash);
            if (use_disconnected_sockets) {
                disconnected_sockets.add(hash);
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to remove socket: {0}\nSocket details:\n{1}", new Object[]{hash, socket_list.get(hash).toString()});
            LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
        }
    }

    /**
     * Removes the specified hash from disconnected_sockets.
     *
     * @param hash Hash to remove.
     */
    public void removeDisconnectedSocket(String hash) {
        disconnected_sockets.remove(hash);
        LOGGER.log(Level.INFO, "Removed hash from disconnected_sockets: {0}", hash);
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Server}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param socket Pre-constructed {@link Server} to use for connection.
     * @return The hash assigned to the SocketThread.
     */
    public String addSocket(Socket socket) {
        String hash = null;
        try {
            Sock temp_socket = new Sock(socket);
            hash = generateUniqueHash();
            socket_list.put(hash, new SocketThread(temp_socket, this, hash));
            socket_list.get(hash).start();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by Socket, connection was not established\nSocket details: {0}", socket.toString());
            LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
        }
        return hash;
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Sock}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param sock Pre-constructed {@link Sock} to use for connection.
     * @return The hash assigned to the SocketThread.
     */
    public String addSocket(Sock sock) {
        String hash = null;
        try {
            hash = generateUniqueHash();
            socket_list.put(hash, new SocketThread(sock, this, hash));
            socket_list.get(hash).start();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by Sock, connection was not established\nSock details: \n{0}", sock.toString());
            LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
        }
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address.
     *
     * @param ip IP address of {@link Server}.
     * @return The hash assigned to the SocketThread.
     */
    public String addSocket(String ip) {
        String hash = null;
        try {
            Sock temp_socket = new Sock(ip, port);
            hash = generateUniqueHash();
            socket_list.put(hash, new SocketThread(temp_socket, this, hash));
            socket_list.get(hash).start();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by IP, connection was not established\nIP: {0}", ip);
            LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
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
     */
    public String addSocket(String ip, int port) {
        String hash = null;
        try {
            Sock temp_socket = new Sock(ip, port);
            hash = generateUniqueHash();
            socket_list.put(hash, new SocketThread(temp_socket, this, hash));
            socket_list.get(hash).start();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to add or start SocketThread by IP and port, connection was not established\nIP: {0}\nPort: {1}", new Object[]{ip, port});
            LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
        }
        return hash;
    }

    /**
     * Generates a random hash key.
     *
     * @return the generated hash in String form.
     * @throws IOException
     */
    public String generateHash() throws IOException {
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
     * @throws IOException
     */
    public String generateUniqueHash() throws IOException {
        String hash = "";
        boolean exists = true;
        int count = 0;
        while (exists) {
            hash = generateHash();
            exists = checkHash(hash);
            count++;
        }
        LOGGER.log(Level.INFO, "Generated new hash {0}, {1} hashes created before unique hash was found", new Object[]{hash, count});
        return hash;
    }

    /**
     * Checks a Key to see if it exists in socket_list.
     *
     * @param hash Key to check whether it exists.
     * @return true or false depending on whether the Key exists in socket_list.
     * @throws IOException
     */
    public boolean checkHash(String hash) throws IOException {
        boolean exists;
        exists = socket_list.containsKey(hash);
        return exists;
    }

    /**
     * Moves a value in socket_list from one key to another and removes the
     * previously used key.
     *
     * @param old_hash Current key associated with the desired value.
     * @param new_hash New Key to associate with the desired value.
     */
    public void replaceHash(String old_hash, String new_hash) {
        SocketThread socket = socket_list.get(old_hash);
        socket_list.put(new_hash, socket);
        socket_list.remove(old_hash);
        LOGGER.log(Level.INFO, "Moved socket details from hash {0} to hash {1}", new Object[]{old_hash, new_hash});
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
            if (disconnected_sockets.contains(saved_hash)) {
                replaceHash(current_hash, saved_hash);
                removeDisconnectedSocket(saved_hash);
                exists = true;
                LOGGER.log(Level.INFO, "", new Object[]{current_hash, saved_hash});
            }
        }
        return exists;
    }

    /**
     * Function used to start a new thread for listening on a ServerSocket. It
     * is meant to be overridden in child class as the basic {@link Server} does
     * not support the use of ServerSocket. An example of this can be found in
     * {@link ListenServer}.
     */
    public void startThread() {

    }

    /**
     * Function used to accept new client connects through a ServerSocket. It is
     * meant to be overridden in child class as the basic {@link Server} does
     * not support the use of ServerSocket. An example of this can be found in
     * {@link ListenServer}.
     *
     * @return The hash of the newly connected {@link SocketThread}.
     * @throws java.io.IOException
     */
    public String listen() throws IOException {
        return "";
    }

    /**
     * Sends a blank String to every connected socket. If an exception is caught
     * when sending the String it removes the {@link SocketThread} from the
     * socket_list. Useful for checking whether any sockets have disconnected
     * but were not detected as disconnected automatically by the
     * {@link Server}.
     *
     * @throws IOException
     */
    public void pingSockets() throws IOException {
        for (SocketThread socket : socket_list.values()) {
            try {
                socket.getSocket().sendMessage("");
                LOGGER.log(Level.INFO, "Ping successful to hash {0}", socket.getHash());
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Ping unsuccessful to hash {0}, attempting to disconnect socket", socket.getHash());
                LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
                removeSocket(socket.getHash());
            }
        }
    }

    /**
     * Puts the attribute states of {@link Server} in readable form.
     *
     * @return Attributes of {@link Server} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = "Server attribute values:" + "\n\tPort: " + port + "\n\tSocket List:";
        for (SocketThread socket : socket_list.values()) {
            to_string += "\n" + socket.toString("\t");
        }
        return to_string;
    }

    /**
     * Puts the attribute states of {@link Server} in readable form.
     * Takes String input to assist formatting. Useful to add special characters
     * to assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link Server} in a readable String form.
     */
    public String toString(String ch) {
        String to_string = ch + "Server attribute values:\n" + ch + "\tPort: " + port + "\n" + ch + "\tSocket List:";
        for (SocketThread socket : socket_list.values()) {
            to_string += "\n" + socket.toString(ch + "\t");
        }
        return to_string;
    }

    /**
     * Used to send a message to a list of sockets. Takes the String to send and a List of the hashes associated with the sockets to send to as input.
     * 
     * @param message The String to send.
     * @param clientIds The List of hashes to send to.
     */
    @Override
    protected void sendMessage(String message, List<String> clientIds) {
        for (String hash : clientIds) {
            if (socket_list.containsKey(hash)) {
                try {
                    socket_list.get(hash).getSocket().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Could not send message through socket with hash: {0}\nMessage was: {1}\nSocket data:\n{2}", new Object[]{hash, message, socket_list.get(hash).toString()});
                    LOGGER.log(Level.INFO, "Stack trace of caught exception: {0}", e);
                }
            }
        }
    }
}