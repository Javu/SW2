package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class TimeoutException extends NetworkingBaseIOException {
    
    public TimeoutException(String message) {
        super(message);
    }
    
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}