package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Game;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * This class is an extension of the base {@link Server} class. While the
 * {@link Server} class is more intended to handle a basic client socket
 * connection, the {@link ListenServer} class extends upon {@link Server}'s
 * basic functionality to extend the more server based connection methods of the
 * {@link Server} class. It allows for the creation of a threaded ServerSocket
 * to accept client connections by Overriding the listen() and startThread()
 * methods.
 *
 * @author javu
 */
public class ListenServer extends Server {

    /**
     * The threaded class used to hold a ServerSocket based connection and
     * interface with it.
     */
    protected ListenThread listen_thread;

    /**
     * Takes the instance of {@link Game} that created this instance of
     * {@link ListenServer}.
     *
     * @param game The {@link Game} class that created the {@link ListenServer}.
     * @throws IOException
     */
    public ListenServer(Game game) throws IOException {
        super(game);
        listen_thread = new ListenThread(this);
    }

    /**
     * Takes the instance of {@link Game} that created this instance of
     * {@link ListenServer} and a port number used to listen for new connections
     * on.
     *
     * @param game The {@link Game} class that created the {@link ListenServer}.
     * @param port The port number used to listen for new client connections.
     * @throws IOException
     */
    public ListenServer(Game game, int port) throws IOException {
        super(game, port);
        listen_thread = new ListenThread(this);
    }

    /**
     * Gracefully closes listen_thread, all the {@link SocketThread}'s in
     * socket_list and sets all attributes to null.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        listen_thread.close();
        for (SocketThread socket : socket_list.values()) {
            socket.close();
        }
        socket_list = null;
        disconnected_sockets = null;
    }

    /**
     * Blocks while waiting for a new client connection. Once the client
     * connection is received a new {@link SocketThread} is created and
     * initialized to allow interfacing with the newly connected client.
     *
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    @Override
    public String listen() throws IOException {
        Socket temp_socket = listen_thread.getServerSocket().accept();
        Sock temp_sock = new Sock(temp_socket);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_sock, this, hash));
        socket_list.get(hash).start();
        LOGGER.log(Level.INFO, "New client connected to ListenServer. Connection was stored in socket_list");
        return hash;
    }

    /**
     * The function used to start the thread listen_thread.
     */
    @Override
    public void startThread() {
        listen_thread.start();
        LOGGER.log(Level.INFO, "Started listen_thread");
    }
    
    /**
     * Puts the attribute states of {@link ListenServer} in readable form.
     *
     * @return Attributes of {@link ListenServer} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = "ListenServer attribute values:" + "\n\tPort: " + port + "\n\tListenThread:\n" + listen_thread.toString() + "\n\tSocket List:";
        for (SocketThread socket : socket_list.values()) {
            to_string += "\n" + socket.toString("\t");
        }
        return to_string;
    }

    /**
     * Puts the attribute states of {@link ListenServer} in readable form.
     * Takes String input to assist formatting. Useful to add special characters
     * to assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link ListenServer} in a readable String form.
     */
    @Override
    public String toString(String ch) {
        String to_string = ch + "ListenServer attribute values:\n" + ch + "\tPort: " + port + "\n" + ch + "\tListenThread:\n" + listen_thread.toString() + "\n" + ch + "\tSocket List:";
        for (SocketThread socket : socket_list.values()) {
            to_string += "\n" + socket.toString(ch + "\t");
        }
        return to_string;
    }

}