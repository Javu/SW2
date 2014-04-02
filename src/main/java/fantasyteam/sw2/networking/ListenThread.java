/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fantasyteam.sw2.networking;

import java.io.IOException;
import java.net.ServerSocket;

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
                server.listen();
            } catch (IOException e) {

            }
        }
    }

    public ServerSocket getServerSocket() {
        return server_socket;
    }
}
