package organizer.exceptionmanager;

public class AnimalCreationException extends OasisUserException {

     public AnimalCreationException(String message) {
        super("Animal creation error: " + message);
    }
    
    public AnimalCreationException(String message, Throwable cause) {
        super("Animal creation error: " + message, cause);
    }
    

}
