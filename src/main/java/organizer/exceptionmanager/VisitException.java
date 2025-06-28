package organizer.exceptionmanager;

public class VisitException extends OasisUserException {

    public VisitException(String message) {
        super("Error in visit management: " + message);
    }
    
    public VisitException(String message, Throwable cause) {
        super("Error in visit management: " + message, cause);
    }
    
    // Constructor for time/date not available-- Only 10 people at a time (moring or evening) can visit the structure
    public static VisitException timeNotAvailable(String date, String time) {
        return new VisitException("Time" + time + " - " + date + " non è dis");
    }
    
    // Costructor for impossibility to modify visit 
    public static VisitException cannotModify(String visitId, String reason) {
        return new VisitException("Impossible to change the visit " + visitId + ": " + reason);
    }

}
