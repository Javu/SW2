package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class ServerSocketCloseException extends NetworkingBaseIOException {
    
    public ServerSocketCloseException(String message) {
        super(message);
    }
    
    public ServerSocketCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
