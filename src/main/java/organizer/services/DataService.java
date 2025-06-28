package organizer.services;



import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import organizer.entities.Admin;
import organizer.entities.Animal;
import organizer.entities.Donor;
import organizer.entities.Person;
import organizer.entities.Status;
import organizer.entities.Role;

public class DataService {

       // Regex patterns for data validation
    private static final Pattern ANIMAL_PATTERN = 
        Pattern.compile("^Animal\\s+([FfMm])\\s+(\\w{2,20})\\s+(\\w{2,20})$");
    private static final Pattern PERSON_PATTERN = 
        Pattern.compile("^(Admin|Donor)\\s+([FfMm])\\s+(\\w+)\\s+(\\w+)\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\w+)\\s+(VETERINARIAN|ADMIN|DONOR)$");

    
      //Loads animals from specified file with validation and error handling.
      //Replaces AnimalUploading logic with improved structure and error reporting.
     
    public List<Animal> loadAnimals(String filename) throws Exception {
        List<String> fileLines = Files.readAllLines(Path.of(filename));
        List<Animal> animalList = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        for (String line : fileLines) {
            lineNumber++;
            String trimmedLine = line.trim();
            
            if (trimmedLine.isEmpty()) {
                continue; // Skip empty lines
            }
            
            try {
                Animal animal = parseAnimalLine(trimmedLine);
                if (animal != null) {
                    animalList.add(animal);
                    successCount++;
                }
            } catch (Exception e) {
                errorCount++;
                System.err.println("Warning: Invalid animal data at line " + lineNumber + 
                                 ": " + trimmedLine + " - " + e.getMessage());
            }
        }

        System.out.println("Animal loading completed: " + successCount + " loaded, " + 
                          errorCount + " errors from " + fileLines.size() + " lines");
        
        if (animalList.isEmpty() && !fileLines.isEmpty()) {
            throw new Exception("No valid animal data found in file: " + filename);
        }

        return animalList;
    }

    
     //Loads people from specified file with role-based instantiation.
     //Replaces PeopleUploading logic with improved validation and error handling.
  
    public List<Person> loadPersons(String filename) throws Exception {
        List<String> fileLines = Files.readAllLines(Path.of(filename));
        List<Person> peopleList = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        for (String line : fileLines) {
            lineNumber++;
            String trimmedLine = line.trim();
            
            if (trimmedLine.isEmpty()) {
                continue; // Skips empty lines
            }
            
            try {
                Person person = parsePersonLine(trimmedLine);
                if (person != null) {
                    peopleList.add(person);
                    successCount++;
                }
            } catch (Exception e) {
                errorCount++;
                System.err.println("Warning: Invalid person data at line " + lineNumber + 
                                 ": " + trimmedLine + " - " + e.getMessage());
            }
        }

        System.out.println("People loading completed: " + successCount + " loaded, " + 
                          errorCount + " errors from " + fileLines.size() + " lines");
        
        if (peopleList.isEmpty() && !fileLines.isEmpty()) {
            throw new Exception("No valid person data found in file: " + filename);
        }

        return peopleList;
    }

    
      //Performs adoption process with comprehensive validation.
     //Centralizes adoption business logic with improved error handling.
 
    public void performAdoption(List<Animal> availableAnimals, List<Person> authorizedDonors,
                               Donor donor, String selectedSpecies,
                               Map<String, Animal> adoptedAnimals, Map<Donor, Animal> adoptionMatches) 
                               throws Exception {
        
        // Validate system state
        validateAdoptionPreconditions(availableAnimals, authorizedDonors, donor);
        
        // Verify donor authorization
        validateDonorAuthorization(donor, authorizedDonors);
        
        // Find available animal of requested species
        Animal selectedAnimal = findAvailableAnimalBySpecies(availableAnimals, selectedSpecies);
        
        // Complete adoption process
        executeAdoptionTransaction(donor, selectedAnimal, availableAnimals, adoptedAnimals, adoptionMatches);
        
        System.out.println("Adoption completed successfully: " + donor.getName() + 
                          " adopted " + selectedAnimal.getName() + " (" + selectedSpecies + ")");
    }

    // Private helper methods for data parsing

    
    private Animal parseAnimalLine(String line) {
        Matcher matcher = ANIMAL_PATTERN.matcher(line);
        
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid animal data format");
        }
        
