/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fantasyteam.sw2.networking;

import java.io.IOException;

/**
 *
 * @author javu
 */
public class LobbyThread extends ListenThread{
    
    public LobbyThread(LobbyServer s) throws IOException{
        super(s);
    }
    
    @Override
    public void run() {
        int run = 1;
        boolean player_added = false;
        LobbyServer server = new LobbyServer();
        server.printUI();
        while (run == 1) {
            // Block while awaiting connections. When a connection is made create a new SocketThread and start the thread
            if (server.getSocketList().size() < server.getMaxPlayers()) {
                String new_hash = "";
                try {
                    new_hash = server.listen();
                    server.getSocketData().put(new_hash, new SocketData(new_hash, "", 1, 1, 0));
                    if (!player_added) {
                        server.setHostHash(new_hash);
                    }
                    player_added = true;
                } catch (IOException e) {
                    System.out.println("Could not listen");
                }
            } else {

            }
        }
    }
}
