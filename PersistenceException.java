package persistence;

/**
 * PersistenceException - Custom exception for data persistence errors
 * Provides detailed error information for file I/O and serialization failures
 * 
 * @author Group Leader
 * @version 1.0
 */
public class PersistenceException extends Exception {
    
    /**
     * Constructs a new PersistenceException with the specified detail message
     * 
     * @param message The detail message
     */
    public PersistenceException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new PersistenceException with the specified detail message and cause
     * 
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new PersistenceException with the specified cause
     * 
     * @param cause The cause of the exception
     */
    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
