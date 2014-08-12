package fantasyteam.ft1.networkingbase.exceptions;

/**
 * <p>
 * This exception should be thrown if a function times out. A lot of critical
 * functions that need to wait on data/information from a different thread
 * implement a timeout feature so that they will only wait a set amount of time
 * for needed data/information. If this set time is reached before the
 * data/implementation is received then they throw a {@link TimeoutException}.
 * </p>
 * <p>
 * The exception type for this class is TimeoutException. See the
 * {@link NetworkingBaseIOException} class for more information regarding
 * exception types.
 * </p>
 *
 * @author javu
 */
public class TimeoutException extends NetworkingBaseIOException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public TimeoutException(String message) {
        super(message, "TimeoutException");
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
    public TimeoutException(String message, Throwable cause) {
        super(message, cause, "TimeoutException");
    }
}
