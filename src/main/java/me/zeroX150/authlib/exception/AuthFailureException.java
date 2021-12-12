package me.zeroX150.authlib.exception;

/**
 * Gets thrown when something goes wrong
 */
public class AuthFailureException extends RuntimeException {

    /**
     * Makes a new instance.
     *
     * @param message the message
     */
    public AuthFailureException(String message) {
        super(message);
    }

}
