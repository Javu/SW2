package fantasyteam.ft1.networkingbase.exceptions;

/**
 * This is the base RunTime exception class for all custom runtime exceptions
 * thrown by the fantasyteam.ft1.networkingbase package. All exceptions that are
 * not required to be handled should extend this class.
 *
 * @author javu
 */
public class NetworkingBaseRuntimeException extends RuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public NetworkingBaseRuntimeException(String message) {
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
    public NetworkingBaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
