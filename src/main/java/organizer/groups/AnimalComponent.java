package organizer.groups;

import java.time.LocalDate;
import java.util.List;

import organizer.entities.Admin;
import organizer.entities.Animal;

// i create a component declaring operations for single animals or groups
public interface AnimalComponent {

    void performVaccination(String vaccineType, LocalDate date, Admin veterinarian);
    void setUnderMedicalCare(boolean underCare, Admin veterinarian, String condition);
    void performSterilization(LocalDate date, Admin veterinarian);
    
    // operations for structure management ( for composite)
    void add(AnimalComponent component);
    void remove(AnimalComponent component);
    AnimalComponent getChild(int index);
    
    // information operations
    int getCount();
    List<Animal> getAllAnimals();
    void displayInfo();
    String getGroupName();

}
