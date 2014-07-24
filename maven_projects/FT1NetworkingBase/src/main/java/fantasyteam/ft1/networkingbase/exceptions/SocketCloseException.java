package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class SocketCloseException extends NetworkingBaseException {
    
    public SocketCloseException(String message) {
        super(message);
    }
    
    public SocketCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
