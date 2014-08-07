package fantasyteam.ft1.networkingbase.exceptions;

/**
 * This exception should be thrown by any function that attempts to validate
 * input and detects that the input is invalid.
 *
 * @author javu
 */
public class InvalidArgumentException extends NetworkingBaseRuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public InvalidArgumentException(String message) {
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
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
