package organizer.exceptionmanager;

//Base exception class for all user-facing errors  in the shelter system
//provides consistent error handling with context information
//it used by excpetion shielding to present user-friendlt messages
public class OasisUserException extends Exception{

    public OasisUserException(String message) {
        super(message);
    }
    
    public OasisUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // i implement a complete constructor composed of message, cause and context
    public OasisUserException(String userMessage, Throwable technicalCause, String context) {
        super(userMessage + " [Context: " + context + "]", technicalCause);
    }

}
