package organizer.exceptionmanager;

public class VeterinaryOperationException extends OasisUserException {

     public VeterinaryOperationException(String message) {
        super("Errore nell'operazione veterinaria: " + message);
    }
    
    public VeterinaryOperationException(String message, Throwable cause) {
        super("Errore nell'operazione veterinaria: " + message, cause);
    }

}
