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
public class ListenServer extends Server {

    protected ListenThread listen_thread;

    public ListenServer() throws IOException {
        super();
        listen_thread = new ListenThread(this);
    }

    public ListenServer(int p) throws IOException {
        super(p);
        listen_thread = new ListenThread(this);
    }
    
    /**
     * Creates a ServerSocket and puts {@link Server} into listen mode.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    public String listen() throws IOException {
        Socket temp_socket = listen_thread.getServerSocket().accept();
        Sock temp_sock = new Sock(temp_socket);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_sock, this, hash));
        socket_list.get(hash).start();
        return hash;
    }
    
    public void startThread(){
        listen_thread.start();
    }

}
