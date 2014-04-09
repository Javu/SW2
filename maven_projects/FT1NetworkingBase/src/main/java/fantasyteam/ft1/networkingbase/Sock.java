package fantasyteam.ft1.networkingbase;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.in;
import static java.lang.System.out;
import java.net.Socket;

/**
 * The {@link Sock} class is essentially a structure used to hold a Socket and an input and output stream to send and received messages through the Socket.
 * @author javu
 */
public class Sock {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Basic Constructor
    public Sock() {
        socket = null;
        out = null;
        in = null;
    }

    // Overloaded constructor, takes a Socket as an argument.
    public Sock(Socket s) throws IOException {
        socket = s;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Overloaded constructor, takes a String for an IP address and an int for a port number as arguments.
    public Sock(String ip, int p) throws IOException {
        socket = new Socket(ip, p);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Closes the {@link Sock}
     * @throws IOException
     */
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }

    /**
     * Sends a string message through the PrintWriter {@link out}.
     * @param message String message to send through the connection.
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    /**
     * Reads message received through the BufferedReader {@link in}.
     * @return message received through connection.
     * @throws IOException
     */
    public String readMessage() throws IOException {
        return in.readLine();
    }
    
    public String toString(){
        String to_string = "socket: "+socket.toString()+"\nout: "+out.toString()+"\nin: "+in.toString();
        return to_string;
    }
}
