package fantasyteam.ft1;

import java.util.ArrayList;
import java.util.List;

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
     * sends an action across the network
     * @param action the action name including the list of parameters to send for the action
     * @param clientIds the client ids to send the action to
     */
    public final void sendAction(List<String> action, List<String> clientIds) {
        String message = encodeAction(action);

        for (String clientId : clientIds) {
            sendMessage(message, clientIds);
        }
    }
    
    /**
     * sends an action across the network
     * @param action the String to send for the action
     * @param clientIds the client ids to send the action to
     */
    public final void sendAction(String action, List<String> clientIds) {
        String message = encodeAction(action);

        for (String clientId : clientIds) {
            sendMessage(message, clientIds);
        }
    }

    /**
     * sends an action across the network
     * @param action the name of the action to send
     * @param parameters the list of parameters to send for the action
     * @param clientId the client id to send the action to
     */
    public final void sendAction(String action, List<String> parameters, String clientId) {
        String message = encodeAction(action, parameters);
        sendMessage(message, clientId);
    }
    
    /**
     * sends an action across the network
     * @param action the action name including the list of parameters to send for the action
     * @param clientId the client id to send the action to
     */
    public final void sendAction(List<String> action, String clientId) {
        String message = encodeAction(action);
        sendMessage(message, clientId);
    }
    
    /**
     * sends an action across the network
     * @param action the String to send for the action
     * @param clientId the client id to send the action to
     */
    public final void sendAction(String action, String clientId) {
        String message = encodeAction(action);
        sendMessage(message, clientId);
    }
    
    /**
     * receive a message from a client
     * @param message the message received from the client
     * @param clientId the id of the client the message was received from
     */
    public void receiveMessage(String message, String clientId) {
        List<String> action = parseAction(message);
        if (!handleNetworkAction(action, clientId)) {
            game.handleAction(action, clientId);
        }
    }
    
    /*
    * gets an action and parameters and turns it into a string to send across the network
    */
    private String encodeAction(String action, List<String> parameters) {
        String seperator = Character.toString((char)31);
        String action_string = action + seperator;
        if(parameters != null) {
            for(String parameter : parameters) {
                action_string += parameter + seperator;
            }
        }
        return action_string;
    }
    
    /*
    * gets an action with parameters and turns it into a string to send across the network
    */
    private String encodeAction(List<String> action) {
        String seperator = Character.toString((char)31);
        String action_string = "";
        if(action != null) {
            for(String parameter : action) {
                action_string += parameter + seperator;
            }
        }
        return action_string;
    }
    
    /*
    * gets a string and turns encodes it to send across the network
    */
    private String encodeAction(String action) {
        String seperator = Character.toString((char)31);
        String action_string = action + seperator;
        return action_string;
    }

    
    /*
    * parses a string received across the network
    */
    private List<String> parseAction(String message) {
        boolean action_found = false;
        String parameter_string = "";
        List<String> parameter_list = new ArrayList<>();
        if(message != null) {
            for (char ch : message.toCharArray()) {
                if (ch == 31) {
                    parameter_list.add(parameter_string);
                    parameter_string = "";
                    action_found = true;
                } else {
                    parameter_string += ch;
                }
            }
            if (!action_found) {
                parameter_list.add(message);
            }
        }
        return parameter_list;
    }

    
    /*
    * handles a reserved network action
    */
    private boolean handleNetworkAction(List<String> action, String clientId) {
        switch (action.get(0)) {
            case "":
                return true;
            case "disconnect":
                disconnect(clientId);
                return true;
            case "customnetwork1":
                customNetwork1(action, clientId);
                return true;
            default:
                return false;
        }
    }

    
    /**
     * abstract method. handles the physical sending of the message string to the
     * specified client ids
     * 
     * @param message the message received over the network
     * @param clientIds the client ids to send the message to
     */
    protected abstract void sendMessage(String message, List<String> clientIds);
    
    /**
     * abstract method. handles the physical sending of the message string to the
     * specified client id
     * 
     * @param message the message received over the network
     * @param clientId the client id to send the message to
     */
    protected abstract void sendMessage(String message, String clientId);

    protected abstract void disconnect(String hash);
    
//    /**
//    * Each reserved network action will have an abstract method that needs to be overridden to contain
//    * the physical implementation of how the server handles reserved network functions (e.g. socket disconnect)
//    */
//    protected abstract void networkAction1();
    
    protected abstract void customNetwork1(List<String> action, String clientId);
}
