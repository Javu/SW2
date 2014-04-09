package fantasyteam.ft1.networkingbase;



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The {@link SocketThread} class is used to hold socket based connections and control interaction with the socket.
 * It extends the thread interface to allow multiple {@link SocketThreads} to be run at once allowing for more seamless, concurrent connections when multiple socket based connections need to be made.
 * @author javu
 */
public class SocketThread extends Thread {

    private Sock socket;
    private Server server;
    private String hash;
    
    private static final Logger LOGGER = Logger.getLogger(SocketThread.class.getName());

    public SocketThread(Sock inSock, Server ser, String h) {
        // Initialise attributes
        socket = inSock;
        server = ser;
        hash = h;
    }

    @Override
    public void run() {
        int run = 1;
        while (run == 1) {
            String message = "";
            // Block and wait for input from the socket
            try {
                message = socket.readMessage();
                if (message==null) {
                    LOGGER.log(Level.INFO, "Socket has been disconnected, attempting to close socket on Server");
                    server.removeSocket(hash);
                } else {
                    LOGGER.log(Level.INFO, "Message received: {0}", message);
                    server.receiveMessage(message,hash);
                }
            } catch (IOException e) {
                
            }
        }
    }

    /**
     * Closes the {@link SocketThread}.
     * @throws IOException
     */
    public void close() throws IOException {
        // Close the socket and interrupt the thread
        socket.close();
        this.interrupt();
    }

    /**
     * Returns the attribute socket.
     * @return the {@link Sock} socket.
     */
    public Sock getSocket() {
        return socket;
    }

    /**
     * Returns the attribute hash.
     * @return the String hash.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the attribute socket with a pre-constructed {@link Sock}.
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
     * @param hash String to set hash to.
     */
    public void setHash(String hash) {
        this.hash = hash;
        LOGGER.log(Level.INFO, "Changed hash: {0}", hash);
    }

    /**
     * Prints attribute states of {@link SocketThread} in readable form to System.out.
     * @return 
     */
    @Override
    public String toString() {
        String to_string = "Socket Thread attribute values:\n\tHash: " + hash;
        return to_string;
    }

    /**
     * Prints attribute states of {@link Server} in readable form to System.out. Takes String input to assist formatting.
     * Useful to add special characters to assist formatting such as \t or \n.
     * @param ch Adds the String ch to the start of each printed line.
     * @return 
     */
    public String toString(String ch) {
        String to_string = ch + "Socket Thread attribute values:\n" + ch + "\tHash: " + hash;
        return to_string;
    }
}
