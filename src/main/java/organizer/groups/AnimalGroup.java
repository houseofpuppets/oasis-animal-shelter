package organizer.groups;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import organizer.entities.Admin;
import organizer.entities.Animal;

public class AnimalGroup implements AnimalComponent {
  private List<AnimalComponent> components;
    private String groupName;
    private String groupType; //it could be species or nr od department of the structure
    
    public AnimalGroup(String groupName, String groupType) {
        this.components = new ArrayList<>();
        this.groupName = groupName;
        this.groupType = groupType;
    }
    
    // management composite structure
    @Override
    public void add(AnimalComponent component) {

        if (component == null){
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.add(component);
        System.out.println(" Added to the group " + groupName + ": " + component.getGroupName());
    }
    
    @Override
    public void remove(AnimalComponent component) {
        components.remove(component);
        System.out.println(" Removed from the group " + groupName + " : " + component.getGroupName());
    }
    
    @Override
    public AnimalComponent getChild(int index) {
        
                if (index < 0 || index >= components.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + 
                " (group size: " + components.size() + ")");
        }
        return components.get(index);

    }
    
    //option to perform vaccination to all the components

    @Override
    public void performVaccination(String vaccineType, LocalDate date, Admin veterinarian) {
        performVaccinationWithExclusions(vaccineType, date, veterinarian, null);
    }

    // Recursive clinical operations except excluded components
    
   public void performVaccinationWithExclusions(String vaccineType, LocalDate date, 
                                                 Admin veterinarian, List<String> excludeNames) {
        System.out.println("\n--- Group Vaccination Operation ---");
        System.out.println("Veterinarian: Dr. " + veterinarian.getName());
        System.out.println("Vaccine: " + vaccineType);
        System.out.println("Group: " + groupName + " (" + getCount() + " animals)");
        System.out.println("Date: " + date);
        
        if (excludeNames != null && !excludeNames.isEmpty()) {
            System.out.println("Excluded animals: " + excludeNames);
            validateExclusions(excludeNames);
        }
        
        System.out.println("---");
        
        performOperationOnComponents((component) -> {
            if (!shouldExcludeComponent(component, excludeNames)) {
                component.performVaccination(vaccineType, date, veterinarian);
            } else {
                System.out.println("Skipped: " + component.getGroupName() + " (excluded)");
            }
        }, "vaccination");
        
        System.out.println("Group vaccination completed: " + groupName);
    }

    @Override
    public void setUnderMedicalCare(boolean underCare, Admin veterinarian, String condition) {
        setUnderMedicalCareWithExclusions(underCare, veterinarian, condition, null);
    }

    /**
     * Sets medical care status for group members with exclusion support.
     */
    public void setUnderMedicalCareWithExclusions(boolean underCare, Admin veterinarian, 
                                                  String condition, List<String> excludeNames) {
        String action = underCare ? "MEDICAL CARE ASSIGNMENT" : "MEDICAL CARE DISCHARGE";
        System.out.println("\n--- " + action + " ---");
        System.out.println("Veterinarian: Dr. " + veterinarian.getName());
        System.out.println("Condition: " + condition);
        System.out.println("Group: " + groupName + " (" + getCount() + " animals)");
        
        if (excludeNames != null && !excludeNames.isEmpty()) {
            System.out.println("Excluded animals: " + excludeNames);
            validateExclusions(excludeNames);
        }
        
        System.out.println("---");
        
        performOperationOnComponents((component) -> {
            if (!shouldExcludeComponent(component, excludeNames)) {
                component.setUnderMedicalCare(underCare, veterinarian, condition);
            } else {
                System.out.println("Skipped: " + component.getGroupName() + " (excluded)");
            }
        }, "medical care management");
        
        System.out.println(action + " completed for group: " + groupName);
    }

    @Override
    public void performSterilization(LocalDate date, Admin veterinarian) {
        performSterilizationWithExclusions(date, veterinarian, null);
    }

    /**
     * Performs sterilization on group members with exclusion support.
     */
    public void performSterilizationWithExclusions(LocalDate date, Admin veterinarian, 
                                                   List<String> excludeNames) {
        System.out.println("\n--- Group Sterilization Operation ---");
        System.out.println("Veterinarian: Dr. " + veterinarian.getName());
        System.out.println("Group: " + groupName + " (" + getCount() + " animals)");
        System.out.println("Date: " + date);
        
        if (excludeNames != null && !excludeNames.isEmpty()) {
            System.out.println("Excluded animals: " + excludeNames);
            validateExclusions(excludeNames);
        }
        
        System.out.println("---");
        
        performOperationOnComponents((component) -> {
            if (!shouldExcludeComponent(component, excludeNames)) {
                component.performSterilization(date, veterinarian);
            } else {
                System.out.println("Skipped: " + component.getGroupName() + " (excluded)");
            }
        }, "sterilization");
        
        System.out.println("Group sterilization completed: " + groupName);
    }

