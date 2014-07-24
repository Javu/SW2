package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author Study
 */
public class NullException extends NetworkingBaseException {
    
    public NullException(String message) {
        super(message);
    }
    
    public NullException(String message, Throwable cause) {
        super(message, cause);
    }
}
