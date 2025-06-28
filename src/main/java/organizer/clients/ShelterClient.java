package organizer.clients;

import java.util.ArrayList;
import java.util.List;



import organizer.activities.Adoption;
import organizer.entities.Animal;
import organizer.entities.Donor;
import organizer.exceptionmanager.OasisUserException;

import organizer.exceptionmanager.OasisExceptionShieldingHandler;


/*Client-facing interface for shelter operations
thanks to the facade pattern, it hides the complexity of multiple services and allows
simplified interface for menus
*/

public class ShelterClient {
   
    private Adoption adoptionService;
  

    // it initializes adoption system that include adoption methods using shielding handler
    public void initializeSystem(String animalFile, String personFile) throws OasisUserException {
        
       
        OasisExceptionShieldingHandler.executeWithShield(() -> {
               this.adoptionService = new Adoption(animalFile, personFile);
        }, "shelter system initialization");
        
        System.out.println("Shelter system initialized ");
    }
    
    public void processAdoptionRequest(Donor donor) throws OasisUserException {
        //shielding for adoption operations
        OasisExceptionShieldingHandler.executeWithShield(() -> {
            adoptionService.adopt(donor);
        }, "adoption request processing");
        
        System.out.println("Adoption completed successfully!");
    }
    
    public void cancelAdoptionRequest(Animal adoptedAnimal, Donor donor) throws OasisUserException {
        // shielding for cancellation
        OasisExceptionShieldingHandler.executeWithShield(() -> {
            adoptionService.cancelAdoption(adoptedAnimal, donor);
        }, "adoption cancellation");
        
        System.out.println("Adoption cancelled successfully");
    }
    
    public List<Animal> getAvailableAnimals() {
        try {
            // shielding for reading operations
            return OasisExceptionShieldingHandler.executeWithShield(() -> {
                return adoptionService.getAdoptableAnimals();
            }, "retrieving available animals");
        } catch (OasisUserException e) {
            System.out.println("Error loading animals: " + e.getMessage());
            return new ArrayList<>(); 
        }
    }
     
    }
