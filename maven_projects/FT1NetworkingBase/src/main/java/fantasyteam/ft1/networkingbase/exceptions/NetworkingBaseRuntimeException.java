package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author jsvu
 */
public class NetworkingBaseRuntimeException extends RuntimeException {
    
    public NetworkingBaseRuntimeException(String message) {
        super(message);
    }
    
    public NetworkingBaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
