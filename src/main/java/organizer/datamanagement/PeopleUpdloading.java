package organizer.datamanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import organizer.entities.Admin;
import organizer.entities.Person;
import organizer.entities.Role;
import organizer.entities.Status;
import organizer.exceptionmanager.OasisExceptionShieldingHandler;
import organizer.exceptionmanager.OasisUserException;
import organizer.exceptionmanager.PersonCreationException;
import organizer.entities.Donor;
import static organizer.entities.Admin.loadFromFile;
import static organizer.entities.Donor.loadFromFile;


public class PeopleUpdloading {
    //same procedure as AnimalUploading
     // I create an arraylist as did with AnimalUploading class
    private ArrayList<Person> peopleList= new ArrayList<Person>();
    
    //regex pattern for people data 

    private static final Pattern PERSON_PATTERN = Pattern.compile("^(Admin|Donor)\\s+([FfMm])\\s+(\\w+)\\s+(\\w+)\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\w+)\\s+(VETERINARIAN|ADMIN|DONOR)$");
// I create a method that checks string format (it will be line of my text) through regular expressions
    
    public PeopleUpdloading(String nameFile) throws OasisUserException {

            OasisExceptionShieldingHandler.executeWithShield(() ->{

                List<String> fileLines = Files.readAllLines(Path.of(nameFile)); 

                for(String line:fileLines){
                  processPersonLine(line.trim());
                }

                System.out.println("Successfully loaded "+peopleList.size()+" people");
            },"people uploading");
    }
    //it processed a single line of person data and return an object
    private void processPersonLine(String line){

        if(line.isEmpty()){
             return;
         } //skip empty lines
             Matcher matcher= PERSON_PATTERN.matcher(line);

             if(matcher.find()){
                String[]parts = line.split("\\s+");
                String personType = parts[0];
                String sex = parts[1];
                String name = parts[2];
                String surname = parts[3];
                String birthDate = parts[4];
                String profession = parts[5];
                String roleString = parts[6];

                try {
                    if("Admin".equals(personType)) {

                        createAdminPerson(sex, name, surname, birthDate, profession, roleString);

                    } else if("Donor".equals(personType)&&"DONOR".equals(roleString)){

                        createDonorPerson(sex,name,surname,birthDate);
                    }

                    } catch(Exception e){
                        System.out.println("Failed to create person from line:" +line+" -"+e.getMessage());
                    }
             } else {

                        System.out.println("Invalid person data format, ignored:"+line);

                    }
            }

            //method to create admin person with his appropriate role

            private void createAdminPerson(String sex, String name, String surname, String birthdate, String profession, String roleString) throws PersonCreationException{

                Role role=Role.valueOf(roleString);
                Admin admin = loadFromFile(sex, name, surname, birthdate, profession, role); // admin creation from an uploadin gno need to save again
                peopleList.add(admin);
            }
                

            //method to create donor person 

            private void createDonorPerson(String sex, String name, String surname, String birthdate) throws PersonCreationException{

                Donor donor = loadFromFile(sex, name, surname, birthdate, Role.DONOR); //polymorphism
                peopleList.add(donor);
            }
            
            //returns loaded people list

            public List<Person> peopleList(){
               return new ArrayList<>(peopleList);
            }
   }



