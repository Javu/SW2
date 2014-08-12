package fantasyteam.ft1.exceptions;

/**
 * <p>
 * This error class is intended to be thrown when the engine reaches a state or
 * performs an action that it could never do under normal circumstances. It is
 * meant to be used to inform the developer of a bug in the engine code. Under
 * most circumstances this error should never be caught and handled as the
 * engine should never throw this exception.
 * </p>
 * <p>
 * An example of appropriate use for this error:  <code>
 * <br>
 * public void calculateA(int x) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;if(x {@literal >} 0) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x = calculateB(x);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} catch(Exception e) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new FT1EngineError("If this code is written correctly an Exception should never be caught here");<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * }<br>
 *<br>
 * public int calculateB(int x) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;if(x {@literal >} 0) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x++;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;} else {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new Exception();<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * }
 * </code>
 * </p>
 *
 * @author javu
 */
public class FT1EngineError extends RuntimeException {

    public FT1EngineError(String message) {
        super(message);
    }

    public FT1EngineError(String message, Throwable cause) {
        super(message, cause);
    }
}
