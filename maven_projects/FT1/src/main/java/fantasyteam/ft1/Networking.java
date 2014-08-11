package fantasyteam.ft1;

import fantasyteam.ft1.exceptions.NetworkingIOException;
import fantasyteam.ft1.exceptions.NetworkingRuntimeException;
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
     *
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
     *
     * @param action the action name including the list of parameters to send
     * for the action
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
     *
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
     *
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
     *
     * @param action the action name including the list of parameters to send
     * for the action
     * @param clientId the client id to send the action to
     */
    public final void sendAction(List<String> action, String clientId) {
        String message = encodeAction(action);
        sendMessage(message, clientId);
    }

    /**
     * sends an action across the network
     *
     * @param action the String to send for the action
     * @param clientId the client id to send the action to
     */
    public final void sendAction(String action, String clientId) {
        String message = encodeAction(action);
        sendMessage(message, clientId);
    }

    /**
     * receive a message from a client
     *
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
        String seperator = Character.toString((char) 31);
        String action_string = action + seperator;
        if (parameters != null) {
            for (String parameter : parameters) {
                action_string += parameter + seperator;
            }
        }
        return action_string;
    }

    /*
     * gets an action with parameters and turns it into a string to send across the network
     */
    private String encodeAction(List<String> action) {
        String seperator = Character.toString((char) 31);
        String action_string = "";
        if (action != null) {
            for (String parameter : action) {
                action_string += parameter + seperator;
            }
        }
        return action_string;
    }

    /*
     * gets a string and turns encodes it to send across the network
     */
    private String encodeAction(String action) {
        String seperator = Character.toString((char) 31);
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
        if (message != null) {
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
     * Abstract method. The handleAction method is how the {@link Game} class
     * will use implementation specific functions from extensions of the
     * {@link Networking} class. For each function that should be available to
     * the {@link Game} class, the extension of {@link Networking} will need to
     * have a list of action strings representing each function that can be
     * passed to the handleAction method to allow the {@link Game} class to run
     * the needed function. This version of handleAction takes a String for the
     * action and a List of the parameters to pass to the function.
     *
     * An example would be if your NetworkingExtension class has a function
     * addConnection(String ip, int port) the {@link Game} class cannot directly
     * run this function as {@link Networking} has no function named this. As
     * such the NetworkingExtension class would need to include an action in its
     * handleAction method to take a String name corresponding to this function
     * and the list of parameters to pass to the function. An example could be:
     *
     * class NetworkingExtension extends Networking { public void
     * addConnection(String ip, int port) { //do stuff in here }
     *
     * public void handleAction(String action, List parameters) { switch(action)
     * { case "connect":
     * addConnection(parameters.get(0),(int)parameters.get(1)); //so on and so
     * on... } } }
     *
     * @param action The String corresponding to the function to be run.
     * @param parameters The parameters to pass to the function.
     * @throws NetworkingIOException If an exception is caught when running the
     * specified action. Exceptions of this nature need to be caught and
     * handled. Check the cause throwable of the exception for more details.
     * @throws NetworkingRuntimeException If an exception is caught when running
     * the specified action. Exceptions of this nature do not need to be caught
     * but will interrupt running of the program is unhandled.
     */
    public abstract void handleAction(String action, List<String> parameters) throws NetworkingIOException, NetworkingRuntimeException;

    /**
     * Abstract method. The handleAction method is how the {@link Game} class
     * will use implementation specific functions from extensions of the
     * {@link Networking} class. For each function that should be available to
     * the {@link Game} class, the extension of {@link Networking} will need to
     * have a list of action strings representing each function that can be
     * passed to the handleAction method to allow the {@link Game} class to run
     * the needed function. This version of handleAction takes just a List. It
     * is expected that the action String will be the first index in the List.
     *
     * An example would be if your NetworkingExtension class has a function
     * addConnection(String ip, int port) the {@link Game} class cannot directly
     * run this function as {@link Networking} has no function named this. As
     * such the NetworkingExtension class would need to include an action in its
     * handleAction method to take a String name corresponding to this function
     * and the list of parameters to pass to the function. An example could be:
     *
     * class NetworkingExtension extends Networking { public void
     * addConnection(String ip, int port) { //do stuff in here }
     *
     * public void handleAction(List parameters) { String action =
     * parameters.get(0); switch(action) { case "connect":
     * addConnection(parameters.get(1),(int)parameters.get(2)); //so on and so
     * on... } } }
     *
     * @param parameters The parameters to pass to the function, including the
     * action String corresponding to the function at index 0.
     * @throws NetworkingIOException If an exception is caught when running the
     * specified action. Exceptions of this nature need to be caught and
     * handled. Check the cause throwable of the exception for more details.
     * @throws NetworkingRuntimeException If an exception is caught when running
     * the specified action. Exceptions of this nature do not need to be caught
     * but will interrupt running of the program is unhandled.
     */
    public abstract void handleAction(List<String> parameters) throws NetworkingIOException, NetworkingRuntimeException;

    /**
     * Abstract method. The handleAction method is how the {@link Game} class
     * will use implementation specific functions from extensions of the
     * {@link Networking} class. For each function that should be available to
     * the {@link Game} class, the extension of {@link Networking} will need to
     * have a list of action strings representing each function that can be
     * passed to the handleAction method to allow the {@link Game} class to run
     * the needed function. This version of handleAction takes just a String. It
     * is intended to be used for functions that do not have any parameters.
     *
     * An example would be if your NetworkingExtension class has a function
     * addConnection() the {@link Game} class cannot directly run this function
     * as {@link Networking} has no function named this. As such the
     * NetworkingExtension class would need to include an action in its
     * handleAction method to take a String name corresponding to this function.
     * An example could be:
     *
     * class NetworkingExtension extends Networking { public void
     * addConnection() { //do stuff in here }
     *
     * public void handleAction(String action) { switch(action) { case
     * "connect": addConnection(); //so on and so on... } } }
     *
     * @param action The action String corresponding to the needed function.
     * @throws NetworkingIOException If an exception is caught when running the
     * specified action. Exceptions of this nature need to be caught and
     * handled. Check the cause throwable of the exception for more details.
     * @throws NetworkingRuntimeException If an exception is caught when running
     * the specified action. Exceptions of this nature do not need to be caught
     * but will interrupt running of the program is unhandled.
     */
    public abstract void handleAction(String action) throws NetworkingIOException, NetworkingRuntimeException;

    /**
     * abstract method. handles the physical sending of the message string to
     * the specified client ids
     *
     * @param message the message received over the network
     * @param clientIds the client ids to send the message to
     */
    protected abstract void sendMessage(String message, List<String> clientIds);

    /**
     * abstract method. handles the physical sending of the message string to
     * the specified client id
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
