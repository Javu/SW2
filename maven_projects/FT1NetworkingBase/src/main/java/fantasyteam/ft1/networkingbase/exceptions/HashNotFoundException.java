package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class HashNotFoundException extends NetworkingBaseException {
    
    public HashNotFoundException(String message) {
        super(message);
    }
    
    public HashNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
