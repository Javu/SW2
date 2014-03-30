import java.io.*;

/**
 * The {@link SocketThread} class is used to hold socket based connections and control interaction with the socket.
 * It extends the thread interface to allow multiple {@link SocketThreads} to be run at once allowing for more seamless, concurrent connections when multiple socket based connections need to be made.
 * @author javu
 */
public class SocketThread extends Thread {

    private Sock socket;
    private Server server;
    private String hash;

    SocketThread(Sock inSock, Server ser, String h) {
        // Initialise attributes
        socket = inSock;
        server = ser;
        hash = h;
        //System.out.println("New Socket Thread Created");
    }

    @Override
    public void run() {
        int run = 1;
        while (run == 1) {
            String message = "";
            // Block and wait for input from the socket
            try {
                message = socket.readMessage();
                if (message.compareTo("-1") == 0) {
                    try {
                        server.removeSocket(hash);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    server.handleMessage(message,hash);
                }
            } catch (IOException e) {
                // If socket is closed pass special command to message handler and end thread loop
                System.out.println(e.getMessage());
                try {
                    close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                run = 0;
            }
        }
    }

    /**
     * Closes the {@link SocketThread}.
     * @throws IOException
     */
    public void close() throws IOException {
        //System.out.println("Closing Socket Thread DICKHEAD");
        // Close the socket, remove sockThread from sockets Vector and interrupt the thread
        socket.close();
        server.getSocketList().remove(hash);
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
     * @param s Pre-constructed {@link Sock} to set socket to.
     * @throws IOException
     */
    public void setSocket(Sock s) throws IOException {
        if (socket != null) {
            socket.close();
        }
        socket = s;
    }

    /**
     * Sets the attribute hash.
     * @param h String to set hash to.
     */
    public void setHash(String h) {
        hash = h;
    }

    /**
     * Prints attribute states of {@link SocketThread} in readable form to System.out.
     * @throws java.io.IOException
     */
    public void print() throws IOException {
        System.out.println("Socket Thread attribute values:");
        System.out.println("\tHash: " + hash);
    }

    /**
     * Prints attribute states of {@link Server} in readable form to System.out. Takes String input to assist formatting.
     * Useful to add special characters to assist formatting such as \t or \n.
     * @param ch Adds the String ch to the start of each printed line.
     * @throws java.io.IOException
     */
    public void print(String ch) throws IOException {
        System.out.println(ch + "Socket Thread attribute values:");
        System.out.println(ch + "\tHash: " + hash);
    }
}