    // Helper methods for operation execution

    /**
     * Functional interface for component operations.
     */
    @FunctionalInterface
    private interface ComponentOperation {
        void perform(AnimalComponent component) throws Exception;
    }

    /**
     * Safely performs operation on all components with error handling.
     */
    private void performOperationOnComponents(ComponentOperation operation, String operationName) {
        for (AnimalComponent component : components) {
            try {
                operation.perform(component);
            } catch (Exception e) {
                System.err.println("Error during " + operationName + " on " + 
                                 component.getGroupName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Checks if component should be excluded from operation.
     */
    private boolean shouldExcludeComponent(AnimalComponent component, List<String> excludeNames) {
        if (excludeNames == null || excludeNames.isEmpty()) {
            return false;
        }
        
        if (component instanceof AnimalLeaf) {
            AnimalLeaf leaf = (AnimalLeaf) component;
            String animalName = leaf.getAnimal().getName();
            return excludeNames.contains(animalName);
        }
        
        return false; // Don't exclude composite groups
    }

    /**
     * Validates that all excluded animal names exist in the group.
     */
    private void validateExclusions(List<String> excludeNames) {
        Set<String> animalNamesInGroup = new HashSet<>();
        
        // Collect all animal names in the group
        for (AnimalComponent component : components) {
            if (component instanceof AnimalLeaf) {
                AnimalLeaf leaf = (AnimalLeaf) component;
                animalNamesInGroup.add(leaf.getAnimal().getName());
            }
        }
        
        // Check for missing animals in exclusion list
        List<String> notFound = new ArrayList<>();
        for (String excludeName : excludeNames) {
            if (!animalNamesInGroup.contains(excludeName)) {
                notFound.add(excludeName);
            }
        }
        
        if (!notFound.isEmpty()) {
            throw new IllegalArgumentException(
                "Animals not found in group '" + groupName + "': " + notFound +
                ". Available animals: " + animalNamesInGroup);
        }
    }

    // Information and query operations

    @Override
    public int getCount() {
        return components.stream()
                        .mapToInt(AnimalComponent::getCount)
                        .sum();
    }

    @Override
    public List<Animal> getAllAnimals() {
        List<Animal> allAnimals = new ArrayList<>();
        for (AnimalComponent component : components) {
            allAnimals.addAll(component.getAllAnimals());
        }
        return allAnimals;
    }

    @Override
    public void displayInfo() {
        System.out.println("\n--- Group Information: " + groupName + " ---");
        System.out.println("Type: " + groupType);
        System.out.println("Total animals: " + getCount());
        
        // Calculate and display health statistics
        displayHealthStatistics();
        
        // Display component breakdown
        System.out.println("Group components:");
        for (int i = 0; i < components.size(); i++) {
            AnimalComponent component = components.get(i);
            System.out.println("  " + (i + 1) + ". " + component.getGroupName() +
                             " (" + component.getCount() + " animals)");
        }
    }

    /**
     * Displays general health statistics for the group.
     */
    private void displayHealthStatistics() {
        List<Animal> allAnimals = getAllAnimals();
        long totalCount = allAnimals.size();
        
        long vaccinatedCount = allAnimals.stream().mapToLong(a -> a.isVaccinated() ? 1 : 0).sum();
        long sterilizedCount = allAnimals.stream().mapToLong(a -> a.isSterilized() ? 1 : 0).sum();
        long underCareCount = allAnimals.stream().mapToLong(a -> a.isUnderMedicalCare() ? 1 : 0).sum();
        
        System.out.println("Health Statistics:");
        System.out.println("  Vaccinated: " + vaccinatedCount + "/" + totalCount);
        System.out.println("  Sterilized: " + sterilizedCount + "/" + totalCount);
        System.out.println("  Under medical care: " + underCareCount + "/" + totalCount);
    }

    @Override
    public String getGroupName() {
        return groupName + " (" + groupType + ")";
    }

    // Additional getters for group properties

    public String getGroupType() {
        return groupType;
    }

    public List<AnimalComponent> getComponents() {
        return new ArrayList<>(components); // Return defensive copy
    }

    @Override
    public String toString() {
        return "AnimalGroup{" +
               "name='" + groupName + '\'' +
               ", type='" + groupType + '\'' +
               ", componentCount=" + components.size() +
               ", totalAnimals=" + getCount() +
               '}';
    }

}
