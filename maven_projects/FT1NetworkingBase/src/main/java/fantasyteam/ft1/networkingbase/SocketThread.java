package fantasyteam.ft1.networkingbase;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link SocketThread} class is used to hold socket based connections and
 * control interaction with the socket. It extends the Thread class to allow
 * multiple {@link SocketThreads} to be run at once allowing for more seamless,
 * concurrent connections when multiple socket based connections need to be
 * made.
 *
 * @author javu
 */
public class SocketThread extends Thread {

    /**
     * {@link Sock} used to hold Socket connection and interface with it.
     */
    private Sock socket;
    /**
     * Instance of the {@link Server} class that created this thread.
     */
    private Server server;
    /**
     * The hash String given to this socket by the {@link Server}.
     */
    private String hash;

    /**
     * Boolean used to determine if the thread is running or not.
     */
    private boolean run;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(SocketThread.class.getName());

    /**
     * Takes the {@link Sock} instance of the socket to interface with, The
     * instance of {@link Server} that created this thread and the hash String
     * assigned to this thread by the {@link Server}.
     *
     * @param sock {@link Sock} used to hold the socket connection this {@link SocketThread} interfaces with.
     * @param server {@link Server} that created this {@link SocketThread}. Mainly used to handle/parse messages received into meaningful actions.
     * @param hash String representing the unique hash associated with this {@link SocketThread} by the {@link Server} that created it.
     */
    public SocketThread(Sock sock, Server server, String hash) {
        // Initialise attributes
        socket = sock;
        this.server = server;
        this.hash = hash;
        run = true;
    }

    /**
     * Loop that blocks while it reads new messages from the socket. If a
     * message is received it passes the message back to the {@link Server} to
     * handle.
     */
    @Override
    public void run() {
        while (run) {
            String message = "";
            // Block and wait for input from the socket
            try {
                message = socket.readMessage();
                if (message == null) {
                    LOGGER.log(Level.INFO, "Socket has been disconnected, attempting to close socket on Server");
                } else {
                    LOGGER.log(Level.INFO, "Message received: {0}", message);
                }
                if (run) {
                    server.receiveMessage(message, hash);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not read from socket or read null, attempting to close socket on Server");
                server.receiveMessage(null, hash);
                run = false;
            }
        }
        LOGGER.log(Level.INFO, "Socket loop has exited");
        try {
            close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close and interrupt SocketThread. Thread may not have terminated correctly and could be tieing up system resources. Stack trace:", e);
        }
    }

    /**
     * Closes the {@link SocketThread}.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if(socket != null) {
            try{
                socket.close();
                socket = null;
                LOGGER.log(Level.INFO, "Successfully closed Sock socket");    
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close Sock socket in SocketThread");
                throw new IOException("Failed to close Sock socket in SocketThread");
            }
        }
        LOGGER.log(Level.INFO, "SocketThread has succesffully closed");
        this.interrupt();
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
     * Returns the attribute hash.
     *
     * @return the String hash.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns the attribute run.
     *
     * @return the boolean run.
     */
    public boolean getRun() {
        return run;
    }

    /**
     * Sets the attribute socket with a pre-constructed {@link Sock}.
     *
     * @param sock Pre-constructed {@link Sock} to set socket to.
     * @throws IOException
     */
    public void setSocket(Sock sock) throws IOException {
        if (socket != null) {
            socket.close();
        }
        socket = sock;
        LOGGER.log(Level.INFO, "Changed Sock used as connection. New Sock details:\n{0}", socket.toString());
    }

    /**
     * Sets the attribute hash.
     *
     * @param hash String to set hash to.
     */
    public void setHash(String hash) {
        this.hash = hash;
        LOGGER.log(Level.INFO, "Changed hash: {0}", hash);
    }

    /**
     * Sets the attribute run. Setting run to false while the thread is started
     * will cause the thread to close.
     *
     * @param run boolean to set run to.
     */
    public void setRun(boolean run) {
        this.run = run;
    }

    public void unblock() throws IOException {
        socket.close();
        socket = null;
        LOGGER.log(Level.INFO, "Successfully closed Sock socket");  
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
        String to_string = ch + "Hash: " + hash + "\n" + ch + "Running: " + run;
        to_string += "\n" + ch + "Sock:";
        if(socket != null && socket.getSocket() != null) {
            to_string += "\n" + socket.toString(ch + "\t");
        } else {
            to_string += " Sock has been closed";
        }
        return to_string;
    }
}
