package fantasyteam.ft1.networkingbase.exceptions;

/**
 * <p>
 * This is a very specific exception that is thrown if there is an issue closing
 * a ServerSocket. Used by the
 * {@link fantasyteam.ft1.networkingbase.ListenThread} class.
 * </p>
 * <p>
 * The exception type for this class is ServerSocketCloseException. See the
 * {@link NetworkingBaseIOException} class for more information regarding
 * exception types.
 * </p>
 *
 * @author javu
 */
public class ServerSocketCloseException extends NetworkingBaseIOException {

    /**
     * Constructor that takes a custom message as input.
     *
     * @param message Custom message String
     */
    public ServerSocketCloseException(String message) {
        super(message, "ServerSocketCloseException");
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
    public ServerSocketCloseException(String message, Throwable cause) {
        super(message, cause, "ServerSocketCloseException");
    }
}
