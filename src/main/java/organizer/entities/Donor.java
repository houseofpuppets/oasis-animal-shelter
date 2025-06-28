package organizer.entities;

import  organizer.entities.Status;
import organizer.exceptionmanager.PersonCreationException;
import organizer.entities.Role;

//donor is Donator and could be Adopter or Vistor o None


public class Donor extends Person {

    private Status statusDonator;
    private Role role;

     public Donor (String sex, String name, String surname, String birthDate, Role role ){

        super(sex, name, surname, birthDate);

         if (role!=Role.DONOR){
            throw new IllegalArgumentException("Donor must have DONOR role");
         }
        this.role=Role.DONOR;
        this.statusDonator=Status.None;
        
     
    }


     @Override
    public String getRole() {
        
        return "Shelter Donor";
    }
    
    public Role getRoleEnum(){
        return Role.DONOR;
    }


     public Status getStatusDonator() {
         return statusDonator;
     }
     public void setStatusDonator(Status statusDonator) {
         this.statusDonator = statusDonator;
         System.out.println("Status apdated to "+statusDonator);
     }
    
    private void saveToFile() {
    try {
        String personData = "Donor " + getSex() + " " + getName() + " " + getSurname() + 
                          " " + getBirthDate() + " " + "Donator" + " " + "DONOR";
        
        java.nio.file.Files.write(
            java.nio.file.Paths.get("People-list.txt"), 
            (personData + "\n").getBytes(), 
            java.nio.file.StandardOpenOption.CREATE, 
            java.nio.file.StandardOpenOption.APPEND
        );
        System.out.println(" Donor saved automatically: " + getName());
        
    } catch (java.io.IOException e) {
        System.err.println(" Error saving donor " + getName() + ": " + e.getMessage());
    }
}

    // two methods to manage two different types of object creation
 public void saveDonor() {
        saveToFile();
    }
    
    // saving
    public static Donor createNewDonor (String sex, String name, String surname, String birthDate, Role role) throws PersonCreationException {
        try{
        Donor donor = new Donor(sex, name,  surname,  birthDate, role);
        donor.saveDonor(); 
        return donor;
        }catch(Exception e){
            throw new PersonCreationException("Failure in creating and saving donor: "+e.getMessage(), e);
        }
    }
    
    // not saving
    public static Donor loadFromFile(String sex, String name, String surname, String birthDate, Role role) throws PersonCreationException {

        try{
        Donor donor = new Donor(sex, name,  surname,  birthDate, role);
        return donor;
        }catch (Exception e){
            throw new PersonCreationException("Failure in creatind donor: "+e.getMessage(), e);
        }
    }

     @Override
     public String toString() {
        return this.getClass().getSimpleName()+
        super.toString()+
         ", Status= "+ this.statusDonator;
     }

    

}
