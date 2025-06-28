package organizer.datamanagement;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;

import static organizer.entities.Animal.loadFromFile;


import organizer.entities.Animal;
import organizer.exceptionmanager.*;

//handles loading and parsing of animal data from files

public class AnimalUploading {

    // arraylist that will contain the lines that I will load
    private ArrayList<Animal> animalList= new ArrayList<Animal>();
    

    // regex pattern for validating animal data format: "Animal [sex] [name] [species]"
    private static final Pattern ANIMAL_PATTERN = Pattern.compile("^Animal\\s+([FfMm])\\s+(\\w{2,20})\\s+(\\w{2,20})$");
    

    //costructor load animals using exception shielding

    public AnimalUploading(String nameFile) throws OasisUserException {
        

             OasisExceptionShieldingHandler.executeWithShield(() -> {
                
                List<String> fileLines = Files.readAllLines(Path.of(nameFile)); //it returns a list of strings that is analized through for-cycle and processAnimalLine method
                for(String line:fileLines) {
                  processAnimalLine(line.trim());
                }
                System.out.println("Successfully loaded"+animalList.size()+"animals");
             }, "uploading animals");
        
    }

    //it processes a single line using regex validation and it creates animal objects only for line matching the given format
    private void processAnimalLine(String line) throws AnimalCreationException{
      if(line.isEmpty()){
        return; // for empty lines
      }
      //if the text line matches the pattern I split the line for animal object creation 
      Matcher matcher =ANIMAL_PATTERN.matcher(line);
      if(matcher.find()){
          String[] parts = line.split("\\s+");
          Animal newAnimal= loadFromFile(parts[1], parts[2], parts[3]);
          animalList.add(newAnimal);

      } else {
        System.out.println("Invalid data format, ignored: ");
      }

    }
   
       //returns the list with loaded animals

       public List<Animal> animalList(){
        return new ArrayList<>(animalList);//defensive copy
       }
  
}


          

    

    
