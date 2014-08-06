package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class InvalidArgumentException extends NetworkingBaseRuntimeException {
    
    public InvalidArgumentException(String message) {
        super(message);
    }
    
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
