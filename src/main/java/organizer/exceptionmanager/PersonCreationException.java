package organizer.exceptionmanager;

public class PersonCreationException extends OasisUserException {

    public PersonCreationException(String message) {
        super("Error in person creation: " + message);
    }
    
    public PersonCreationException(String message, Throwable cause) {
        super("Errore in person creation: " + message, cause);
    }
    

}


