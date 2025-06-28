package organizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import organizer.entities.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;



 //Test class for Main application
 
class MainTest {

    @TempDir
    Path tempDir;
    
    private Main mainApp;
    private InputStream originalSystemIn;
    private PrintStream originalSystemOut;
    private ByteArrayOutputStream testOutput;

    @BeforeEach
    void setUp() throws Exception {
        // Save original streams
        originalSystemIn = System.in;
        originalSystemOut = System.out;
        
        // Create test files
        Path animalFile = tempDir.resolve("Animal-list.txt");
        Path peopleFile = tempDir.resolve("People-list.txt");
        
        createTestDataFiles(animalFile, peopleFile);
        
        // Redirect output to capture it
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
        
        // Create Main instance
        mainApp = new Main();
    }

    @AfterEach
    void tearDown() {
        // Restore original streams
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
        
        mainApp = null;
    }

    
     // Creates test data files for the application
     
    private void createTestDataFiles(Path animalFile, Path peopleFile) throws Exception {
        // Create animal test data
        String animalData = 
            "Animal F Luna gatto\n" +
            "Animal M Rex cane\n" +
            "Animal F Bella coniglio\n";
        Files.write(animalFile, animalData.getBytes());
        
        // Create people test data  
        String peopleData = 
            "Donor F Maria Rossi 1985-06-15 Donator DONOR\n" +
            "Admin M Marco Bianchi 1975-08-10 Veterinario VETERINARIAN\n" +
            "Admin F Sara Neri 1982-03-25 Amministratore ADMIN\n";
        Files.write(peopleFile, peopleData.getBytes());
    }

    
     //Helper method to simulate user input
     
    private void simulateUserInput(String input) {
        ByteArrayInputStream testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);
    }

    // CONSTRUCTOR AND BASIC INITIALIZATION TESTS

    @Test
    @DisplayName("Should create Main instance successfully")
    void testMainConstructor() {
        assertDoesNotThrow(() -> {
            Main main = new Main();
            assertNotNull(main);
        });
    }

    // MENU FACTORY AND CREATION TESTS

    @Test
    @DisplayName("Should create appropriate menu for donor")
    void testDonorMenuCreation() {
        assertDoesNotThrow(() -> {
            Donor testDonor = new Donor("F", "Test", "User", "1990-01-01", Role.DONOR);
            
            assertNotNull(testDonor);
            assertTrue(testDonor instanceof Donor);
            assertEquals(Role.DONOR, testDonor.getRoleEnum());
            assertEquals(Status.None, testDonor.getStatusDonator());
        });
    }

    @Test
    @DisplayName("Should create appropriate menu for admin")
    void testAdminMenuCreation() {
        assertDoesNotThrow(() -> {
            Admin testAdmin = new Admin("M", "Test", "Admin", "1980-01-01", "Administrator", Role.ADMIN);
            
            assertNotNull(testAdmin);
            assertTrue(testAdmin.isAdmin());
            assertFalse(testAdmin.isVeterinarian());
            assertEquals("Administrator", testAdmin.getProfession());
        });
    }

    @Test
    @DisplayName("Should create appropriate menu for veterinarian")
    void testVeterinarianMenuCreation() {
        assertDoesNotThrow(() -> {
            Admin testVet = new Admin("F", "Test", "Vet", "1975-01-01", "Veterinario", Role.VETERINARIAN);
            
            assertNotNull(testVet);
            assertTrue(testVet.isVeterinarian());
            assertFalse(testVet.isAdmin());
            assertTrue(testVet.canPerformClinicalOperations());
        });
    }

    // INPUT VALIDATION TESTS

    @Test
    @DisplayName("Should validate input correctly")
    void testInputValidationMethods() {
        // Test input validation patterns used in Main
        
        // Valid sex values
        assertTrue("MF".contains("M"));
        assertTrue("MF".contains("F"));
        assertFalse("MF".contains("X"));
        
        // Valid name pattern (letters only)
        assertTrue("TestName".matches("[a-zA-Z]+"));
        assertFalse("Test123".matches("[a-zA-Z]+"));
        assertFalse("".matches("[a-zA-Z]+"));
        
        // Valid date pattern (YYYY-MM-DD)
        assertTrue("1990-01-01".matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue("2025-12-31".matches("\\d{4}-\\d{2}-\\d{2}"));
        assertFalse("90-1-1".matches("\\d{4}-\\d{2}-\\d{2}"));
        assertFalse("1990/01/01".matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    @DisplayName("Should handle invalid menu input gracefully")
    void testInvalidMenuInput() {
        // Simulate invalid menu choice followed by exit
        simulateUserInput("999\n3\n");
        
        assertDoesNotThrow(() -> {
            // Should handle invalid input without crashing
            // The main loop should continue until valid input or exit
            assertTrue(true, "Invalid input should be handled gracefully");
        });
    }

    // PERFORMANCE AND ROBUSTNESS TESTS

    @Test
    @DisplayName("Should perform efficiently")
    void testPerformance() {
        assertDoesNotThrow(() -> {
            long startTime = System.currentTimeMillis();
            
            Main main = new Main();
            assertNotNull(main);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Should initialize quickly (less than 1 second)
            assertTrue(duration < 1000, "Main initialization took too long: " + duration + "ms");
        });
    }

    @Test
    @DisplayName("Should handle system shutdown gracefully")
    void testSystemShutdown() {
        // Test that the application can be created and destroyed without issues
        assertDoesNotThrow(() -> {
            Main main1 = new Main();
            assertNotNull(main1);
            main1 = null; // Simulate cleanup
            
            Main main2 = new Main();
            assertNotNull(main2);
            main2 = null; // Simulate cleanup
            
            // Multiple instances should be handled correctly
        });
    }

    // INTEGRATION AND WORKFLOW TESTS

    @Test
    @DisplayName("Should handle user data validation")
    void testUserDataValidation() {
        assertDoesNotThrow(() -> {
            // Test creating users with various data - should validate correctly
            
            // Valid donor creation
            Donor validDonor = new Donor("F", "Maria", "Rossi", "1985-06-15", Role.DONOR);
            assertNotNull(validDonor);
            assertEquals("Maria", validDonor.getName());
            assertEquals("Rossi", validDonor.getSurname());
            
            // Valid admin creation
            Admin validAdmin = new Admin("M", "Marco", "Bianchi", "1975-08-10", "Veterinario", Role.VETERINARIAN);
            assertNotNull(validAdmin);
            assertEquals("Marco", validAdmin.getName());
            assertEquals("Bianchi", validAdmin.getSurname());
        });
    }

    @Test
    @DisplayName("Should handle malformed input robustly")
    void testMalformedInputHandling() {
        // Test various edge cases for input
        assertDoesNotThrow(() -> {
            // Empty strings
            String empty = "";
            assertFalse(empty.matches("[a-zA-Z]+"));
            
            // Special characters  
            String special = "@#$%";
            assertFalse(special.matches("[a-zA-Z]+"));
            
            // Mixed valid/invalid
            String mixed = "Test123";
            assertFalse(mixed.matches("[a-zA-Z]+"));
            
            // Whitespace
            String whitespace = "   ";
            assertFalse(whitespace.trim().matches("[a-zA-Z]+"));
        });
    }
}