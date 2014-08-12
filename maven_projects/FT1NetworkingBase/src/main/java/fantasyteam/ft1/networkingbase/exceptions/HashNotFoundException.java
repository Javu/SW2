package fantasyteam.ft1.networkingbase.exceptions;

/**
 * <p>
 * This exception should be thrown by any function that attempts to reference a
 * hash/key/index in a map/list/array but the referenced hash/key/index does not
 * exist.
 * </p>
 * <p>
 * The exception type for this class is HashNotFoundException. See the
 * {@link NetworkingBaseRuntimeException} class for more information regarding
 * exception types.
 * </p>
 *
 * @author javu
 */
public class HashNotFoundException extends NetworkingBaseRuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public HashNotFoundException(String message) {
        super(message, "HashNotFoundException");
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
    public HashNotFoundException(String message, Throwable cause) {
        super(message, cause, "HashNotFoundException");
    }
}
