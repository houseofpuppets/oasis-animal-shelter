package organizer.entities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import organizer.exceptionmanager.AnimalCreationException;
import organizer.exceptionmanager.LengthException;
import organizer.exceptionmanager.SexException;



    
 //Test class for Animal 
  //Tests all functionalities 
 
class AnimalTest {

    private Animal animal;
    private Admin veterinarian; 

    @BeforeEach
    void setUp() {
        // Create a test animal before each test
        animal = new Animal("M", "Rex", "Dog");
        
        // Create a test veterinarian for clinical operations
        veterinarian = new Admin("F", "Dr. Martin", "Rossi", "1980-05-15", 
                                "Veterinarian", Role.VETERINARIAN);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        animal = null;
        veterinarian = null;
    }

    // CONSTRUCTOR TEST

    @Test
    @DisplayName("Should create animal with valid data")
    void testValidAnimalCreation() {
        // Test that animal is created correctly with valid parameters
        assertEquals("M", animal.getSex());
        assertEquals("Rex", animal.getName());
        assertEquals("Dog", animal.getSpecies());
        assertEquals("Host Animal", animal.getRole());
        
        // Test initial health status
        assertFalse(animal.isVaccinated());
        assertFalse(animal.isSterilized());
        assertFalse(animal.isUnderMedicalCare());
        
        // Test medical history is initialized
        assertNotNull(animal.getMedicalHistory());
        assertTrue(animal.getMedicalHistory().isEmpty());
        
        // Test registration date is set
        assertNotNull(animal.getRegistrationDate());
    }

    @Test
    @DisplayName("Should throw SexException for invalid sex")
    void testInvalidSexThrowsException() {
        // Test various invalid sex values
        assertThrows(SexException.class, () -> new Animal("X", "Pippo", "cane"));
        assertThrows(SexException.class, () -> new Animal("male", "Mauro", "cane"));
        assertThrows(SexException.class, () -> new Animal("", "Giusy", "cane"));
        assertThrows(SexException.class, () -> new Animal("123", "Rebi", "cane"));
    }

    @Test
    @DisplayName("Should throw LengthException for invalid name")
    void testInvalidNameThrowsException() {
        // Test name too short
        assertThrows(LengthException.class, () -> new Animal("M", "R", "cane"));
        assertThrows(LengthException.class, () -> new Animal("M", "", "cane"));
        
        // Test name too long (over 20 characters)
       
        assertThrows(LengthException.class, () -> new Animal("M", "ahahahhahodneddndbdjwkakajadndndnddahah", "cane"));
        
        // Test null name
        assertThrows(LengthException.class, () -> new Animal("M", null, "cane"));
    }

    @Test
    @DisplayName("Should accept valid sex values regardless of case")
    void testValidSexValues() {
        // Test different valid sex formats
        assertDoesNotThrow(() -> new Animal("M", "Re", "cane"));
        assertDoesNotThrow(() -> new Animal("F", "Luna", "gatto"));
        assertDoesNotThrow(() -> new Animal("m", "Panna", "cane"));
        assertDoesNotThrow(() -> new Animal("f", "Bella", "gatto"));
    }

    // TEST FOR CLINICAL OPERATIONS

    @Test
    @DisplayName("Should perform vaccination correctly")
    void testPerformVaccination() {
        // Initially not vaccinated
        assertFalse(animal.isVaccinated());
        assertNull(animal.getLastVaccinationDate());
        
        // Perform vaccination
        String vaccineType = "Rxf";
        LocalDate vaccinationDate = LocalDate.now();
        animal.performVaccination(vaccineType, vaccinationDate);
        
        // Check vaccination status
        assertTrue(animal.isVaccinated());
        assertEquals(vaccinationDate, animal.getLastVaccinationDate());
        
        // Check medical history
        List<String> history = animal.getMedicalHistory();
        assertEquals(1, history.size()); // after one vaccination it could have only one element
        assertTrue(history.get(0).contains("Vaccine: " + vaccineType));//the first element must contain inserted vaccine type
    }

