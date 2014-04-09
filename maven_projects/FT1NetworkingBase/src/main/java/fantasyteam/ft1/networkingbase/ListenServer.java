package fantasyteam.ft1.networkingbase;



import fantasyteam.ft1.Game;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author javu
 */
public class ListenServer extends Server {

    protected ListenThread listen_thread;

    public ListenServer(Game game) throws IOException {
        super(game);
        listen_thread = new ListenThread(this);
    }

    public ListenServer(Game game, int p) throws IOException {
        super(game, p);
        listen_thread = new ListenThread(this);
    }
    
    @Override
    public void close() throws IOException{
        listen_thread.close();
    }
    
    /**
     * Creates a ServerSocket and puts {@link Server} into listen mode.
     * @return The hash assigned to the SocketThread.
     * @throws IOException
     */
    @Override
    public String listen() throws IOException {
        Socket temp_socket = listen_thread.getServerSocket().accept();
        Sock temp_sock = new Sock(temp_socket);
        String hash = generateUniqueHash();
        socket_list.put(hash, new SocketThread(temp_sock, this, hash));
        socket_list.get(hash).start();
        return hash;
    }
    
    @Override
    public void startThread(){
        listen_thread.start();
    }

}
