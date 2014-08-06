package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author Study
 */
public class NullException extends NetworkingBaseRuntimeException {
    
    public NullException(String message) {
        super(message);
    }
    
    public NullException(String message, Throwable cause) {
        super(message, cause);
    }
}
