package organizer.menus;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import organizer.entities.Admin;
import organizer.entities.Animal;
import organizer.groups.*;
import organizer.exceptionmanager.*;

// Enhanced menu for veterinarians with clinical operations and group management

public class VeterinarianMenu extends AdminMenu {
    
    // Current animal group for group operations
    private AnimalGroup currentGroup;
    
    // Constructor initializes veterinarian menu
    public VeterinarianMenu(Admin veterinarian) {
        super(veterinarian);
        this.currentGroup = null;
    }
    
    // Displays veterinarian menu options with clinical operations
    @Override
    public void displayOptions() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("VETERINARIAN MENU - Dr. " + admin.getName());
        System.out.println("Profession: " + admin.getProfession());
        System.out.println("==================================================");
        
        System.out.println("ADMINISTRATION:");
        System.out.println("  1. View All Animals");
        System.out.println("  2. View Staff Members");
        System.out.println("  3. View Scheduled Visits");
        System.out.println("  4. Add New Animal");
        System.out.println("  5. Add Staff Member");
        
        System.out.println();
        System.out.println("CLINICAL OPERATIONS:");
        System.out.println("  6. Individual Animal Care");
        System.out.println("  7. Group Operations");
        System.out.println("  8. Medical History Review");
        System.out.println("  9. Health Status Management");
        
        System.out.println();
        System.out.println("REPORTS:");
        System.out.println("  10. Health Statistics");
        System.out.println("  11. Group Management");
        
        System.out.println();
        System.out.println("SESSION:");
        System.out.println("  12. Logout");
        
