package organizer.groups;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import organizer.entities.Admin;
import organizer.entities.Animal;
//represents single animals in the composite structure
public class AnimalLeaf implements AnimalComponent{

    private Animal animal;
    
    public AnimalLeaf(Animal animal) {

        if(animal==null){
            throw new IllegalArgumentException("Animal cannot be null");
        }
        this.animal = animal;
    }
    //methods that delegate clinical operation to animal
    @Override
    public void performVaccination(String vaccineType, LocalDate date, Admin veterinarian) {
        animal.performVaccination(vaccineType, date);
        System.out.println(" Vaccination " + vaccineType + " performed on " + animal.getName() + 
                          " by = " + veterinarian.getName());
    }
    
    @Override
    public void setUnderMedicalCare(boolean underCare, Admin veterinarian, String condition) {
        animal.setUnderMedicalCare(underCare, veterinarian, condition);
        String action = underCare ? "under care" : "discharged";
        System.out.println( animal.getName() + " " + action + " by Dr. " + veterinarian.getName() + 
                          " for: " + condition);
    }
    
    @Override
    public void performSterilization(LocalDate date, Admin veterinarian) {
        animal.setSterilizationStatus(true, date, veterinarian);
        System.out.println(" Sterilization performed on " + animal.getName() + 
                          " by Dr. " + veterinarian.getName());
    }
    
    // Methods for composite structure - not for leaf
    @Override
    public void add(AnimalComponent component) {
        throw new UnsupportedOperationException("Impossible to add components to a single animal");
    }
    
    @Override
    public void remove(AnimalComponent component) {
        throw new UnsupportedOperationException("Impossible to remove componenet to a single aniaml");
    }
    
    @Override
    public AnimalComponent getChild(int index) {
        throw new UnsupportedOperationException("A single animals has not child");
    }
    
    @Override
    public int getCount() {
        return 1;// single animal always counts as 1
    }
    
    @Override
    public List<Animal> getAllAnimals() {  //list containing this single animal
        return Arrays.asList(animal);
    }
    
    @Override
    public void displayInfo() {
        System.out.println("Animal" + animal.getName() + " (" + animal.getSpecies() + ") - " +
                          "Vaccinated: " + (animal.isVaccinated() ? "YES" : "NO") + 
                          ", Sterilized: " + (animal.isSterilized() ? "YES" : "NO") +
                          ", Under care: " + (animal.isUnderMedicalCare() ? "YES" : "NO"));
    }
    
    @Override
    public String getGroupName() { //individual animal name serve as group name
        return animal.getName();
    }
    
    public Animal getAnimal() {
        return animal;
    }

        @Override
    public String toString() {
        return "AnimalLeaf{" + animal.getName() + " (" + animal.getSpecies() + ")}";
    }


}
