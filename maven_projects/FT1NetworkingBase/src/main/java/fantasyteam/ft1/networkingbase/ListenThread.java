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
    private int port;
    /**
     * Boolean used to determine if the thread is running or not.
     */
    private boolean run;
    
    
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
        port = this.server.getPort();
        server_socket = new ServerSocket(port);
        run = false;
        LOGGER.log(Level.INFO, "Constructed new ListenThread on port {0}", port);
    }

    /**
     * Loop that blocks while it accepts new connections through server_socket.
     */
    @Override
    public void run() {
        run = true;
        while (run) {
            try {
                server.listen();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to accept new connection. ListenThread will now terminate", e);
                run = false;
            }
        }
        LOGGER.log(Level.INFO, "Listen loop has exited on port {0}", port);
        try {
            this.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to close and interrupt ListenThread. Thread may not have terminated correctly and could be tieing up system resources", e);
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
        LOGGER.log(Level.INFO, "Closed ListenThread on port {0}", port);
    }

    /**
     * Returns server_socket.
     * 
     * @return The ServerSocket attribute server_socket.
     */
    public ServerSocket getServerSocket() {
        return server_socket;
    }
    
    public int getPort() {
        return port;
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
     * Sets the attribute run. Setting run to false while the thread is started
     * will cause the thread to close.
     *
     * @param run boolean to set run to.
     */
    public void setRun(boolean run) {
        this.run = run;
    }
}
