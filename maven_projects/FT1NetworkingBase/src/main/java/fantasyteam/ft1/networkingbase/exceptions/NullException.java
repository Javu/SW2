package fantasyteam.ft1.networkingbase.exceptions;

/**
 * This exception should be thrown if a variable or class is referenced but the
 * variable or class is set to null.
 *
 * @author javu
 */
public class NullException extends NetworkingBaseRuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public NullException(String message) {
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
    public NullException(String message, Throwable cause) {
        super(message, cause);
    }
}
