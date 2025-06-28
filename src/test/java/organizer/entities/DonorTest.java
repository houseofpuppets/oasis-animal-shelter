package organizer.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import organizer.exceptionmanager.*;


 // Test class for Donor 

 
class DonorTest {

    private Donor donor;
    private Donor anotherDonor;

    @BeforeEach
    void setUp() {
        // Create test donors
        donor = new Donor("M", "John", "Smith", "1990-05-20", Role.DONOR);
        anotherDonor = new Donor("F", "Alice", "Johnson", "1985-12-10", Role.DONOR);
    }

    @AfterEach
    void tearDown() {
        donor = null;
        anotherDonor = null;
    }

    // CONSTRUCTOR TESTS

    @Test
    @DisplayName("Should create donor with valid data")
    void testValidDonorCreation() {
        // Test basic donor properties
        assertEquals("M", donor.getSex());
        assertEquals("John", donor.getName());
        assertEquals("Smith", donor.getSurname());
        assertEquals("1990-05-20", donor.getBirthDate());
        assertEquals(Role.DONOR, donor.getRoleEnum());
        
        // Test initial status
        assertEquals(Status.None, donor.getStatusDonator());
    
        
        // Test that donor is properly initialized
        assertNotNull(donor.getRegistrationDate());
        assertTrue(donor.getAge() > 0);
    }

    @Test
    @DisplayName("Should create female donor correctly")
    void testFemaleDonorCreation() {
        assertEquals("F", anotherDonor.getSex());
        assertEquals("Alice", anotherDonor.getName());
        assertEquals("Johnson", anotherDonor.getSurname());
        assertEquals(Role.DONOR, anotherDonor.getRoleEnum());
        assertEquals(Status.None, anotherDonor.getStatusDonator());
    }

