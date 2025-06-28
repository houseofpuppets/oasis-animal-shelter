package organizer.entities;
import organizer.datamanagement.SpeciesSet;
import organizer.exceptionmanager.AnimalCreationException;
import organizer.exceptionmanager.LengthException;
import organizer.exceptionmanager.SexException;
import organizer.exceptionmanager.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Animal extends Being implements ClinicalOperations {

    private String species;
    private boolean vaccinated = false;
    private boolean sterilized = false;
    private boolean underMedicalCare = false;
    private Admin currentVeterinarian = null;
    private String medicalCondition = null;
    private List<String> medicalHistory;
    private LocalDate lastVaccinationDate = null;
    private LocalDate sterilizationDate = null;
   



    public Animal (String sex, String name, String species) {
        
        
        super(sex,name);
        this.species=species;
        this.medicalHistory = new ArrayList<>();
        SpeciesSet.specieSet.add(species.toLowerCase()); // i use set to avoid duplicates
        
    }
    
    @Override
    public String getRole(){
        return "Host Animal";
    }
    //methods implemented from ClinicalOperations interface
    @Override
    public void performVaccination(String vaccineType, LocalDate date) {
        //when performed Vaccination this.vaccinated is always true
        this.vaccinated = true;
        this.lastVaccinationDate = date;
        addMedicalNote("Vaccine: " + vaccineType, date, currentVeterinarian);
        System.out.println("Vaccination completed for " + getName() + ": " + vaccineType);
    }
    
    @Override
    public void setSterilizationStatus(boolean sterilized, LocalDate date, Admin veterinarian) {
        this.sterilized = sterilized;
        this.sterilizationDate = date;
        addMedicalNote("Sterilization performed ", date, veterinarian);
    }
    
    @Override
    public void setUnderMedicalCare(boolean underCare, Admin veterinarian, String condition) {
        this.underMedicalCare = underCare;
        this.currentVeterinarian = underCare? veterinarian:null;
        this.medicalCondition = underCare? condition:null;
        addMedicalNote("Under care: " + condition, LocalDate.now(), veterinarian);
        
    }
    
    @Override
    public void addMedicalNote(String note, LocalDate date, Admin veterinarian) {
        String updateNote = date + " - Dr. " + veterinarian + ": " + note;
        medicalHistory.add(updateNote);
    }
    
    //methods that depend on the previous ones
    @Override
    public boolean isVaccinated() { 
        return vaccinated; 
    }
    
    @Override
    public boolean isSterilized() { 
        return sterilized;
     }
    
    @Override
    public boolean isUnderMedicalCare() {
         return underMedicalCare; 
    }
    
    @Override
    public List<String> getMedicalHistory() {
        // we get a copy of the ArrayList
         return new ArrayList<>(medicalHistory);
     }
    


    public String getSpecies() {
        return species;
    }


    public void setSpecies(String species) {
        this.species = species;
       // SpeciesSet.species.add(species);
    }

    public LocalDate getLastVaccinationDate(){
      return lastVaccinationDate;
    }
    
    public LocalDate getSteriliDate(){
        return sterilizationDate;
    }

    public Admin getAssignedVeterinarian(){
        return currentVeterinarian;
    }

        public String getHealthSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Health Status for ").append(getName()).append(":\n");
        summary.append("- Vaccinated: ").append(vaccinated ? "Yes" : "No");
        if (lastVaccinationDate != null) {
            summary.append(" (").append(lastVaccinationDate).append(")");
        }
        summary.append("\n- Sterilized: ").append(sterilized ? "Yes" : "No");
        if (sterilizationDate != null) {
            summary.append(" (").append(sterilizationDate).append(")");
        }
        summary.append("\n- Under Medical Care: ").append(underMedicalCare ? "Yes" : "No");
        if (medicalCondition != null) {
            summary.append(" (").append(medicalCondition).append(")");
        }
        return summary.toString();
    }


    private void saveToFile() {
    try {
        String animalData = "Animal " + getSex() + " " + getName() + " " + species;
        java.nio.file.Files.write(
            java.nio.file.Paths.get("Animal-list.txt"), 
            (animalData + "\n").getBytes(), 
            java.nio.file.StandardOpenOption.CREATE, 
            java.nio.file.StandardOpenOption.APPEND
        );
        System.out.println("Animal automatically saved: " + getName());
        
    } catch (java.io.IOException e) {
        System.err.println(" Error saving Animal " + getName() + ": " + e.getMessage());
    }
}
 /*
  * since animals are instantiated when reading from the file,
   whoever saves an animal must be able to write to the file without creating an infinite loop.
    So I created a factory method to split the two types of creation
  */
    public void saveAnimal() {
        saveToFile();
    }
    
    // saving
    public static Animal createNewAnimal(String sex, String name, String species) throws AnimalCreationException{
      
        try{

        Animal animal = new Animal(sex, name, species);
        animal.saveAnimal(); // Salva esplicitamente
        return animal;
         } catch(Exception e){
            throw new AnimalCreationException("Failed to create animal: " + e.getMessage(), e);

         }

    }
    
    // not saving
    public static Animal loadFromFile(String sex, String name, String species) throws AnimalCreationException {
        try {
        return new Animal(sex, name, species); 
        }catch(Exception e){
            throw new AnimalCreationException("Failed to create animal and save"+ e.getMessage(), e );
        }
       
    }


    public String toString() {
      
        return super.toString() +
         ", species = " + this.species;
    }

    
}


