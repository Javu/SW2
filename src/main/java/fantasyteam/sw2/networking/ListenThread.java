/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fantasyteam.sw2.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author javu
 */
public class ListenThread extends Thread {

    private ServerSocket server_socket;
    private Server server;

    public ListenThread(Server s) throws IOException {
        server = s;
        server_socket = new ServerSocket(server.getPort());
    }

    @Override
    public void run() {
        int run = 1;
        while (run == 1) {
            try {
                listen();
            } catch (IOException e) {

            }
        }
    }

    /**
     * Creates a ServerSocket and puts {@link Server} into listen mode.
     *
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String listen() throws IOException {
        Socket temp_socket = server_socket.accept();
        Sock temp_sock = new Sock(temp_socket);
        String hash = server.generateUniqueHash();
        server.getSocketList().put(hash, new SocketThread(temp_sock, server, hash));
        server.getSocketList().get(hash).start();
        return hash;
    }

}
