/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author javu
 */
public class SocketData {
    private String hash;
    private String player_name;
    private int square_type;
    private int player_number;
    private int team;
    
    SocketData(){
        hash = "";
        player_name = hash;
        player_number = 0;
        team = 0;
    }
    
    SocketData(String h, String pna, int st, int pn, int t){
        hash = h;
        player_name = pna;
        square_type = st;
        player_number = pn;
        team = t;
    }
    
    public void setHash(String h){
        hash = h;
    }
    
    public void setPlayerName(String pna){
        player_name = pna;
    }
    
    public void setSquareType(int st){
        square_type = st;
    }
    
    public void setPlayerNumber(int pn){
        player_number = pn;
    }
    
    public void setTeam(int t){
        team = t;
    }
    
    public String getHash(){
        return hash;
    }
    
    public String getPlayerName(){
        return player_name;
    }
    
    public int getSquareType(){
        return square_type;
    }
    
    public int getPlayerNumber(){
        return player_number;
    }
    
    public int getTeam(){
        return team;
    }
    
    @Override
    public String toString(){
        return hash+","+player_name+","+square_type+","+player_number+","+team+",";
    }
}
