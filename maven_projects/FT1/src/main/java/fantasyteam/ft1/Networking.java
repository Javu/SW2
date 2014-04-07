package fantasyteam.ft1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * base class for networking implementation. contains concrete methods for code
 * that needs to happen, otherwise the physical implementation is left up to the
 * user
 *
 * @author jamessemple
 */
public abstract class Networking {

    //The game class to send actions back to
    private final Game game;
    
    
    public Networking(Game game) {
        this.game = game;
    }
    
    
    /**
     * sends an action across the network
     * @param action the name of the action to send
     * @param parameters the list of parameters to send for the action
     * @param clientIds the client ids to send the action to
     */
    public final void sendAction(String action, List<String> parameters, List<String> clientIds) {
        String message = encodeAction(action, parameters);

        for (String clientId : clientIds) {
            sendMessage(message, clientIds);
        }
    }

    
    /**
     * receive a message from a client
     * @param message the message received from the client
     * @param clientId the id of the client the message was received from
     */
    public final void receiveMessage(String message, String clientId) {
        Map<String, List<String>> action = parseAction(message);
        if (!handleNetworkAction(action, clientId)) {
            game.handleAction(action, clientId);
        }
    }

    
    /*
    * gets an action and parameters and turns it into a string to send accross the network
    */
    private String encodeAction(String action, List<String> parameters) {
        //TODO gets the action and list of parameters and turns them into a string to
        //transmit across the network
        return "";
    }

    
    /*
    * parses a string received accross the network
    */
    private Map<String, List<String>> parseAction(String message) {
        //TODO parsing code goes here, needs to return a map with a single entry
        //the key is the actionName, the value is the list of string parameters
        return new HashMap<>();
    }

    
    /*
    * handles a reserved network action
    */
    private boolean handleNetworkAction(Map<String, List<String>> action, String clientId) {
//        switch (action.get(0).key()) {
//            case "reserved network action 1":
//                networkAction1(action.get(0))
//                                    return true;
//            case "reserved network action 2":
//                networkAction2(action.get(0));
//                return true;
//                ......
//            default:
//                return false;
//        }
        return false;
    }

    
    /**
     * abstract method. handles the physical sending of the message string to the
     * specified client ids
     * 
     * @param message the message received over the network
     * @param clientIds the client ids to send the message to
     */
    protected abstract void sendMessage(String message, List<String> clientIds);


//    /**
//    * Each reserved network action will have an abstract method that needs to be overridden to contain
//    * the physical implementation of how the server handles reserved network functions (e.g. socket disconnect)
//    */
//    protected abstract void networkAction1();
}
