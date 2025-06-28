package organizer.exceptionmanager;
//exception for business logic errors related to adoption
public class AdoptionException extends OasisUserException {

     public AdoptionException(String message) {
        super("Adoption error: " + message);
    }
    
    public AdoptionException(String message, Throwable cause) {
        super("Adoption error: " + message, cause);
    }
    
    // Constructor for is animal not adoptable because is not availabe -- an animal could be adopted once at time
    public static AdoptionException animalNotAvailable(String animalName, String reason) {
        return new AdoptionException("Animal selected " + animalName + " is not available: " + reason);
    }
    
    // Constructor for error to adoption not available for a person
    public static AdoptionException donorNotEligible(String donorName, String reason) {
        return new AdoptionException("Person" + donorName + " can't adopt: " + reason);
    }

}
