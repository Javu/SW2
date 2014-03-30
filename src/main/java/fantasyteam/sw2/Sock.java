package fantasyteam.sw2;

import java.net.*;
import java.io.*;

/**
 * The {@link Sock} class is essentially a structure used to hold a Socket and an input and output stream to send and received messages through the Socket.
 * @author javu
 */
public class Sock {


    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Basic Constructor
    Sock() {
        socket = null;
        out = null;
        in = null;
    }

    /**
     * Closes the {@link Sock}
     * @throws IOException
     */
    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }

    // Overloaded constructor, takes a Socket as an argument.
    Sock(Socket s) throws IOException {
        socket = s;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Overloaded constructor, takes a String for an IP address and an int for a port number as arguments.
    Sock(String ip, int p) throws IOException {
        socket = new Socket(ip, p);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Overloaded constructor, takes an int for a port as arguments. Used to create a ServerSocket based connection.
    Sock(int p) throws IOException {
        ServerSocket server_socket = new ServerSocket(p);
        socket = server_socket.accept();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            String message = "";
            message = in.readLine();
        return message;
    }
}
