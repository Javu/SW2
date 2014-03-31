package fantasyteam.sw2.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class GameServer extends Server {

    private HashMap<String, SocketData> socket_data;
    private ArrayList<String> player_keys;
    private ArrayList<Boolean> player_valid;
    private int max_players;
    private int game_type;
    private int use_teams;
    private boolean ready;

    public static void main(String args[]) {
        int run = 1;
        GameServer server = new GameServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        for (int i = 0; i < server.getMaxPlayers(); i++) {
            server.getPlayerKeys().add(args[i + 3]);
            server.getPlayerValid().add(false);
        }
        File file = null;
        try {
            file = new File("player-data.txt");
        } catch (Exception e) {
            System.out.println("LOL NO FILE");
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (String message; (message = reader.readLine()) != null;) {
                ArrayList<String> messages = new ArrayList<String>();
                String data = "";
                for (char n : message.toCharArray()) {
                    if (Character.toString(n).compareTo(",") == 0) {
                        messages.add(data);
                        data = "";
                    } else {
                        data += n;
                    }
                }
                server.socket_data.put(messages.get(0), new SocketData(messages.get(0), messages.get(1), Integer.parseInt(messages.get(2)), Integer.parseInt(messages.get(3)), Integer.parseInt(messages.get(4))));
            }
        } catch (Exception e) {

            while (run == 1) {
                // Block while awaiting connections. When a connection is made create a new SocketThread and start the thread
                // Only accept new connections until all players have connected and have been validated
                if (server.getReady() == false) {
                    try {
                        server.listen();
                    } catch (IOException ex) {
                        System.out.println("Could not listen");
                    }
                } // Once all players are validated begin running the standard game loop, checking for win conditions and keeping players updated
                else {
                    System.out.println("HOT DAM EVERYONE'S CONNECTED");
                }
            }
        }
    }

    public GameServer() {
        port = 23232;
        max_players = 8;
        game_type = 0;
        use_teams = 0;
        socket_data = new HashMap<String, SocketData>();
        player_keys = new ArrayList<String>();
        player_valid = new ArrayList<Boolean>();
        ready = false;
    }

    public GameServer(int g_type, int teams, int max_p
    ) {
        port = 23232;
        max_players = max_p;
        game_type = g_type;
        use_teams = teams;
        socket_data = new HashMap<String, SocketData>();
        player_keys = new ArrayList<String>();
        player_valid = new ArrayList<Boolean>();
        ready = false;
    }

    public int getMaxPlayers() {
        return max_players;
    }

    public boolean getReady() {
        return ready;
    }

    public HashMap<String, SocketData> getSocketData() {
        return socket_data;
    }

    public ArrayList<String> getPlayerKeys() {
        return player_keys;
    }

    public ArrayList<Boolean> getPlayerValid() {
        return player_valid;
    }

    public void setPlayerKeys(ArrayList<String> s) {
        player_keys = s;
    }

    public void setPlayerValid(ArrayList<Boolean> v) {
        player_valid = v;
    }

    public void handleMessage(String message, String hash) {
        int option = 0;
        int count = 0;
        String text = "";
        String opt = "";
        if (ready == false) {
            for (char n : message.toCharArray()) {
                // If a special command is found extract the attributes of the command
                if (option != 0) {
                    if (count != 0) {
                        text += n;
                    } else {
                        count += 1;
                    }
                } else {
                    opt += n;
                }

                if (opt.compareTo("/connect") == 0) {
                    option = 1;
                }
            }

            boolean p_valid = false;
            if (opt.compareTo("/connect") == 0) {
                for (int i = 0; i < player_keys.size(); i++) {
                    if (text == player_keys.get(i) && player_valid.get(i).booleanValue() != true) {
                        socket_list.put(text, socket_list.get(hash));
                        socket_list.remove(hash);
                        player_valid.set(i, Boolean.valueOf(true));
                        p_valid = true;

                        boolean ready_valid = true;
                        for (int j = 0; j < player_valid.size(); j++) {
                            if (player_valid.get(j).booleanValue() == false) {
                                ready_valid = false;
                            }
                        }
                        if (ready_valid == true) {
                            ready = true;
                        }
                    }
                }
            }

            if (p_valid == false) {
                try {
                    socket_list.get(hash).getSocket().close();
                } catch (IOException e) {
                    //server.writeLog("Socket cannot be closed. Please close manually");
                }
                try {
                    socket_list.get(hash).interrupt();
                } catch (Exception e) {
                    //server.writeLog("Thread could not be interrupted");
                }
                socket_list.remove(hash);
            }
        } else {
            for (char n : message.toCharArray()) {
                // If a special command is found extract the attributes of the command
                if (option != 0) {
                    if (count != 0) {
                        text += n;
                    } else {
                        count += 1;
                    }
                } else {
                    opt += n;
                }
            }
        }
    }
}
