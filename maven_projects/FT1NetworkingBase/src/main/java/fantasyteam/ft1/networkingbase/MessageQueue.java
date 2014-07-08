package fantasyteam.ft1.networkingbase;

import fantasyteam.ft1.Timing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javu
 */
public class MessageQueue extends Thread {
    
    private ArrayList<String> messages;
    private SocketThread socket;
    private Timing timer;
    private int state;
    /**
     * Logger for logging important actions and exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(MessageQueue.class.getName());
    
    public MessageQueue(SocketThread socket) {
        messages = new ArrayList<String>();
        this.socket = socket;
        timer = null;
        state = 0;
    }
    
    public void close() {
        messages = null;
        timer = null;
        state = 4;
    }
    
    @Override
    public void run() {
        state = 1;
        while(state >= 1 && state <= 3) {
            if(!messages.isEmpty() && socket.getRun() >= 2 && socket.getRun() <= 3){
                try {
                    socket.getSocket().sendMessage(messages.get(0));
                    messages.remove(0);
                    state = 1;
                    if(timer != null) {
                        timer = null;
                    }
                } catch (IOException e) {
                    state = 2;
                    if(timer == null) {
                        timer = new Timing();
                    }
                    LOGGER.log(Level.SEVERE,"Could not send message: {0}. Exception: {1}",new Object[]{messages.get(0),e});
                    if(timer.getTime() > 5000) {
                        LOGGER.log(Level.SEVERE,"Sending message has timed out. Disconnecting socket. Message: ",messages.get(0));
                        socket.getServer().disconnect(socket.getHash());
                    }
                } 
            }
        }
        LOGGER.log(Level.INFO,"MessageQueue successfully closed");
        //this.interrupt();
    }
    
    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
    
    public void setRun(int state) {
        this.state = state;
    }
    
    public ArrayList<String> getMessages() {
        return messages;
    }
    
    public int getRun() {
        return state;
    }
    
    public void queueMessage(String message) {
        messages.add(message);
    }
    
    public void pauseQueue() {
        state = 3;
    }
    
    public void resumeQueue() {
        state = 1;
    }
    
    public void clearQueue() {
        messages.clear();
    }
    
    @Override
    public String toString() {
        String to_string = toString("");
        return to_string;
    }
    
    public String toString(String ch) {
        String to_string = ch + "MessageQueue attribute values:" + "\n" + ch + "Owning SocketThread hash: " + socket.getHash() + "\n" + ch + "State: " + state;
        if(!messages.isEmpty()) {
            to_string += "\n" + ch + "Queued messages:";
            for(String message : messages) {
                to_string += "\n" + ch + "\t" + message;
            }
        } else {
            to_string += "\n" + ch + "There are no queued messages";
        }
        return to_string;
    }
}
