package fantasyteam.ft1.networkingbase.exceptions;

/**
 * <p>
 * This exception should be thrown by any function that requires a specific
 * feature to be turned on before it can be run.
 * </p>
 * <p>
 * The exception type for this class is FeatureNotUsedException. See the
 * {@link NetworkingBaseRuntimeException} class for more information regarding
 * exception types.
 * </p>
 *
 * @author javu
 */
public class FeatureNotUsedException extends NetworkingBaseRuntimeException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public FeatureNotUsedException(String message) {
        super(message, "FeatureNotUsedException");
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
    public FeatureNotUsedException(String message, Throwable cause) {
        super(message, cause, "FeatureNotUsedException");
    }
}