    @Test
    @DisplayName("Should throw PersonCreationException for invalid data")
    void testInvalidDonorCreation() {
        // Test invalid sex
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("X", "John", "Doe", "1990-01-01", Role.DONOR);
        });
        
        // Test invalid name (too short)
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("M", "J", "Doe", "1990-01-01", Role.DONOR);
        });
        
        // Test invalid name (too long)
        String longName = "ThisNameIsTooLongForValidation";
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("F", longName, "Doe", "1990-01-01", Role.DONOR);
        });
        
        // Test invalid surname (too short)
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("M", "John", "D", "1990-01-01", Role.DONOR);
        });
        
        // Test invalid birth date format
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("F", "Jane", "Doe", "1990/01/01", Role.DONOR);
        });
        
        // Test null name
        assertThrows(PersonCreationException.class, () -> {
            Donor.createNewDonor("M", null, "Doe", "1990-01-01", Role.DONOR);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for non-DONOR role")
    void testInvalidRoleCreation() {
        // Donor must have DONOR role, not ADMIN or VETERINARIAN
        assertThrows(IllegalArgumentException.class, () -> {
            new Donor("M", "John", "Doe", "1990-01-01", Role.ADMIN);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Donor("F", "Jane", "Doe", "1990-01-01", Role.VETERINARIAN);
        });
    }

    //STATIUS tets

    @Test
    @DisplayName("Should set and get donor status correctly")
    void testStatusManagement() {
        // Initial status should be None
        assertEquals(Status.None, donor.getStatusDonator());
        
        // Change to Visitor status
        donor.setStatusDonator(Status.Visitor);
        assertEquals(Status.Visitor, donor.getStatusDonator());
        
        // Change to Adopter status
        donor.setStatusDonator(Status.Adopter);
        assertEquals(Status.Adopter, donor.getStatusDonator());
        
        // Change back to None
        donor.setStatusDonator(Status.None);
        assertEquals(Status.None, donor.getStatusDonator());
    }

    @Test
    @DisplayName("Should handle all possible status transitions")
    void testAllStatusTransitions() {
        // Test None → Visitor
        donor.setStatusDonator(Status.Visitor);
        assertEquals(Status.Visitor, donor.getStatusDonator());
        
        // Test Visitor → Adopter
        donor.setStatusDonator(Status.Adopter);
        assertEquals(Status.Adopter, donor.getStatusDonator());
        
        // Test Adopter → None 
        donor.setStatusDonator(Status.None);
        assertEquals(Status.None, donor.getStatusDonator());
        
        // Test direct None → Adopter (with adoption)
        donor.setStatusDonator(Status.Adopter);
        assertEquals(Status.Adopter, donor.getStatusDonator());
        
        // Test Adopter → Visitor (after cancelling adoption)
        donor.setStatusDonator(Status.Visitor);
        assertEquals(Status.Visitor, donor.getStatusDonator());
    }

    

     //ROLE VALIDATION TESTS 

    @Test
    @DisplayName("Should validate DONOR role correctly")
    void testRoleValidation() {
        // Donor should only accept DONOR role
        assertDoesNotThrow(() -> {
            new Donor("M", "Test", "User", "1990-01-01", Role.DONOR);
        });
        
        // Verify role is set correctly ( not enum)
        assertEquals("Shelter Donor", donor.getRole());
    }

    // FACTORY METHODS TESTS 

    @Test
    @DisplayName("Should create new donor with saving")
    void testCreateNewDonor() {
        assertDoesNotThrow(() -> {
            Donor newDonor = Donor.createNewDonor("F", "Emma", "Wilson", "1992-08-15", Role.DONOR);
            assertNotNull(newDonor);
            assertEquals("Emma", newDonor.getName());
            assertEquals("Wilson", newDonor.getSurname());
            assertEquals("F", newDonor.getSex());
            assertEquals(Status.None, newDonor.getStatusDonator());
            assertEquals("Shelter Donor", newDonor.getRole());
        });
    }

    @Test
    @DisplayName("Should load donor from file without saving")
    void testLoadFromFile() {
        assertDoesNotThrow(() -> {
            Donor loadedDonor = Donor.loadFromFile("M", "Michael", "Brown", "1988-03-25", Role.DONOR);
            assertNotNull(loadedDonor);
            assertEquals("Michael", loadedDonor.getName());
            assertEquals("Brown", loadedDonor.getSurname());
            assertEquals("M", loadedDonor.getSex());
            assertEquals(Status.None, loadedDonor.getStatusDonator());
            assertEquals("Shelter Donor", loadedDonor.getRole());
        });
    }

    @Test
    @DisplayName("Should throw PersonCreationException for invalid data in factory methods")
    void testFactoryMethodsWithInvalidData() {
        // Test createNewDonor with invalid data
        assertThrows(PersonCreationException.class, () -> 
            Donor.createNewDonor("X", "InvalidSex", "Test", "1990-01-01", Role.DONOR));
        
        assertThrows(PersonCreationException.class, () -> 
            Donor.createNewDonor("M", "A", "TooShort", "1990-01-01", Role.DONOR));
        
        assertThrows(PersonCreationException.class, () -> 
            Donor.createNewDonor("F", "Jane", "Doe", "invalid-date", Role.DONOR));
        
        // Test loadFromFile with invalid data
        assertThrows(PersonCreationException.class, () -> 
            Donor.loadFromFile("Y", "InvalidSex", "Test", "1990-01-01", Role.DONOR));
        
        assertThrows(PersonCreationException.class, () -> 
            Donor.loadFromFile("M", "", "EmptyName", "1990-01-01", Role.DONOR));
    }

    //  Test about inheritance

    @Test
    @DisplayName("Should correctly inherit from Person class")
    void testPersonInheritance() {
        // Test inherited methods from Person
        assertNotNull(donor.getBirthDate());
        assertTrue(donor.getAge() > 0);
        assertNotNull(donor.getRegistrationDate());
        
       
        Person person = donor;
        assertEquals("Shelter Donor", person.getRole());
        
        // Test that donor is instance of Person and Being
        assertTrue(donor instanceof Person);
        assertTrue(donor instanceof Being);
    }

    @Test
    @DisplayName("Should calculate age correctly")
    void testAgeCalculation() {
        // Donor born in 1990 should be around 34-35 years old in 2024
        assertTrue(donor.getAge() >= 30);
        assertTrue(donor.getAge() <= 40);
        
        // Another donor born in 1985 should be around 39-40 years old
        assertTrue(anotherDonor.getAge() >= 35);
        assertTrue(anotherDonor.getAge() <= 45);
    }

    // test for maximum and minimum

    @Test
    @DisplayName("Should handle minimum and maximum name lengths")
    void testNameLengthLimits() {
        // Test minimum length (2 characters)
        assertDoesNotThrow(() -> {
            new Donor("M", "Al", "Bo", "1990-01-01", Role.DONOR);
        });
        
        // Test maximum length (20 characters)
        String maxName = "TwentyCharacterName1"; // Exactly 20 characters
        assertDoesNotThrow(() -> {
            new Donor("F", maxName, maxName, "1990-01-01", Role.DONOR);
        });
    }

    @Test
    @DisplayName("Should handle case-insensitive sex values")
    void testCaseInsensitiveSex() {
        // Test different valid sex formats
        assertDoesNotThrow(() -> new Donor("M", "John", "Doe", "1990-01-01", Role.DONOR));
        assertDoesNotThrow(() -> new Donor("F", "Jane", "Doe", "1990-01-01", Role.DONOR));
        assertDoesNotThrow(() -> new Donor("m", "Bob", "Smith", "1990-01-01", Role.DONOR));
        assertDoesNotThrow(() -> new Donor("f", "Alice", "Jones", "1990-01-01", Role.DONOR));
        
        // Verify sex is stored in uppercase
        Donor maleDonor = new Donor("m", "Test", "Male", "1990-01-01", Role.DONOR);
        assertEquals("M", maleDonor.getSex());
        
        Donor femaleDonor = new Donor("f", "Test", "Female", "1990-01-01", Role.DONOR);
        assertEquals("F", femaleDonor.getSex());
    }

    @Test
    @DisplayName("Should handle various birth date formats correctly")
    void testBirthDateFormats() {
        // Test valid date formats
        assertDoesNotThrow(() -> {
            new Donor("M", "Test", "User", "1990-01-01", Role.DONOR);
        });
        
        assertDoesNotThrow(() -> {
            new Donor("F", "Test", "User", "2000-12-31", Role.DONOR);
        });
        
        // Test edge dates
        assertDoesNotThrow(() -> {
            new Donor("M", "Test", "User", "1950-01-01", Role.DONOR);
        });
    }

    // TO STRING TEST

    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String donorString = donor.toString();
        
        // Check that toString contains key information
        assertTrue(donorString.contains("John"));
        assertTrue(donorString.contains("Smith"));
        assertTrue(donorString.contains("Donor")); // Class name
        assertTrue(donorString.contains("None")); // Initial status
        
        // Should also contain inherited Person information
        assertTrue(donorString.contains("M"));
        assertNotNull(donorString);
        assertFalse(donorString.isEmpty());
        
        // Test toString with different status
        donor.setStatusDonator(Status.Adopter);
        String updatedString = donor.toString();
        assertTrue(updatedString.contains("Adopter"));
    }

    @Test
    @DisplayName("Should differentiate toString for different statuses")
    void testToStringWithDifferentStatuses() {
        // Test None status
        donor.setStatusDonator(Status.None);
        String noneString = donor.toString();
        assertTrue(noneString.contains("None"));
        
        // Test Visitor status
        donor.setStatusDonator(Status.Visitor);
        String visitorString = donor.toString();
        assertTrue(visitorString.contains("Visitor"));
        
        // Test Adopter status
        donor.setStatusDonator(Status.Adopter);
        String adopterString = donor.toString();
        assertTrue(adopterString.contains("Adopter"));
    }

    //  LOGIC TESTS 

    @Test
    @DisplayName("Should maintain status consistency")
    void testStatusConsistency() {
        
        
        // 1. Start as None (new donor)
        assertEquals(Status.None, donor.getStatusDonator());
        
        // 2. Books visit → becomes Visitor
        donor.setStatusDonator(Status.Visitor);
        assertEquals(Status.Visitor, donor.getStatusDonator());
        
        // 3. Adopts animal → becomes Adopter
        donor.setStatusDonator(Status.Adopter);
        assertEquals(Status.Adopter, donor.getStatusDonator());
        
        // 4. Cancels adoption → back to None
        donor.setStatusDonator(Status.None);
        assertEquals(Status.None, donor.getStatusDonator());
    }

    @Test
    @DisplayName("Should handle concurrent status changes")
    void testConcurrentStatusManagement() {
        // Simulate multiple status changes
        for (int i = 0; i < 10; i++) {
            donor.setStatusDonator(Status.Visitor);
            assertEquals(Status.Visitor, donor.getStatusDonator());
            
            donor.setStatusDonator(Status.Adopter);
            assertEquals(Status.Adopter, donor.getStatusDonator());
            
            donor.setStatusDonator(Status.None);
            assertEquals(Status.None, donor.getStatusDonator());
        }
    }

    // comparison test
    @Test
    @DisplayName("Should handle donor comparison correctly")
    void testDonorComparison() {
        // Create another donor with same data
        Donor sameDonor = new Donor("M", "John", "Smith", "1990-05-20", Role.DONOR);
        
        // They should have same properties but be different objects
        assertNotSame(donor, sameDonor);
        assertEquals(donor.getName(), sameDonor.getName());
        assertEquals(donor.getSurname(), sameDonor.getSurname());
        assertEquals(donor.getSex(), sameDonor.getSex());
        
        
    }
}
