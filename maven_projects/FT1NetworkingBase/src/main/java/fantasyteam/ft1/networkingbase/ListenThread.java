package fantasyteam.ft1.networkingbase;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link ListenThread} class is used to implement a ServerSocket and accept
 * new connections to the socket by clients. It extends the Thread class to
 * allow it to block for input and wait for new clients to connect without
 * blocking the normal execution of the {@link Server} instance that created it.
 */

public class ListenThread extends Thread {

    /**
     * ServerSocket used to accept new client connections.
     */
    private ServerSocket server_socket;
    /**
     * The instance of {@link Server} that created this instance of {@link ListenThread}.
     */
    private Server server;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(ListenThread.class.getName());
    
    /**
     * Takes the instance of {@link Server} that created this ListenThread as a parameter.
     * 
     * @param server
     * @throws IOException 
     */
    public ListenThread(Server server) throws IOException {
        this.server = server;
        server_socket = new ServerSocket(this.server.getPort());
    }

    /**
     * Loop that blocks while it accepts new connections through server_socket.
     */
    @Override
    public void run() {
        int run = 1;
        while (run == 1) {
            try {
                server.listen();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to accept new connection. Stack trace: {0}", e);
            }
        }
    }

    /**
     * Closes server_socket and interrupts the thread.
     * @throws IOException 
     */
    public void close() throws IOException {
        server_socket.close();
        server_socket = null;
        this.interrupt();
        
    }

    /**
     * Returns server_socket.
     * 
     * @return The ServerSocket attribute server_socket.
     */
    public ServerSocket getServerSocket() {
        return server_socket;
    }
}