    @Test
    @DisplayName("Should set sterilization status correctly")
    void testSetSterilizationStatus() {
        // Initially not sterilized
        assertFalse(animal.isSterilized());
        assertNull(animal.getSteriliDate());
        
        // Perform sterilization
        LocalDate sterilizationDate = LocalDate.now();
        animal.setSterilizationStatus(true, sterilizationDate, veterinarian);
        
        // Check sterilization status
        assertTrue(animal.isSterilized());
        assertEquals(sterilizationDate, animal.getSteriliDate());
        
        // Check medical history
        List<String> history = animal.getMedicalHistory(); // same as vaccination
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("Sterilization performed"));
    }

    @Test
    @DisplayName("Should manage medical care status correctly")
    void testSetUnderMedicalCare() {
        // Initially not under medical care
        assertFalse(animal.isUnderMedicalCare());
        assertNull(animal.getAssignedVeterinarian());
        
        // Assign to medical care
        String condition = "Fever";
        animal.setUnderMedicalCare(true, veterinarian, condition);
        
        // Check medical care status
        assertTrue(animal.isUnderMedicalCare());
        assertEquals(veterinarian, animal.getAssignedVeterinarian());
        
        // Check medical history
        List<String> history = animal.getMedicalHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("Under care: " + condition));
        
        // Discharge from medical care
        animal.setUnderMedicalCare(false, veterinarian, "Recovered");
        
        // Check discharge status
        assertFalse(animal.isUnderMedicalCare());
        assertNull(animal.getAssignedVeterinarian());
    }

    @Test
    @DisplayName("Should add medical notes correctly")
    void testAddMedicalNote() {
        // Initially empty medical history
        assertTrue(animal.getMedicalHistory().isEmpty());
        
        // Add medical note
        String note = "Regular checkup - blood analysis required";
        LocalDate date = LocalDate.now();
        animal.addMedicalNote(note, date, veterinarian);
        
        // Check medical history
        List<String> history = animal.getMedicalHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains(note));
        assertTrue(history.get(0).contains(date.toString()));
        assertTrue(history.get(0).contains("Dr."));
        
        // Add another note
        String note2 = "Follow-up examination";
        animal.addMedicalNote(note2, date, veterinarian);
        
        // Check updated history
        history = animal.getMedicalHistory();
        assertEquals(2, history.size());
    }

    // GETTERS AND SETTERS TESTS 

    @Test
    @DisplayName("Should get and set species correctly")
    void testSpeciesGetterSetter() {
        assertEquals("Dog", animal.getSpecies());
        
        animal.setSpecies("gatto");
        assertEquals("gatto", animal.getSpecies());
    }

  
    // ==================== HEALTH SUMMARY TESTS ====================

    @Test
    @DisplayName("Should generate correct health summary")
    void testGetHealthSummary() {
        String summary = animal.getHealthSummary();
        
        // Check that summary contains animal name
        assertTrue(summary.contains("Rex"));
        
        // Check default health status
        assertTrue(summary.contains("Vaccinated: No"));
        assertTrue(summary.contains("Sterilized: No"));
        assertTrue(summary.contains("Under Medical Care: No"));
        
        // Perform some medical operations
        animal.performVaccination("Rabies", LocalDate.now());
        animal.setSterilizationStatus(true, LocalDate.now(), veterinarian);
        
        // Check updated summary
        String updatedSummary = animal.getHealthSummary();
        assertTrue(updatedSummary.contains("Vaccinated: Yes"));
        assertTrue(updatedSummary.contains("Sterilized: Yes"));
    }

    // FACTORY METHODS TESTS 

    @Test
    @DisplayName("Should create new animal with saving")
    void testCreateNewAnimal() {
        assertDoesNotThrow(() -> {
            Animal newAnimal = Animal.createNewAnimal("F", "Luna", "gatto");
            assertNotNull(newAnimal);
            assertEquals("F", newAnimal.getSex());
            assertEquals("Luna", newAnimal.getName());
            assertEquals("gatto", newAnimal.getSpecies());
        });
    }

    @Test
    @DisplayName("Should load animal from file without saving")
    void testLoadFromFile() {
        assertDoesNotThrow(() -> {
            Animal loadedAnimal = Animal.loadFromFile("M", "Buddy", "Golden Retriever");
            assertNotNull(loadedAnimal);
            assertEquals("M", loadedAnimal.getSex());
            assertEquals("Buddy", loadedAnimal.getName());
            assertEquals("Golden Retriever", loadedAnimal.getSpecies());
        });
    }

    @Test
    @DisplayName("Should throw AnimalCreationException for invalid data in factory methods")
    void testFactoryMethodsWithInvalidData() {
        // Test createNewAnimal with invalid data
        assertThrows(AnimalCreationException.class, () -> 
            Animal.createNewAnimal("X", "InvalidSex", "Dog"));
        
        assertThrows(AnimalCreationException.class, () -> 
            Animal.createNewAnimal("M", "A", "Dog")); // Name too short
        
        // Test loadFromFile with invalid data
        assertThrows(AnimalCreationException.class, () -> 
            Animal.loadFromFile("Y", "InvalidSex", "Cat"));
        
        assertThrows(AnimalCreationException.class, () -> 
            Animal.loadFromFile("F", "", "Cat")); // Empty name
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    @DisplayName("Handle multiple medical operations")
    void testMultipleMedicalOperations() {
        // Perform multiple operations
        animal.performVaccination("Rabies", LocalDate.now());
        animal.performVaccination("Distemper", LocalDate.now().plusDays(1));
        animal.setSterilizationStatus(true, LocalDate.now().plusDays(2), veterinarian);
        animal.setUnderMedicalCare(true, veterinarian, "Fever monitoring");
        animal.addMedicalNote("Doing well", LocalDate.now().plusDays(3), veterinarian);
        
        // Check final status
        assertTrue(animal.isVaccinated());
        assertTrue(animal.isSterilized());
        assertTrue(animal.isUnderMedicalCare());
        
        // Check medical history has all entries
        List<String> history = animal.getMedicalHistory();
        assertEquals(5, history.size()); // 2 vaccinations + 1 sterilization + 1 medical care + 1 note
    }

   
    // TOSTRING TESTS 

    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String animalString = animal.toString();
        
        // Check that toString contains key information
        assertTrue(animalString.contains("Rex"));
        assertTrue(animalString.contains("Dog"));
        assertTrue(animalString.contains("species"));
        
        // Should also contain inherited Being information
        assertTrue(animalString.contains("M"));
        assertNotNull(animalString);
        assertFalse(animalString.isEmpty());
    }
}


