package organizer.entities;

import organizer.datamanagement.ProfessionalSet;
import organizer.exceptionmanager.*;
import java.time.LocalDate;
import java.util.List;

//fare enum anche per questa classe
public class Admin extends Person {

    private String profession;
    private Role adminRole;
//i create a new class that extends Person containing a new attributes, having privilege to update collections of data
   public Admin (String sex, String name,  String surname, String birthDate, String profession, Role adminRole){
        super(sex,name, surname, birthDate);
           validateAdminRole(adminRole);
           this.profession=profession;
           ProfessionalSet.professions.add(profession.toLowerCase()); // i add the profession to the set 
           this.adminRole = adminRole;

          
           
   }
   //as Admin could have only ADMIN or VETERINIAN Role 

   private void validateAdminRole(Role role){
      if(role != Role.ADMIN && role != Role.VETERINARIAN){
          throw new IllegalArgumentException("Admin can only have ADMIN or VETERINARIAN role");
        }

   }

   //through this method an Admin instance ( veterinarian Role) can perform vet operation on animal

  public void performClinicalOperation(Animal animal, String operation, String details) {
        if (!isVeterinarian()) {
            throw new UnsupportedOperationException("Clinical operations are only available for veterinarians");
        }
        
        switch (operation.toLowerCase()) {
            case "vaccination":
                animal.performVaccination(details, LocalDate.now());
                break;
            case "sterilization":
                animal.setSterilizationStatus(true, LocalDate.now(), this);
                break;
            case "medical_care":
                animal.setUnderMedicalCare(true, this, details);
                break;
            case "medical_note":
                animal.addMedicalNote(details, LocalDate.now(), this);
                break;
            default:
                throw new IllegalArgumentException("Unknown clinical operation: " + operation);
        }

        System.out.println("Clinical operation"+operation+" performed by D. "+getName()+ " "+getSurname()+" on" +animal.getName());
    }


  //methods that control Role and the permitted operations 
   public boolean isVeterinarian() {
        return adminRole == Role.VETERINARIAN;
    }

    public boolean isAdmin() {
        return adminRole == Role.ADMIN;
    }

    public boolean canPerformClinicalOperations() {
        return isVeterinarian();
    }

   @Override
  public String getRole() {
    
    return "Shelter Staff ("+adminRole.toString().toLowerCase()+")";
  }



   public String getProfession() {
    return profession;
   }

   public void setProfession(String profession) {
    this.profession = profession;
    ProfessionalSet.professions.add(profession);
   }

     public Role getAdminRole() {
      return adminRole;
    }

private void saveToFile() {
    try {
        String roleString = adminRole == Role.VETERINARIAN ? "VETERINARIAN" : "ADMIN";
        String personData = "Admin " + getSex() + " " + getName() + " " + getSurname() + 
                          " " + getBirthDate() + " " + profession + " " + roleString;
        
        java.nio.file.Files.write(
            java.nio.file.Paths.get("People-list.txt"), 
            (personData + "\n").getBytes(), 
            java.nio.file.StandardOpenOption.CREATE, 
            java.nio.file.StandardOpenOption.APPEND
        );
        System.out.println(" Admin saved automatically: " + getName());
        
    } catch (java.io.IOException e) {
        System.err.println(" Error saving admin " + getName() + ": " + e.getMessage());
    }
}
//as in animal two methods to manage two different types of object creation
 public void saveAdmin() {
        saveToFile();
    }
    
    // saving
    public static Admin createNewAdmin(String sex, String name,  String surname, String birthDate, String profession, Role adminRole) throws PersonCreationException {

        try {
        Admin admin = new Admin(sex, name,  surname,  birthDate,  profession, adminRole);
        admin.saveAdmin(); 
        return admin;
        }catch(Exception e){
            throw new PersonCreationException("Failed to create and save admin: "+e.getMessage(), e);
        }
    }
    
    // not saving
    public static Admin loadFromFile(String sex, String name,  String surname, String birthDate, String profession, Role adminRole) throws PersonCreationException {
        try{
        Admin admin = new Admin(sex, name,  surname,  birthDate,  profession, adminRole);
        return admin;
        }catch(Exception e){
            throw new PersonCreationException("Failed to create admin: "+e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+
        super.toString()+
          " , job title=" + this.profession +", Role: "+adminRole;
   
}



  

}
