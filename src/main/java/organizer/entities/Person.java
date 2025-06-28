package organizer.entities;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import organizer.exceptionmanager.EmailException;
import organizer.exceptionmanager.LengthException;
import organizer.exceptionmanager.AgeException;
import organizer.exceptionmanager.DateException;

//Person extends Being as they have attributes in common
public abstract class Person extends Being {

    protected String surname;
    protected String birthDate;
    protected LocalDate date;
    protected int age;
    protected String email;
    protected int telephone;

    //pattern validation
    private static final Pattern DATE_PATTERN=Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,4}");

    public Person (String sex, String name, String surname, String birthDate){
        super(sex, name);

        validateAndSetSurname(surname);
        validateAndSetBirthDate(birthDate);
        calculateAge();
    }
    
  
    @Override
    public String getRole() {
       
        return "Animal Shelter Contributor";
    }
     //validates surname length
    private void validateAndSetSurname(String surname){
        if(surname==null ||surname.trim().isEmpty()){
            throw new LengthException("Surname cannot be null or empty");
        }

        String trimmedSurname = surname.trim();
        if(trimmedSurname.length()<2|| trimmedSurname.length()>20){
            throw new LengthException("Inavlid Lenght! Surname must be 2-20 characters");
        }
        this.surname=trimmedSurname;
    }

     //validates birth date format and sets both string and Local date versions

     private void validateAndSetBirthDate(String birthDate){
        Matcher matcher = DATE_PATTERN.matcher((birthDate));
        if(!matcher.find()){
            throw new IllegalArgumentException("Birth must follow YYYY-MM-DD format");
        }

        try {
            this.date= LocalDate.parse(birthDate);
            this.birthDate=birthDate;
        } catch (DateTimeParseException e){
            throw new DateException("Unexpected error parsing date "+ birthDate);
        }
     }

     //calculates age based on birth date

     private void calculateAge(){
        LocalDate today= LocalDate.now();
        int age= Period.between(date, today).getYears();

        if (age<0|| age> 150){
            throw new AgeException("Invalid age calculated: "+age);
        }
        this.age=age;
     }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if(surname.length() < 2 || surname.length()>20){
            throw new LengthException("Invalid length for surname!");
        } 
        this.surname = surname;
    }

    public String getBirthDate() {
       
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(myFormat);

    }

    public void setBirthDate( String birthDate) {
       
       validateAndSetBirthDate(birthDate);

     }
     
    public String getEmail() {
        return email;
    }

    
    

    public void setEmail(String email) {
        if (email!=null&&!email.trim().isEmpty()){
            Matcher matcher= EMAIL_PATTERN.matcher(email.trim());
      
          if (!matcher.matches()){
            throw new EmailException("Invalid E-mail!");
          }
        
        this.email=email!=null?email.trim():null; //email can be null
    }
}


    public int getTelephone() {
        return telephone;
    }


    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }
    
    
    public int getAge() {
        return age;
    }
    
    //returns formatted birth date for display purpose
    public String getFormattedBirthDate(){

        if(date != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy");
            return date.format(formatter);
        }
        return birthDate;
    }
    @Override
    public String toString() {
        return super.toString()+
         ", Surname=" + this.surname + ", Birth date=" + this.birthDate+", contacts="+ getEmail()+" - "+getTelephone();
    }



    
    

}
