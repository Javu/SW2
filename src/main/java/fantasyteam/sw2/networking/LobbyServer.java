package fantasyteam.sw2.networking;


import java.io.IOException;
import java.util.HashMap;

public class LobbyServer extends Server {

    private int max_players;
    private int game_type;
    private int use_teams;
    private String host_hash;
    private String game_map;
    private HashMap<String, SocketData> socket_data;
    private int game_state;

    public static void main(String args[]) {
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
                    server.socket_data.put(new_hash, new SocketData(new_hash, "", 1, 1, 0));
                    if (!player_added) {
                        server.host_hash = new_hash;
                    }
                    player_added = true;
                } catch (IOException e) {
                    System.out.println("Could not listen");
                }
            } else {

            }
        }
    }

    public LobbyServer() {
        super(23231);
        max_players = 8;
        game_type = 0;
        use_teams = 0;
        host_hash = "";
        game_map = "";
        socket_data = new HashMap<String, SocketData>();
        game_state = 0;
    }

    public LobbyServer(int players) {
        super(23231);
        max_players = players;
        game_type = 0;
        use_teams = 0;
        host_hash = "";
        game_map = "";
        socket_data = new HashMap<String, SocketData>();
        game_state = 0;
    }

    public void setGameMap(String gm) {
        game_map = gm;
    }

    public void setGameState(int gs) {
        game_state = gs;
    }

    public int getMaxPlayers() {
        return max_players;
    }

    public String getGameMap() {
        return game_map;
    }

    public int getGameSate() {
        return game_state;
    }

    public void printUI() {
        int num_players = 0;
        for (SocketThread socket : socket_list.values()) {
            num_players++;
        }
        if (game_state == 0) {
            System.out.println("Square Wars game server version 0.1 ALPHA - Using port " + port);
            System.out.println("-----------------------------------------");
            System.out.println();
            System.out.println("-Current Game Information-");
            System.out.println("Game Type:\t" + game_type);
            System.out.println("Map:\t\t" + game_map);
            System.out.println("Use Teams:\t" + use_teams);
            System.out.println("Max Players:\t" + max_players);
            System.out.println();
            System.out.println("-Connected players " + num_players + "/" + max_players + "-");
            for (SocketData socket : socket_data.values()) {
                System.out.println(socket.toString());
            }
        }
    }

    /**
     *
     * @param message
     * @param hash
     */
    @Override
    public void handleMessage(String message, String hash) {
        int option = 0;
        String text = "";
        String opt = "";
        for (char n : message.toCharArray()) {
            // If a special command is found extract the attributes of the command
            if (option != 0) {
                text += n;
            } else {
                if (Character.toString(n).compareTo(" ") == 0) {
                    option = -1;
                } else {
                    opt += n;
                }
            }
        }
        if (game_state == 0) {

            if (opt.compareTo("/player") == 0) {
                option = 1;
            } else if (opt.compareTo("/team") == 0) {
                option = 2;
            } else if (opt.compareTo("/kick") == 0) {
                option = 3;
            } else if (opt.compareTo("/game") == 0) {
                option = 4;
            } else if (opt.compareTo("/score") == 0) {
                option = 5;
            } else if (opt.compareTo("/time") == 0) {
                option = 6;
            } else if (opt.compareTo("/useteams") == 0) {
                option = 7;
            } else if (opt.compareTo("/start") == 0) {
                option = 8;
            } else if (opt.compareTo("/close") == 0) {
                option = 9;
            } else if (opt.compareTo("/help") == 0) {
                option = 10;
            } else if (opt.compareTo("/map") == 0) {
                option = 11;
            } else if (opt.compareTo("/name") == 0) {
                option = 12;
            }

            if (option == 1) {
                try {
                    socket_list.get(hash).getSocket().sendMessage("/send Command not yet implemented");
                } catch (IOException e) {

                }
            } else if (option == 2) {
                int num = Integer.parseInt(text);
                socket_data.get(hash).setTeam(num);
                for (SocketThread socket : socket_list.values()) {
                    try {
                        socket.getSocket().sendMessage("/send Player " + hash + " changed to team " + text);
                    } catch (IOException e) {

                    }
                }
            } else if (option == 12) {
                boolean name_taken = false;
                for (SocketData socket : socket_data.values()) {
                    if (socket.getPlayerName().compareTo(text) == 0) {
                        name_taken = true;
                    }
                }
                if (!name_taken) {
                    try {
                        socket_list.get(hash).getSocket().sendMessage("/send Name is already taken");
                    } catch (IOException e) {

                    }
                } else {
                    socket_data.get(hash).setPlayerName(text);
                    for (SocketThread socket : socket_list.values()) {
                        try {
                            socket.getSocket().sendMessage("/send Player " + hash + " changed name to " + text);
                        } catch (IOException e) {

                        }
                    }
                }
            } // Host commands
            else if (option == 3 || option == 4 || option == 5 || option == 6 || option == 7 || option == 8 || option == 11) {
                if (hash.compareTo(host_hash) == 0) {
                    // Kick Command. Kicks selected player based on index - TO BE IMPLEMENTED FULLY
                    /*
                     if(option == 3)
                     {
                     // Close the socket, remove sockThread from sockets Vector and interrupt the thread
                     int num = Integer.parseInt(text);
                     if(num == 0)
                     {
                     socket_list.get(hash).getSocket().out.println("You can't kick yourself");
                     }
                     else
                     {
                     try
                     {
                     socket_list.elementAt(num).getSocket().socket.close();
                     }
                     catch(Exception e)
                     {
                     //server.writeLog("Socket cannot be closed. Please close manually");
                     }
                     try
                     {
                     socket_list.remove(num);
                     }
                     catch(Exception e)
                     {
                     //server.writeLog("Socket could not be removed from vector");
                     }
                     try
                     {
                     socket_list.elementAt(num).interrupt();
                     }
                     catch(Exception e)
                     {
                     //server.writeLog("Thread could not be interrupted");
                     }
                     for(int i=0;i<socket_list.size();i++)
                     {
                     if(socket_list.elementAt(i).getIndex() > num)
                     {
                     socket_list.elementAt(i).setIndex(socket_list.elementAt(i).getIndex()-1);
                     }
                     String s = Integer.toString(num+1);
                     socket_list.elementAt(i).getSocket().out.println("Player "+s+" has been kicked");
                     }
                     }
                     }*/
                    // Game command. Changes game mode - TO BE IMPLEMENTED FULLY
                    /*
                     else if(option == 4)
                     {
                     socket_list.elementAt(index).getSocket().out.println("Command not yet implemented");
                     }*/
                    // Score command. Changes score limit if applicable - TO BE IMPLEMENTED FULLY
                    /*
                     else if(option == 5)
                     {
                     socket_list.elementAt(index).getSocket().out.println("Command not yet implemented");
                     }*/
                    // Time command. Changes time limit if applicable - TO BE IMPLEMENTED FULLY
                    /*
                     else if(option == 6)
                     {
                     socket_list.elementAt(index).getSocket().out.println("Command not yet implemented");
                     }*/
                    // Useteams command. Toggles whether to use teams or free for all - TO BE IMPLEMENTED FULLY
                    /*
                     else if(option == 7)
                     {
                     if(use_teams == 0)
                     {
                     use_teams = 1;
                     }
                     else
                     {
                     use_teams = 0;
                     }
                     for(int i=0;i<socket_list.size();i++)
                     {
                     if(use_teams == 0)
                     {
                     socket_list.elementAt(i).getSocket().out.println("Teams are now being used");
                     }
                     else
                     {
                     socket_list.elementAt(i).getSocket().out.println("Teams are no longer being used");
                     }
                     }
                     }*/
                    // Start command. Closes the lobby server and starts the game server using all parameters set - NEED TO IMPLEMENT THIS NEXT
                    if (option == 8) {
                        game_state = 1;
                        for (SocketThread socket : socket_list.values()) {
                            try {
                                socket.getSocket().sendMessage("/gamestate 1");
                            } catch (IOException e) {

                            }
                        }
                    } else if (option == 11) {
                        game_map = text;
                        for (SocketThread socket : socket_list.values()) {
                            try {
                                socket.getSocket().sendMessage("/send Map has been changed to " + game_map);
                            } catch (IOException e) {

                            }
                        }
                    }
                }// If user is not the host notify them that the command is illegal
                else {
                    try {
                        socket_list.get(hash).getSocket().sendMessage("/send Only the Host can change this option");
                    } catch (IOException e) {

                    }
                }// Close command. Closes connection with the server
            } else if (option == 9) {
                if (hash.compareTo(host_hash) == 0) {
                    for (SocketThread socket : socket_list.values()) {
                        try {
                            socket.getSocket().sendMessage("/send The host has left, server has been closed");
                        } catch (IOException e) {

                        }
                        try {
                            socket.getSocket().close();
                        } catch (Exception e) {
                            //server.writeLog("Socket cannot be closed. Please close manually");
                        }
                        try {
                            socket.interrupt();
                        } catch (Exception e) {
                            //server.writeLog("Thread could not be interrupted");
                        }
                    }
                } else {
                    try {
                        socket_list.get(hash).getSocket().close();
                    } catch (Exception e) {
                        //server.writeLog("Socket cannot be closed. Please close manually");
                    }
                    try {
                        socket_list.remove(hash);
                    } catch (Exception e) {
                        //server.writeLog("Socket could not be removed from vector");
                    }
                    try {
                        socket_list.get(hash).interrupt();
                    } catch (Exception e) {
                        //server.writeLog("Thread could not be interrupted");
                    }
                }
            } // Help command
            else if (option == 10) {
                try {
                    socket_list.get(hash).getSocket().sendMessage("/send Lobby Commands: /player # - change player number. /teams # - change team number. /close - closes connection to the server. /kick # - kick player # (host only). /useteams - toggles whether teams are used (host only). /start - starts the game (host only)");
                } catch (IOException e) {

                }
            } else {
                try {
                    socket_list.get(hash).getSocket().sendMessage("/send Invalid Command");
                } catch (IOException e) {

                }
            }/*
             System.out.println("Raw Text: "+message);
             System.out.println("Option String: "+opt);
             System.out.println("Option Variable: "+text);
             System.out.println("Option code: "+option);*/

        } else if (game_state == 1) {

        }
        printUI();
    }
}

