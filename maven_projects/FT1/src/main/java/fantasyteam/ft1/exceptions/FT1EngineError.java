package fantasyteam.ft1.exceptions;

/**
 *
 * @author javu
 */
public class FT1EngineError extends RuntimeException {
    
    public FT1EngineError(String message) {
        super(message);
    }
    
    public FT1EngineError(String message, Throwable cause) {
        super(message, cause);
    }
}