        String[] parts = line.split("\\s+");
        return new Animal(parts[1], parts[2], parts[3]);
    }

    /**
     * Parses single line of person data into appropriate Person subclass.
     */
    private Person parsePersonLine(String line) {
        Matcher matcher = PERSON_PATTERN.matcher(line);
        
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid person data format");
        }
        
        String[] parts = line.split("\\s+");
        String personType = parts[0];
        String sex = parts[1];
        String name = parts[2];
        String surname = parts[3];
        String birthDate = parts[4];
        String profession = parts[5];
        String roleString = parts[6];
        
        if ("Admin".equals(personType)) {
            return createAdminPerson(sex, name, surname, birthDate, profession, roleString);
        } else if ("Donor".equals(personType) && "DONOR".equals(roleString)) {
            return createDonorPerson(sex, name, surname, birthDate);
        } else {
            throw new IllegalArgumentException("Unsupported person type or role combination");
        }
    }

    /**
     * Creates Admin person with role validation.
     */
    private Admin createAdminPerson(String sex, String name, String surname, 
                                   String birthDate, String profession, String roleString) {
        Role role = Role.valueOf(roleString);
        return new Admin(sex, name, surname, birthDate, profession, role);
    }

    /**
     * Creates Donor person with DONOR role.
     */
    private Donor createDonorPerson(String sex, String name, String surname, String birthDate) {
        return new Donor(sex, name, surname, birthDate, Role.DONOR);
    }

    // Private helper methods for adoption processing

    /**
     * Validates basic preconditions for adoption process.
     */
    private void validateAdoptionPreconditions(List<Animal> availableAnimals, 
                                              List<Person> authorizedDonors, Donor donor) throws Exception {
        if (availableAnimals == null || availableAnimals.isEmpty()) {
            throw new IllegalStateException("No animals available for adoption at this time");
        }
        
        if (authorizedDonors == null || authorizedDonors.isEmpty()) {
            throw new IllegalStateException("No authorized donors in system");
        }
        
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
        }
    }

    
      //Validates that donor is authorized for adoptions.
     
    private void validateDonorAuthorization(Donor donor, List<Person> authorizedDonors) throws Exception {
        boolean isAuthorized = authorizedDonors.stream()
                                              .anyMatch(person -> person.getName().equalsIgnoreCase(donor.getName()));
        
        if (!isAuthorized) {
            throw new SecurityException("Donor '" + donor.getName() + "' is not authorized for adoptions");
        }
    }

    
     //Finds available animal of specified species with detailed error reporting.
    
    private Animal findAvailableAnimalBySpecies(List<Animal> availableAnimals, String selectedSpecies) 
            throws Exception {
        
        if (selectedSpecies == null || selectedSpecies.trim().isEmpty()) {
            throw new IllegalArgumentException("Species selection cannot be empty");
        }
        
        List<Animal> matchingAnimals = availableAnimals.stream()
                                                      .filter(animal -> animal.getSpecies().equalsIgnoreCase(selectedSpecies.trim()))
                                                      .collect(Collectors.toList());
        
        if (matchingAnimals.isEmpty()) {
            // Provide helpful information about available species
            Set<String> availableSpecies = availableAnimals.stream()
                                                          .map(Animal::getSpecies)
                                                          .collect(Collectors.toSet());
            throw new IllegalArgumentException("No animals of species '" + selectedSpecies + 
                "' available. Available species: " + availableSpecies);
        }
        
        return matchingAnimals.get(0); // Return first available animal of requested species
    }

    private void executeAdoptionTransaction(Donor donor, Animal animal, 
                                          List<Animal> availableAnimals,
                                          Map<String, Animal> adoptedAnimals, 
                                          Map<Donor, Animal> adoptionMatches) {
        // Remove animal from available pool
        availableAnimals.remove(animal);
        
        // Add to adoption tracking structures
        adoptedAnimals.put(animal.getName(), animal);
        adoptionMatches.put(donor, animal);
        
        // Update donor status
        donor.setStatusDonator(Status.Adopter);
        
        System.out.println("Adoption transaction completed: " + animal.getName() + 
                          " removed from available pool and assigned to " + donor.getName());
    }

    
     //Provides summary statistics for loaded animal data.
     
    public void displayAnimalStatistics(List<Animal> animals) {
        if (animals.isEmpty()) {
            System.out.println("No animals in system");
            return;
        }
        
        Map<String, Long> speciesCount = animals.stream()
                                               .collect(Collectors.groupingBy(Animal::getSpecies, 
                                                       Collectors.counting()));
        
        long vaccinatedCount = animals.stream().mapToLong(a -> a.isVaccinated() ? 1 : 0).sum();
        long sterilizedCount = animals.stream().mapToLong(a -> a.isSterilized() ? 1 : 0).sum();
        long underCareCount = animals.stream().mapToLong(a -> a.isUnderMedicalCare() ? 1 : 0).sum();
        
        System.out.println("\n--- Animal Statistics ---");
        System.out.println("Total animals: " + animals.size());
        System.out.println("Species breakdown: " + speciesCount);
        System.out.println("Health status:");
        System.out.println("  Vaccinated: " + vaccinatedCount);
        System.out.println("  Sterilized: " + sterilizedCount);
        System.out.println("  Under medical care: " + underCareCount);
    }

    /**
     * Provides summary statistics for loaded people data.
     */
    public void displayPeopleStatistics(List<Person> people) {
        if (people.isEmpty()) {
            System.out.println("No people in system");
            return;
        }
        
        long adminCount = people.stream().mapToLong(p -> p instanceof Admin ? 1 : 0).sum();
        long donorCount = people.stream().mapToLong(p -> p instanceof Donor ? 1 : 0).sum();
        long veterinarianCount = people.stream()
                                      .filter(p -> p instanceof Admin)
                                      .map(p -> (Admin) p)
                                      .mapToLong(a -> a.isVeterinarian() ? 1 : 0)
                                      .sum();
        
        System.out.println("\n--- People Statistics ---");
        System.out.println("Total people: " + people.size());
        System.out.println("Staff members: " + adminCount);
        System.out.println("  Veterinarians: " + veterinarianCount);
        System.out.println("  General admin: " + (adminCount - veterinarianCount));
        System.out.println("Donors: " + donorCount);
    }


   
}