        System.out.print("\nSelect option (1-12): ");
    }
    
    // Processes veterinarian's menu choice with clinical operations
    @Override
    public void processUserChoice(int choice) {
        try {
            // Use exception shielding for all operations
            OasisExceptionShieldingHandler.executeWithShield(() -> {
                switch (choice) {
                    case 1, 2, 3, 4, 5 -> super.processUserChoice(choice); // Use parent admin methods
                    case 6 -> performIndividualClinicalOperations();        // Individual animal care
                    case 7 -> performGroupOperations();                     // Group operations
                    case 8 -> viewMedicalHistory();                         // Medical history
                    case 9 -> updateHealthStatus();                         // Health status
                    case 10 -> displayHealthStatistics();                   // Health reports
                    case 11 -> manageAnimalGroups();                       // Group management
                    case 12 -> logout();                                    // End session
                    default -> System.out.println("Invalid option. Please select 1-12.");
                }
            }, "veterinarian menu operation");
            
        } catch (OasisUserException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }
    
    // Performs clinical operations on individual animals
    private void performIndividualClinicalOperations() throws Exception {
        System.out.println();
        System.out.println("INDIVIDUAL CLINICAL OPERATIONS");
        System.out.println("------------------------------");
        System.out.println("1. Vaccination");
        System.out.println("2. Sterilization");
        System.out.println("3. Medical Care Assignment");
        System.out.println("4. Add Medical Note");
        System.out.print("Select operation: ");
        
        // Get operation choice
        int operation = Integer.parseInt(scanner.nextLine().trim());
        
        // Get animal name
        System.out.print("Animal name: ");
        String animalName = scanner.nextLine().trim();
        
        // Find animal in system
        Animal animal = findAnimalByName(animalName);
        if (animal == null) {
            throw new IllegalArgumentException("Animal not found: " + animalName);
        }

        // Perform selected operation
        switch (operation) {
            case 1 -> performVaccination(animal);      // Vaccinate animal
            case 2 -> performSterilization(animal);    // Sterilize animal
            case 3 -> assignMedicalCare(animal);       // Assign to medical care
            case 4 -> addMedicalNote(animal);          // Add medical note
            default -> throw new IllegalArgumentException("Invalid operation selected");
        }
    }
    
    // Performs clinical operations on groups of animals
    private void performGroupOperations() throws Exception {
        System.out.println();
        System.out.println("GROUP CLINICAL OPERATIONS");
        System.out.println("------------------------------");
        
        // Create or select animal group
        AnimalGroup group = createOrSelectGroup();
        if (group == null) return;
        
        // Display group information
        System.out.println("Selected Group: " + group.getGroupName());
        System.out.println("Animals in group: " + group.getCount());
        
        // Show available group operations
        System.out.println();
        System.out.println("Available Operations:");
        System.out.println("1. Group Vaccination");
        System.out.println("2. Group Sterilization");
        System.out.println("3. Group Medical Care Assignment");
        System.out.println("4. Display Group Info");
        System.out.print("Select operation: ");
        
        // Get operation choice and execute
        int operation = Integer.parseInt(scanner.nextLine().trim());
        
        switch (operation) {
            case 1 -> performGroupVaccination(group);     // Vaccinate group
            case 2 -> performGroupSterilization(group);   // Sterilize group
            case 3 -> performGroupMedicalCare(group);     // Assign group to care
            case 4 -> group.displayInfo();               // Show group details
            default -> throw new IllegalArgumentException("Invalid operation selected");
        }
    }
    
    // Creates or selects an animal group for operations
    private AnimalGroup createOrSelectGroup() throws Exception {
        System.out.println();
        System.out.println("Group Creation Options:");
        System.out.println("1. Create group by species");
        System.out.println("2. Create custom group");
        System.out.println("3. Use existing group");
        System.out.print("Choose option: ");
        
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        // Return appropriate group based on choice
        switch (choice) {
            case 1 -> {
                return createGroupBySpecies();  // Group animals by species
            }
            case 2 -> {
                return createCustomGroup();     // Manually select animals
            }
            case 3 -> {
                return currentGroup;           // Use previously created group
            }
            default -> {
                System.out.println("Invalid choice.");
                return null;
            }
        }
    }
    
    // Creates a group containing all animals of specified species
    private AnimalGroup createGroupBySpecies() throws Exception {
        // Load all animals
        List<Animal> animals = dataService.loadAnimals("Animal-list.txt");
        
        // Get species from user
        System.out.print("Enter species for group: ");
        String species = scanner.nextLine().trim();
        
        // Create new group for this species
        AnimalGroup group = new AnimalGroup(species + " Group", "Species-based");
        
        // Add all animals of specified species to group
        int addedCount = 0;
        for (Animal animal : animals) {
            if (animal.getSpecies().equalsIgnoreCase(species)) {
                group.add(new AnimalLeaf(animal));
                addedCount++;
            }
        }
        
        // Check if any animals were found
        if (addedCount == 0) {
            System.out.println("No animals found for species: " + species);
            return null;
        }
        
        System.out.println("Created group with " + addedCount + " animals");
        return group;
    }
    
    // Creates a custom group with manually selected animals
    private AnimalGroup createCustomGroup() throws Exception {
        // Load all animals
        List<Animal> animals = dataService.loadAnimals("Animal-list.txt");
        
        // Get group name from user
        System.out.print("Enter group name: ");
        String groupName = scanner.nextLine().trim();
        
        // Create new custom group
        AnimalGroup group = new AnimalGroup(groupName, "Custom");
        
        // Display available animals for selection
        System.out.println();
        System.out.println("Available Animals:");
        for (int i = 0; i < animals.size(); i++) {
            System.out.println((i + 1) + ". " + animals.get(i).getName() + 
                             " (" + animals.get(i).getSpecies() + ")");
        }
        
        // Get animal selection from user
        System.out.println();
        System.out.println("Enter animal numbers to add (comma-separated, or 'all' for all animals):");
        String input = scanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("all")) {
            // Add all animals to group
            for (Animal animal : animals) {
                group.add(new AnimalLeaf(animal));
            }
        } else {
            // Add selected animals to group
            String[] indices = input.split(",");
            for (String index : indices) {
                try {
                    int idx = Integer.parseInt(index.trim()) - 1;
                    if (idx >= 0 && idx < animals.size()) {
                        group.add(new AnimalLeaf(animals.get(idx)));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number: " + index);
                }
            }
        }
        
        System.out.println("Created group with " + group.getCount() + " animals");
        return group;
    }
    
    // Performs vaccination on group of animals with exclusion support
    private void performGroupVaccination(AnimalGroup group) {
        // Get vaccine information
        System.out.print("Enter vaccine type: ");
        String vaccineType = scanner.nextLine().trim();
        
        // Get vaccination date
        System.out.print("Enter vaccination date (YYYY-MM-DD) or press Enter for today: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);
        
        // Get animals to exclude from vaccination
        List<String> exclusions = getExclusionList(group);
        
        try {
            // Perform vaccination with or without exclusions
            if (exclusions.isEmpty()) {
                group.performVaccination(vaccineType, date, admin);
            } else {
                group.performVaccinationWithExclusions(vaccineType, date, admin, exclusions);
            }
            System.out.println("Group vaccination completed successfully!");
        } catch (Exception e) {
            System.out.println("Group vaccination failed: " + e.getMessage());
        }
    }
    
    // Performs sterilization on group of animals with exclusion support
    private void performGroupSterilization(AnimalGroup group) {
        // Get sterilization date
        System.out.print("Enter sterilization date (YYYY-MM-DD) or press Enter for today: ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);
        
        // Get animals to exclude from sterilization
        List<String> exclusions = getExclusionList(group);
        
        try {
            // Perform sterilization with or without exclusions
            if (exclusions.isEmpty()) {
                group.performSterilization(date, admin);
            } else {
                group.performSterilizationWithExclusions(date, admin, exclusions);
            }
            System.out.println("Group sterilization completed successfully!");
        } catch (Exception e) {
            System.out.println("Group sterilization failed: " + e.getMessage());
        }
    }
    
    // Assigns group of animals to medical care with exclusion support
    private void performGroupMedicalCare(AnimalGroup group) {
        // Get medical condition information
        System.out.print("Enter medical condition/treatment: ");
        String condition = scanner.nextLine().trim();
        
        // Check if assigning to care or discharging
        System.out.println("Assign to medical care? (y/n): ");
        boolean underCare = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        // Get animals to exclude from operation
        List<String> exclusions = getExclusionList(group);
        
        try {
            // Perform medical care assignment with or without exclusions
            if (exclusions.isEmpty()) {
                group.setUnderMedicalCare(underCare, admin, condition);
            } else {
                group.setUnderMedicalCareWithExclusions(underCare, admin, condition, exclusions);
            }
            
            String action = underCare ? "assigned to" : "discharged from";
            System.out.println("Group " + action + " medical care successfully!");
        } catch (Exception e) {
            System.out.println("Group medical care operation failed: " + e.getMessage());
        }
    }
    
    // Gets list of animal names to exclude from group operations
    private List<String> getExclusionList(AnimalGroup group) {
        System.out.print("Animals to exclude (comma-separated names, or press Enter for none): ");
        String exclusionInput = scanner.nextLine().trim();
        
        // Return empty list if no exclusions
        if (exclusionInput.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Parse exclusion list and return
        return Arrays.asList(exclusionInput.split(","))
                     .stream()
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
    
    // Performs vaccination on individual animal
    private void performVaccination(Animal animal) {
        // Get vaccine type
        System.out.print("Vaccine type: ");
        String vaccineType = scanner.nextLine().trim();

        try {
            // Perform vaccination using admin's clinical operation
            admin.performClinicalOperation(animal, "vaccination", vaccineType);
            System.out.println("Vaccination completed for " + animal.getName());
        } catch (Exception e) {
            System.out.println("Vaccination failed: " + e.getMessage());
        }
    }

    // Performs sterilization on individual animal
    private void performSterilization(Animal animal) {
        try {
            // Perform sterilization using admin's clinical operation
            admin.performClinicalOperation(animal, "sterilization", "Standard procedure");
            System.out.println("Sterilization completed for " + animal.getName());
        } catch (Exception e) {
            System.out.println("Sterilization failed: " + e.getMessage());
        }
    }

    // Assigns individual animal to medical care
    private void assignMedicalCare(Animal animal) {
        // Get medical condition
        System.out.print("Medical condition: ");
        String condition = scanner.nextLine().trim();

        try {
            // Assign to medical care using admin's clinical operation
            admin.performClinicalOperation(animal, "medical_care", condition);
            System.out.println(animal.getName() + " assigned to medical care");
        } catch (Exception e) {
            System.out.println("Medical care assignment failed: " + e.getMessage());
        }
    }

    // Adds medical note to individual animal
    private void addMedicalNote(Animal animal) {
        // Get medical note content
        System.out.print("Medical note: ");
        String note = scanner.nextLine().trim();

        try {
            // Add medical note using admin's clinical operation
            admin.performClinicalOperation(animal, "medical_note", note);
            System.out.println("Medical note added for " + animal.getName());
        } catch (Exception e) {
            System.out.println("Note addition failed: " + e.getMessage());
        }
    }

    // Views medical history for specified animal
    private void viewMedicalHistory() {
        // Get animal name
        System.out.print("Animal name: ");
        String animalName = scanner.nextLine().trim();

        // Find animal in system
        Animal animal = findAnimalByName(animalName);
        if (animal == null) {
            System.out.println("Animal not found: " + animalName);
            return;
        }

        // Get and display medical history
        List<String> history = animal.getMedicalHistory();
        if (history.isEmpty()) {
            System.out.println("No medical history for " + animal.getName());
            return;
        }

        System.out.println();
        System.out.println("Medical History - " + animal.getName() + ":");
        System.out.println("------------------------------------------");
        history.forEach(System.out::println);
    }

    // Updates health status for specified animal
    private void updateHealthStatus() {
        // Get animal name
        System.out.print("Animal name: ");
        String animalName = scanner.nextLine().trim();

        // Find animal in system
        Animal animal = findAnimalByName(animalName);
        if (animal == null) {
            System.out.println("Animal not found: " + animalName);
            return;
        }

        // Display current health status
        System.out.println();
        System.out.println("Current Status - " + animal.getName() + ":");
        System.out.println("Vaccinated: " + animal.isVaccinated());
        System.out.println("Sterilized: " + animal.isSterilized());
        System.out.println("Under care: " + animal.isUnderMedicalCare());
        
        // Show health status options
        System.out.println();
        System.out.println("1. Place under care");
        System.out.println("2. Discharge from care");
        System.out.print("Select action: ");
        
        int choice = Integer.parseInt(scanner.nextLine().trim());

        // Perform selected action
        switch (choice) {
            case 1 -> {
                // Place animal under medical care
                System.out.print("Condition: ");
                String condition = scanner.nextLine().trim();
                animal.setUnderMedicalCare(true, admin, condition);
                System.out.println(animal.getName() + " placed under medical care");
            }
            case 2 -> {
                // Discharge animal from medical care
                animal.setUnderMedicalCare(false, admin, "Discharged");
                System.out.println(animal.getName() + " discharged from medical care");
            }
            default -> System.out.println("Invalid choice");
        }
    }

    // Displays comprehensive health statistics for all animals
    private void displayHealthStatistics() {
        try {
            // Use exception shielding for statistics generation
            OasisExceptionShieldingHandler.executeWithShield(() -> {
                // Load all animals
                List<Animal> animals = dataService.loadAnimals("Animal-list.txt");
                
                System.out.println();
                System.out.println("==================================================");
                System.out.println("COMPREHENSIVE HEALTH STATISTICS");
                System.out.println("==================================================");
                
                // Display basic animal statistics
                dataService.displayAnimalStatistics(animals);
                
                // Calculate veterinary-specific statistics
                long needVaccination = animals.stream().mapToLong(a -> !a.isVaccinated() ? 1 : 0).sum();
                long needSterilization = animals.stream().mapToLong(a -> !a.isSterilized() ? 1 : 0).sum();
                long currentlyInCare = animals.stream().mapToLong(a -> a.isUnderMedicalCare() ? 1 : 0).sum();
                
                // Display veterinary priority information
                System.out.println();
                System.out.println("VETERINARY PRIORITY QUEUE:");
                System.out.println("  Animals needing vaccination: " + needVaccination);
                System.out.println("  Animals needing sterilization: " + needSterilization);
                System.out.println("  Animals currently in care: " + currentlyInCare);
                
            }, "health statistics generation");
            
        } catch (OasisUserException e) {
            System.out.println("Failed to generate statistics: " + e.getMessage());
        }
    }
    
    // Manages animal groups (create, view, clear)
    private void manageAnimalGroups() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("ANIMAL GROUP MANAGEMENT");
        System.out.println("========================================");
        
        // Show group management options
        System.out.println("1. Create new group");
        System.out.println("2. Display current group info");
        System.out.println("3. Clear current group");
        System.out.print("Select option: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            // Perform selected group management action
            switch (choice) {
                case 1 -> {
                    // Create new group and set as current
                    currentGroup = createOrSelectGroup();
                    if (currentGroup != null) {
                        System.out.println("Group created and set as current group");
                    }
                }
                case 2 -> {
                    // Display current group information
                    if (currentGroup != null) {
                        currentGroup.displayInfo();
                    } else {
                        System.out.println("No current group selected");
                    }
                }
                case 3 -> {
                    // Clear current group
                    currentGroup = null;
                    System.out.println("Current group cleared");
                }
                default -> System.out.println("Invalid option");
            }
        } catch (Exception e) {
            System.out.println("Group management error: " + e.getMessage());
        }
    }

    // Finds animal by name in the system
    private Animal findAnimalByName(String name) {
        try {
            // Load animals and search by name
            List<Animal> animals = dataService.loadAnimals("Animal-list.txt");
            return animals.stream()
                .filter(animal -> animal.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            System.out.println("Error loading animals: " + e.getMessage());
            return null;
        }
    }

    // Logs out current veterinarian and ends session
    @Override
    public void logout() {
        this.sessionActive = false;
        System.out.println("Veterinarian logged out successfully.");
    }
}
