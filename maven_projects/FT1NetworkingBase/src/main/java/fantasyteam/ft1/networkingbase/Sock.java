package fantasyteam.ft1.networkingbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.in;
import static java.lang.System.out;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link Sock} class is essentially a structure used to hold a Socket and
 * an input and output stream to send and received messages through the Socket.
 *
 * @author javu
 */
public class Sock {

    /**
     * Socket used to hold the connection.
     */
    private Socket socket;
    /**
     * Output stream for the socket. Used to send information through the
     * connected socket.
     */
    private PrintWriter out;
    /**
     * Input stream for the socket. Used to receive information through the
     * connected socket.
     */
    private BufferedReader in;

    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(Sock.class.getName());

    /**
     * Default constructor.
     */
    public Sock() {
        socket = null;
        out = null;
        in = null;
    }

    /**
     * Takes a Socket as an argument.
     *
     * @param socket socket used to construct the Sock with.
     * @throws java.io.IOException
     */
    public Sock(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(this.socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        LOGGER.log(Level.INFO, "Successfully created Socket using preconstructed Socket");
    }

    /**
     * Takes a String for an IP address and an int for a port number as
     * arguments.
     *
     * @param ip IP address to connect the socket to.
     * @param port Port number to connect to.
     * @throws java.io.IOException
     */
    public Sock(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        LOGGER.log(Level.INFO, "Successfully created Socket connected to IP {0} on port {1}", new Object[]{ip, port});
    }

    /**
     * Closes the {@link Sock}.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        LOGGER.log(Level.INFO, "Attempting to close Sock");
        if(socket != null) {
           socket.close();
           socket = null; 
        }
        in = null;
        out = null;
    }

    /**
     * Returns the value of socket.
     * 
     * @return the Socket socket.
     */
    public Socket getSocket() {
        return socket;
    }
    
    /**
     * Returns the value of out.
     * 
     * @return the PrintWriter out.
     */
    public PrintWriter getOut() {
        return out;
    }
    
    /**
     * Returns the value of in.
     * 
     * @return the BufferedReader in.
     */
    public BufferedReader getIn() {
        return in;
    }
    
    /**
     * Sends a string message through the PrintWriter {@link out}.
     *
     * @param message String message to send through the connection.
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        out.println(message);
        LOGGER.log(Level.INFO, "Sent message {0}", message);
    }

    /**
     * Reads message received through the BufferedReader {@link in}.
     *
     * @return message received through connection.
     * @throws IOException
     */
    public String readMessage() throws IOException {
        String message = in.readLine();
        LOGGER.log(Level.INFO, "Read message {0}", message);
        return message;
    }
    
    /**
     * Puts the attribute states of {@link Sock} in readable form.
     *
     * @return Attributes of {@link Sock} in a readable String form.
     */
    @Override
    public String toString() {
        String to_string = toString("");
        return to_string;
    }
    
    /**
     * Puts the attribute states of {@link Sock} in readable form. Takes
     * String input to assist formatting. Useful to add special characters to
     * assist formatting such as \t or \n.
     *
     * @param ch Adds the String ch to the start of each line in the String.
     * @return Attributes of {@link Sock} in a readable String form.
     */
        public String toString(String ch) {
        String to_string = ch + "socket: " + socket.toString() + "\n" + ch + "out: " + out.toString() + "\n" + ch + "in: " + in.toString();
        return to_string;
    }
}
