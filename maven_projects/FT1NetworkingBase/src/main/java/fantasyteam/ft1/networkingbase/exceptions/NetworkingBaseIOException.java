package fantasyteam.ft1.networkingbase.exceptions;

/**
 * This is the base exception class for all custom exceptions thrown by the
 * fantasyteam.ft1.networkingbase package that are not runtime exceptions. All
 * exceptions that are required to be handled should extend this class.
 *
 * @author javu
 */
public class NetworkingBaseIOException extends Exception {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public NetworkingBaseIOException(String message) {
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
    public NetworkingBaseIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
