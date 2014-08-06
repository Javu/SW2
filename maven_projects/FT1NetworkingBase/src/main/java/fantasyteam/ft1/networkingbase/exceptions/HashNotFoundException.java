package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class HashNotFoundException extends NetworkingBaseRuntimeException {
    
    public HashNotFoundException(String message) {
        super(message);
    }
    
    public HashNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
