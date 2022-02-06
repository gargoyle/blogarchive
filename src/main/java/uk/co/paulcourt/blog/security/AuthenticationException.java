package uk.co.paulcourt.blog.security;

/**
 *
 * @author paul
 */
public class AuthenticationException extends SecurityException {

    public AuthenticationException() {
        super("Authentication Failed");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
