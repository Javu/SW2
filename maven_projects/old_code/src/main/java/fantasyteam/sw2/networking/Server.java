package fantasyteam.sw2.networking;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
public class Server {

    /* Port number */
    protected int port;
    /* Boolean used to specify whether to keep the hashes of disconnected sockets */
    protected boolean use_disconnected_sockets;
    /* HashMap used to hold {@link SocketThread}s and the hashed keys to associate them with */
    protected HashMap<String, SocketThread> socket_list;
    /* ArrayList used to keep the hashes of disconnected sockets */
    protected ArrayList<String> disconnected_sockets;

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /* Default Constructor */
    public Server() {
        port = 0;
        use_disconnected_sockets = false;
        socket_list = new HashMap<>();
        disconnected_sockets = new ArrayList<String>();
    }

    public Server(int p) {
        port = p;
        use_disconnected_sockets = false;
        socket_list = new HashMap<>();
        disconnected_sockets = new ArrayList<String>();
    }

    /**
     * Closes the {@link Server}.
     *
     * @throws java.io.IOException
     * @throws RuntimeException if there is an error finding the
     * {@link SocketThread} key
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
     * @param p Port number to listen for connections on.
     */
    public void setPort(int p) {
        port = p;
    }

    /**
     * Sets whether to keep the hashes of disconnected sockets.
     *
     * @param use Boolean specifying whether to keep the hashes of disconnected
     * sockets.
     */
    public void setUseDisconnectedSockets(boolean use) {
        use_disconnected_sockets = use;
    }

    /**
     * Sets the list of connections to the {@link Server} using a
     * pre-constructed HashMap(String,{@link SocketThread}).
     *
     * @param sl HashMap({@link SocketThread}) to use as list of {@link Server}
     * connections.
     */
    public void setSocketList(HashMap<String, SocketThread> sl) {
        socket_list = sl;
    }

    /**
     * Sets the list of disconnected sockets hashes.
     *
     * @param ds ArrayList<String> to use as list of hashes of disconnected
     * sockets.
     */
    public void setDisconnectedSockets(ArrayList<String> ds) {
        disconnected_sockets = ds;
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
     * @return the ArrayList<String> of hashes of disconnected sockets.
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
     * @throws IOException
     */
    public void removeSocket(String hash) throws IOException {
        socket_list.get(hash).close();
        socket_list.remove(hash);
        if (use_disconnected_sockets) {
            disconnected_sockets.add(hash);
        }
    }

    /**
     * Removes the specified hash from disconnected_sockets.
     *
     * @param hash Hash to remove.
     */
    public void removeDisconnectedSocket(String hash) {
        disconnected_sockets.remove(hash);
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Server}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param s Pre-constructed {@link Server} to use for connection.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String addSocket(Socket s) throws IOException {
        Sock temp_socket = new Sock(s);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_socket, this, hash));
        socket_list.get(hash).start();
        return hash;
    }

    /**
     * Creates a new {@link SocketThread} using a pre-constructed {@link Sock}
     * and adds it to HashMap(String,{@link SocketThread}) socket_list.
     *
     * @param s Pre-constructed {@link Sock} to use for connection.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String addSocket(Sock s) throws IOException {
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(s, this, hash));
        socket_list.get(hash).start();
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address.
     *
     * @param ip IP address of {@link Server}.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String addSocket(String ip) throws IOException {
        Sock temp_socket = new Sock(ip, port);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_socket, this, hash));
        socket_list.get(hash).start();
        return hash;
    }

    /**
     * Attempts to connect to another {@link Server} via IP address and port
     * number.
     *
     * @param ip IP address of {@link Server}.
     * @param p Port number of {@link Server}.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String addSocket(String ip, int p) throws IOException {
        Sock temp_socket = new Sock(ip, p);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_socket, this, hash));
        socket_list.get(hash).start();
        return hash;
    }

    /**
     * Generates a random hash Key.
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
        return hash;
    }

    /**
     * Generates a unique, random hash Key.
     *
     * @return the generated unique hash in String form.
     * @throws IOException
     */
    public String generateUniqueHash() throws IOException {
        String hash = "";
        boolean exists = true;
        while (exists) {
            hash = generateHash();
            exists = checkHash(hash);
        }
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
     * @throws IOException
     */
    public void replaceHash(String old_hash, String new_hash) throws IOException {
        SocketThread socket = socket_list.get(old_hash);
        socket_list.put(new_hash, socket);
        socket_list.remove(old_hash);
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
            } catch (IOException e) {
                removeSocket(socket.getHash());
            }
        }
    }

    /**
     * Interprets messages received from {@link Server}. Function is meant to be
     * overridden in child class.
     *
     * @param message Message received from {@link Server} that needs to be
     * handled.
     */
    public void handleMessage(String message) {

    }

    /**
     * Interprets messages received from {@link Server}. Function is meant to be
     * overridden in child class.
     *
     * @param message Message received from {@link Server} that needs to be
     * handled.
     * @param h Hash of the {@link SocketThread} the message was received by.
     */
    public void handleMessage(String message, String h) {

    }

    /**
     * Prints attribute states of {@link Server} in readable form to System.out.
     *
     * @throws java.io.IOException
     */
    public void print() throws IOException {
        System.out.println("Server attribute values:");
        System.out.println("\tPort: " + port);
        System.out.println("\tSocket List:");
        for (SocketThread socket : socket_list.values()) {
            socket.print("\t");
        }
    }

    /**
     * Prints attribute states of {@link Server} in readable form to System.out.
     * Takes String input to assist formatting. Useful to add special characters
     * to assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each printed line.
     * @throws java.io.IOException
     */
    public void print(String ch) throws IOException {
        System.out.println(ch + "Server attribute values:");
        System.out.println(ch + "\tPort: " + port);
        System.out.println(ch + "\tSocket List:");
        for (SocketThread socket : socket_list.values()) {
            socket.print(ch + "\t");
        }
    }
}
