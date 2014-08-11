package fantasyteam.ft1.exceptions;

/**
 *
 * @author Javu
 */
public class NetworkingRuntimeException extends RuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public NetworkingRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructor that takes a custom message and another Throwable instance as
     * input. Usually used if a different exception is caught and an instance of
     * this exception is then thrown instead but the original Throwable's data
     * still needs to be thrown.
     *
     * @param message Custom message String.
     * @param cause a different instance of Throwable, usually the Throwable
     * that caused this exception to be thrown.
     */
    public NetworkingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
