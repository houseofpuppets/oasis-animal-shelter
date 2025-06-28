package organizer.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import organizer.exceptionmanager.*;

/*
 * I create an abstract class that it will be extended by Person and Animal: abstract because it will never be instantiated and it has attributes that can be reused 
 * by derivatives class. I inserted an abstract method getRole that it will be implemented differently.
 */
public abstract class Being {
    
    private String sex;
    private String name;
    private String registrationDate;

    public Being ( String sex, String name) {

        validateAndSetSex(sex);
        validateAndSetName(name);
        setRegistrationDate();

        
       
    }

    public abstract String getRole();

    private void validateAndSetSex(String sex){

       //  exceptions to ensure that the data entered for sex are formally correct
        
        if( !sex.equalsIgnoreCase("M") && !sex.equalsIgnoreCase("F")) {
          throw new SexException("Invalid input! Sex must be F or M");
        } else {
          this.sex = sex.toUpperCase();  
        }

    }

    private void validateAndSetName(String name){
        // custom exception to ensure data is formally correct
        if(name==null||name.trim().isEmpty()){
            throw new LengthException("Name cannot be null");
        }
        String trimmedName = name.trim(); // i use often trim method to remove whitespaces
       if(trimmedName.length() < 2 || trimmedName.length()>20){
            throw new LengthException("Invalid length for name! Name must be 2-20 characters!");
        } 

         this.name=trimmedName;
    }

    public void setName(String name) {
         validateAndSetName(name);
    }


    private void setRegistrationDate(){

         //I insert the registration date attribute to track when an individual is entered into the database.
         // I do this formatting the output of now() method of LocalDate class

        LocalDate today=LocalDate.now();
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.registrationDate=today.format(myFormat);

    }
     public String getName() {
        return name;
    }

    

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {

        validateAndSetSex(sex);


        
    }
    
    public String getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public String toString() {
        return " Registration Date= "+registrationDate+", sex= "+sex+ ", Name= " + name;
    }

    

   
}
