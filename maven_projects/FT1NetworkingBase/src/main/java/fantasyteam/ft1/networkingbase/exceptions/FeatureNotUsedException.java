package fantasyteam.ft1.networkingbase.exceptions;

/**
 *
 * @author javu
 */
public class FeatureNotUsedException extends NetworkingBaseException {
    
    public FeatureNotUsedException(String message) {
        super(message);
    }
    
    public FeatureNotUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
