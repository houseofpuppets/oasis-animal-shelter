package organizer.activities;

import organizer.datamanagement.AnimalUploading;
import organizer.datamanagement.PeopleUpdloading;
import static organizer.datamanagement.SpeciesSet.specieSet;
import organizer.entities.*;
import organizer.exceptionmanager.AdoptionException;
import organizer.exceptionmanager.OasisExceptionShieldingHandler;
import organizer.exceptionmanager.OasisUserException;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import organizer.datamanagement.AnimalUploading;
import organizer.clients.ShelterClient;



public class Adoption {
  
   //core structure for adoption management
    
   
    private List<Animal> adoptableAnimals; //aanimals available for adoption
    private String selectedSpecies; // species selected by user 
    private List<Person> donors;
    private HashMap<String, Animal> adopted=new HashMap<String, Animal>(); //map structure (name animal-key/animal-value)
    private HashMap<Donor, Animal>adoptionMatches=new HashMap<Donor, Animal>(); //matches donor with adopted animal

    public Adoption(String fileAnimal, String filePeople) throws OasisUserException {
     // costrustor initializes adoption system by loading animals and people
     //use of exception shielding to handle file loading errors
      OasisExceptionShieldingHandler.executeWithShield(() ->{
        AnimalUploading uploading = new AnimalUploading(fileAnimal); //uploader for animals
        this.adoptableAnimals=new ArrayList<>();  
        this.adoptableAnimals=uploading.animalList();                                      
        PeopleUpdloading updloading1 =new PeopleUpdloading(filePeople);
        //fitering authorized donors
        this.donors =updloading1.peopleList().stream()
                                                     .filter(p->p instanceof Donor)
                                                     .collect(Collectors.toList());
        System.out.println("Adoption system initialized with"+adoptableAnimals.size()+"animals and"+donors.size()+"authorized donors");
  
    }, "adoption system initialization");
}
   //it processes an adoption from a verified donor--sspecies selection--adoption complation
    public void adopt( Donor donor) throws OasisUserException {
      OasisExceptionShieldingHandler.executeWithShield(() ->{
         //check animal availability
        if (adoptableAnimals==null|| adoptableAnimals.isEmpty()){

            throw new AdoptionException("Distance adoption not available in this moment. Not available animals");
          
        } 
       //verify person authorization
       if(!isDonorAuthorized(donor)){
           throw new AdoptionException("Donor not authorized");
       }
         //display available species and get user selection

         displayAvailableSpecies();
         this.selectedSpecies=getUserSpeciesSelection();

         //find and assign animal for adoption
         Animal selectedAnimal=findAvailableAnimalBySpecies(selectedSpecies);
         completeAdoptionProcess(donor, selectedAnimal);
         
          },"adoption process");
                
      }
      
      //method that cancels an existing adoption and returns animal to the available list
      public void cancelAdoption(Animal adoptedAnimal, Donor donor) throws OasisUserException {   
        OasisExceptionShieldingHandler.executeWithShield(() -> {
         //verify if adoption exists and that animal and dono matches
          if (!adoptionMatches.containsKey(donor)|| !adoptionMatches.get(donor).equals(adoptedAnimal)){ 
           throw new AdoptionException("Error!Animal not found in adoption records for this donor");
        }
           adoptableAnimals.add(adoptedAnimal); 
           adopted.remove(adoptedAnimal.getName());
           adoptionMatches.remove(donor);
           donor.setStatusDonator(Status.None); // donor loses status Adopter

   

        System.out.println("Adoption successfully cancelled for " + adoptedAnimal.getName());
            
        }, "adoption cancellation");
    }


    //method for internal operation
     //Name/surnames matching. To implement in future better authentication
      private boolean isDonorAuthorized(Donor donor){

          return donors.stream()
                   .anyMatch(person->person.getName().equalsIgnoreCase(donor.getName())&&person.getSurname().equalsIgnoreCase(donor.getSurname()));
         
        }

      //Display all available species to help species selection
        
      private void displayAvailableSpecies(){
          
          System.out.println("Available species for adoption:");
          specieSet.forEach(System.out::println);
      }

      //Gets species selection from user input
      
      private String getUserSpeciesSelection(){

         Scanner scanner = new Scanner(System.in);
         System.out.print("Enter the desiderd species:");

         return scanner.nextLine().trim();
      }

      //this method finds first available animal of a specified species. 
      //throws exception if no animal of the species are available

      private Animal findAvailableAnimalBySpecies(String species) throws OasisUserException{

         return adoptableAnimals.stream()        
                                .filter(animal -> animal.getSpecies().equalsIgnoreCase(species))
                                .findFirst()
                                .orElseThrow(()->new AdoptionException(
                                 "No animal of species "+species+" available for adoption"));
                                }
      
          
      
      //complete the adoption process by updating records

      private void completeAdoptionProcess(Donor donor, Animal animal){

               adopted.put(animal.getName(), animal );  
               adoptableAnimals.remove(animal);
               adoptionMatches.put(donor, animal);
               donor.setStatusDonator(Status.Adopter);
               System.out.println("Adoption completed successfully: "+donor.getName()+" "+donor.getSurname()+"adopted"+animal.getName());  
      }

      //getters and setters for external access
     
 // due to shelter client class we check if adoption service is initialized
      public List<Animal>getAdoptableAnimals(){
        
        // Return defensive copy to prevent external modification
        return new ArrayList<>(adoptableAnimals);

         
    
}
          
      
      public Map<Donor, Animal> getAdoptionMatches(){

         return new HashMap<>(adoptionMatches);
      }

       public List<Animal> getAvailableAnimals() {
        return getAdoptableAnimals();  
    }
                
          // Returns map of adopted animals (name -> animal)
    public Map<String, Animal> getAdoptedAnimals() {
        // Return defensive copy to prevent external modification
        return new HashMap<>(adopted);
    }

    // Returns list of authorized donors
    public List<Person> getAuthorizedDonors() {
        // Return defensive copy to prevent external modification
        return new ArrayList<>(donors);
    }   
        
    
    

    
    
    }

    




