/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fantasyteam.sw2.networking;

import java.net.ServerSocket;

/**
 *
 * @author javu
 */
public class ServerListen extends Server{
    ServerSocket server_socket;
    
    public ServerListen(){
        super();
        server_socket = null;
    }
    
    public ServerListen(int p){
        super(p);
        server_socket = null;
    }
    
    
}
