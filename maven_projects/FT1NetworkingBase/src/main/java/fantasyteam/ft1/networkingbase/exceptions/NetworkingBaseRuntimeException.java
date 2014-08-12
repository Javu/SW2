package fantasyteam.ft1.networkingbase.exceptions;

/**
 * <p>
 * This is the base RunTime exception class for all custom runtime exceptions
 * thrown by the fantasyteam.ft1.networkingbase package. All exceptions that are
 * not required to be handled should extend this class.
 * </p>
 * <p>
 * The exception type for this class is NetworkingBaseIOException. See the
 * getExceptionType function for more information on the exception type.
 * </p>
 *
 * @author javu
 */
public class NetworkingBaseRuntimeException extends RuntimeException {

    /**
     * <p>
     * This variable is used to give a String name to this exception. This is
     * used to easily identify what class this exception is when it is
     * used/extracted as the cause of another throwable.
     * </p>
     * <p>
     * The exception type for this class is NetworkingBaseRuntimeException. See
     * the getExceptionType function for more information on the exception type.
     * </p>
     */
    protected final String exception_type;

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public NetworkingBaseRuntimeException(String message) {
        super(message);
        exception_type = "NetworkingBaseRuntimeException";
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
        exception_type = "NetworkingBaseRuntimeException";
    }

    /**
     * Constructor that takes a custom message as input and a string for the
     * exception_type attribute. This constructor is only meant to be used by
     * classes extending this class so they can set their own custom
     * exception_type.
     *
     * @param message Custom message String.
     * @param exception_type String identifier for this class.
     */
    protected NetworkingBaseRuntimeException(String message, String exception_type) {
        super(message);
        this.exception_type = exception_type;
    }

    /**
     * Constructor that takes a custom message, another Throwable instance and a
     * string for the exception_type attribute. This constructor is only meant
     * to be used by classes extending this class so they can set their own
     * custom exception_type. Usually used if a different exception is caught
     * and an instance of this exception is then thrown instead but the original
     * Throwable's data still needs to be thrown.
     *
     * @param message Custom message String.
     * @param cause a different instance of Throwable, usually the Throwable
     * that caused this exception to be thrown.
     * @param exception_type String identifier for this class.
     */
    protected NetworkingBaseRuntimeException(String message, Throwable cause, String exception_type) {
        super(message, cause);
        this.exception_type = exception_type;
    }

    /**
     * Returns the attribute exception_type. This String variable is meant to be
     * used to easily identify the class of exception this is. This is useful
     * when this exception class is contained as the cause inside another
     * exception and you need to know what type of exception this cause is. See
     * this classes description for the exception_type.
     *
     * @return String identifier used for this class.
     */
    public String getExceptionType() {
        return exception_type;
    }
}
