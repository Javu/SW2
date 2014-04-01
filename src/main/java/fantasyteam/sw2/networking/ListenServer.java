package fantasyteam.sw2.networking;

import java.net.ServerSocket;

/**
 *
 * @author javu
 */
public class ListenServer extends Server {

    ServerSocket server_socket;

    public ListenServer() {
        super();
        server_socket = null;
    }

    public ListenServer(int p) {
        super(p);
        server_socket = null;
    }
}