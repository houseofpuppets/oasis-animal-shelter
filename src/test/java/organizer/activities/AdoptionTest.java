package organizer.activities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import organizer.entities.*;
import organizer.exceptionmanager.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


 //Test class for Adoption 
 
class AdoptionTest {

    @TempDir //to create temporary files
    Path tempDir;
    
    private Adoption adoptionSystem;
    private Path animalFile;
    private Path peopleFile;
    
    private Donor authorizedDonor;
    private Donor unauthorizedDonor;
    private Animal testAnimal1;
    private Animal testAnimal2;

    @BeforeEach
    void setUp() throws Exception {
        // Create temporary test files
        animalFile = tempDir.resolve("test-animals.txt");
        peopleFile = tempDir.resolve("test-people.txt");
        
        // Create test data--upload data on files
        createTestFiles(); 
        
        // Create test entities
        authorizedDonor = new Donor("F", "Maria", "Rossi", "1985-06-15", Role.DONOR);
        unauthorizedDonor = new Donor("M", "Giovanni", "Verdi", "1980-03-20", Role.DONOR);
        testAnimal1 = new Animal("F", "Luna", "gatto");
        testAnimal2 = new Animal("M", "Rex", "cane");
        
        // Initialize new adoption
        adoptionSystem = new Adoption(animalFile.toString(), peopleFile.toString()); //path to string
    }

    @AfterEach
    void tearDown() {
        adoptionSystem = null;
        authorizedDonor = null;
        unauthorizedDonor = null;
        testAnimal1 = null;
        testAnimal2 = null;
    }

    
     //writes sample animal and people data on test files
     
    private void createTestFiles() throws Exception {
        // Create animal test data
        String animalData = """
                Animal F Luna gatto
                Animal M Rex cane
                Animal F Bella coniglio
                Animal M Max gatto
                """;
        Files.writeString(animalFile, animalData);
        
        // Create people test data (including authorized donor)
        String peopleData = """
                Donor F Maria Rossi 1985-06-15 Donator DONOR
                Admin M Marco Bianchi 1975-08-10 Veterinario VETERINARIAN
                Admin F Sara Neri 1982-03-25 Amministratore ADMIN
                """;
        Files.writeString(peopleFile, peopleData);
    }

    // CONSTRUCTOR TESTS

    @Test
    @DisplayName("Should create adoption system with valid files")
    void testValidAdoptionSystemCreation() {
        assertDoesNotThrow(() -> {
            Adoption adoption = new Adoption(animalFile.toString(), peopleFile.toString());
            assertNotNull(adoption);
        });
    }

    @Test
    @DisplayName("Should throw OasisUserException for invalid animal file")
    void testInvalidAnimalFile() {
        assertThrows(OasisUserException.class, () -> {
            new Adoption("nonexistent-animals.txt", peopleFile.toString());
        });
    }

    @Test
    @DisplayName("Should throw OasisUserException for invalid people file")
    void testInvalidPeopleFile() {
        assertThrows(OasisUserException.class, () -> {
            new Adoption(animalFile.toString(), "nonexistent-people.txt");
        });
    }

    @Test
    @DisplayName("Should throw OasisUserException for both invalid files")
    void testBothInvalidFiles() {
        assertThrows(OasisUserException.class, () -> {
            new Adoption("invalid-animals.txt", "invalid-people.txt");
        });
    }

    // DONOR AUTHORIZATION TESTS

    @Test
    @DisplayName("Should authorize valid donor")
    void testAuthorizedDonorAdoption() {
        assertDoesNotThrow(() -> {
            adoptionSystem.adopt(authorizedDonor);
        });
    }

    @Test
    @DisplayName("Should reject unauthorized donor")
    void testUnauthorizedDonorAdoption() {
        assertThrows(OasisUserException.class, () -> {
            adoptionSystem.adopt(unauthorizedDonor);
        });
    }

    @Test
    @DisplayName("Should reject null donor")
    void testNullDonorAdoption() {
        assertThrows(OasisUserException.class, () -> {
            adoptionSystem.adopt(null);
        });
    }

    // ANIMAL AVAILABILITY TESTS

    @Test
    @DisplayName("Should have animals available after initialization")
    void testAnimalsAvailableAfterInitialization() {
        List<Animal> availableAnimals = adoptionSystem.getAdoptableAnimals();
        assertNotNull(availableAnimals);
        assertFalse(availableAnimals.isEmpty());
        assertTrue(availableAnimals.size() >= 4); // Based on inserted test data
    }

    @Test
    @DisplayName("Should return defensive copy of adoptable animals")
    void testDefensiveCopyOfAdoptableAnimals() {
        List<Animal> animals1 = adoptionSystem.getAdoptableAnimals();
        List<Animal> animals2 = adoptionSystem.getAdoptableAnimals();
        
        // Should be different objects (defensive copy)
        assertNotSame(animals1, animals2);
        // But same content
        assertEquals(animals1.size(), animals2.size());
    }

    // ADOPTION PROCESS TESTS

    @Test
    @DisplayName("Should complete adoption process successfully")
    void testSuccessfulAdoptionProcess() {
        assertDoesNotThrow(() -> {
            int initialCount = adoptionSystem.getAdoptableAnimals().size();
            
            adoptionSystem.adopt(authorizedDonor);
            
            // Check that available animals decreased
            int finalCount = adoptionSystem.getAdoptableAnimals().size();
            assertEquals(initialCount - 1, finalCount);
            
            // Check donor status changed to Adopter
            assertEquals(Status.Adopter, authorizedDonor.getStatusDonator());
            
            // Check adoption matches
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            assertTrue(matches.containsKey(authorizedDonor));
        });
    }

    @Test
    @DisplayName("Should handle multiple adoptions by same donor")
    void testMultipleAdoptionsBySameDonor() {
        assertDoesNotThrow(() -> {
            // First adoption
            adoptionSystem.adopt(authorizedDonor);
            assertEquals(Status.Adopter, authorizedDonor.getStatusDonator());
            
            // Try second adoption - should work if there are more animals
            if (adoptionSystem.getAdoptableAnimals().size() > 0) {
                adoptionSystem.adopt(authorizedDonor);
                // Status should remain Adopter
                assertEquals(Status.Adopter, authorizedDonor.getStatusDonator());
            }
        });
    }

    // ADOPTION CANCELLATION TESTS

    @Test
    @DisplayName("Should cancel adoption successfully")
    void testSuccessfulAdoptionCancellation() {
        assertDoesNotThrow(() -> {
            // First adopt an animal
            int initialCount = adoptionSystem.getAdoptableAnimals().size();
            adoptionSystem.adopt(authorizedDonor);
            
            // Get the adopted animal
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            Animal adoptedAnimal = matches.get(authorizedDonor);
            assertNotNull(adoptedAnimal);
            
            // Cancel the adoption
            adoptionSystem.cancelAdoption(adoptedAnimal, authorizedDonor);
            
            // Check that animal is back in available list
            int finalCount = adoptionSystem.getAdoptableAnimals().size();
            assertEquals(initialCount, finalCount);
            
            // Check donor status changed to None
            assertEquals(Status.None, authorizedDonor.getStatusDonator());
            
            // Check adoption matches no longer contains this donor
            Map<Donor, Animal> updatedMatches = adoptionSystem.getAdoptionMatches();
            assertFalse(updatedMatches.containsKey(authorizedDonor));
        });
    }

    @Test
    @DisplayName("Should reject cancellation for non-adopted animal")
    void testCancelNonAdoptedAnimal() {
        assertThrows(OasisUserException.class, () -> {
            // Try to cancel adoption without adopting first
            adoptionSystem.cancelAdoption(testAnimal1, authorizedDonor);
        });
    }

    @Test
    @DisplayName("Should reject cancellation with wrong donor")
    void testCancelAdoptionWithWrongDonor() {
        assertDoesNotThrow(() -> {
            // First adopt an animal with authorized donor
            adoptionSystem.adopt(authorizedDonor);
            
            // Get the adopted animal
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            Animal adoptedAnimal = matches.get(authorizedDonor);
            
            // Try to cancel with different donor
            assertThrows(OasisUserException.class, () -> {
                adoptionSystem.cancelAdoption(adoptedAnimal, unauthorizedDonor);
            });
        });
    }

    @Test
    @DisplayName("Should reject cancellation with null parameters")
    void testCancelAdoptionWithNullParameters() {
        assertDoesNotThrow(() -> {
            // First adopt an animal
            adoptionSystem.adopt(authorizedDonor);
            
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            Animal adoptedAnimal = matches.get(authorizedDonor);
            
            // Test null animal
            assertThrows(OasisUserException.class, () -> {
                adoptionSystem.cancelAdoption(null, authorizedDonor);
            });
            
            // Test null donor
            assertThrows(OasisUserException.class, () -> {
                adoptionSystem.cancelAdoption(adoptedAnimal, null);
            });
        });
    }

    // EDGE CASES AND ERROR HANDLING

    @Test
    @DisplayName("Should handle empty animal file gracefully")
    void testEmptyAnimalFile() throws Exception {
        // Create empty animal file
        Path emptyAnimalFile = tempDir.resolve("empty-animals.txt");
        Files.writeString(emptyAnimalFile, "");
        
        assertThrows(OasisUserException.class, () -> {
            new Adoption(emptyAnimalFile.toString(), peopleFile.toString());
        });
    }

    @Test
    @DisplayName("Should handle file with invalid animal data")
    void testInvalidAnimalData() throws Exception {
        // Create file with invalid animal data
        Path invalidAnimalFile = tempDir.resolve("invalid-animals.txt");
        String invalidData = """
                InvalidFormat F Luna gatto
                Animal Z Luna gatto
                Animal F
                """;
        Files.writeString(invalidAnimalFile, invalidData);
        
        assertDoesNotThrow(() -> {
            // Should create adoption system but with fewer animals
            Adoption adoption = new Adoption(invalidAnimalFile.toString(), peopleFile.toString());
            assertNotNull(adoption);
            // Should have filtered out invalid entries
            assertTrue(adoption.getAdoptableAnimals().size() < 3);
        });
    }

    @Test
    @DisplayName("Should handle adoption when no animals available")
    void testAdoptionWithNoAnimals() throws Exception {
        // Create file with no valid animals
        Path noAnimalsFile = tempDir.resolve("no-animals.txt");
        Files.writeString(noAnimalsFile, "# No animals available\n");
        
        assertThrows(OasisUserException.class, () -> {
            Adoption adoption = new Adoption(noAnimalsFile.toString(), peopleFile.toString());
            adoption.adopt(authorizedDonor);
        });
    }

    // GETTER TESTS

    @Test
    @DisplayName("Should return correct adoption matches")
    void testGetAdoptionMatches() {
        assertDoesNotThrow(() -> {
            // Initially should be empty
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            assertNotNull(matches);
            assertTrue(matches.isEmpty());
            
            // After adoption should contain the match
            adoptionSystem.adopt(authorizedDonor);
            Map<Donor, Animal> updatedMatches = adoptionSystem.getAdoptionMatches();
            assertEquals(1, updatedMatches.size());
            assertTrue(updatedMatches.containsKey(authorizedDonor));
        });
    }

    @Test
    @DisplayName("Should return defensive copy of adoption matches")
    void testDefensiveCopyOfAdoptionMatches() {
        assertDoesNotThrow(() -> {
            adoptionSystem.adopt(authorizedDonor);
            
            Map<Donor, Animal> matches1 = adoptionSystem.getAdoptionMatches();
            Map<Donor, Animal> matches2 = adoptionSystem.getAdoptionMatches();
            
            // Should be different objects (defensive copy)
            assertNotSame(matches1, matches2);
            // But same content
            assertEquals(matches1.size(), matches2.size());
        });
    }

    @Test
    @DisplayName("Should return same list from getAvailableAnimals and getAdoptableAnimals")
    void testGetAvailableAnimalsConsistency() {
        List<Animal> adoptableAnimals = adoptionSystem.getAdoptableAnimals();
        List<Animal> availableAnimals = adoptionSystem.getAvailableAnimals();
        
        assertNotNull(adoptableAnimals);
        assertNotNull(availableAnimals);
        assertEquals(adoptableAnimals.size(), availableAnimals.size());
    }

    // INTEGRATION TESTS

    @Test
    @DisplayName("Should handle complete adoption workflow")
    void testCompleteAdoptionWorkflow() {
        assertDoesNotThrow(() -> {
            // 1. Check initial state
            int initialAnimals = adoptionSystem.getAdoptableAnimals().size();
            assertTrue(initialAnimals > 0);
            assertEquals(Status.None, authorizedDonor.getStatusDonator());
            
            // 2. Perform adoption
            adoptionSystem.adopt(authorizedDonor);
            
            // 3. Verify adoption state
            assertEquals(initialAnimals - 1, adoptionSystem.getAdoptableAnimals().size());
            assertEquals(Status.Adopter, authorizedDonor.getStatusDonator());
            Map<Donor, Animal> matches = adoptionSystem.getAdoptionMatches();
            assertTrue(matches.containsKey(authorizedDonor));
            Animal adoptedAnimal = matches.get(authorizedDonor);
            assertNotNull(adoptedAnimal);
            
            // 4. Cancel adoption
            adoptionSystem.cancelAdoption(adoptedAnimal, authorizedDonor);
            
            // 5. Verify cancellation state
            assertEquals(initialAnimals, adoptionSystem.getAdoptableAnimals().size());
            assertEquals(Status.None, authorizedDonor.getStatusDonator());
            Map<Donor, Animal> updatedMatches = adoptionSystem.getAdoptionMatches();
            assertFalse(updatedMatches.containsKey(authorizedDonor));
        });
    }

    @Test
    @DisplayName("Should handle multiple concurrent adoptions")
    void testMultipleConcurrentAdoptions() throws Exception {
        // Add another authorized donor to test file
        String additionalDonor = "Donor M Luigi Verdi 1975-12-10 Donator DONOR\n";
        Files.writeString(peopleFile, Files.readString(peopleFile) + additionalDonor);
        
        // Recreate adoption system with updated file
        Adoption newAdoptionSystem = new Adoption(animalFile.toString(), peopleFile.toString());
        
        // Create second authorized donor
        Donor secondDonor = new Donor("M", "Luigi", "Verdi", "1975-12-10", Role.DONOR);
        
        assertDoesNotThrow(() -> {
            int initialCount = newAdoptionSystem.getAdoptableAnimals().size();
            assertTrue(initialCount >= 2); // Need at least 2 animals
            
            // First adoption
            newAdoptionSystem.adopt(authorizedDonor);
            assertEquals(Status.Adopter, authorizedDonor.getStatusDonator());
            
            // Second adoption
            newAdoptionSystem.adopt(secondDonor);
            assertEquals(Status.Adopter, secondDonor.getStatusDonator());
            
            // Verify both adoptions
            assertEquals(initialCount - 2, newAdoptionSystem.getAdoptableAnimals().size());
            Map<Donor, Animal> matches = newAdoptionSystem.getAdoptionMatches();
            assertEquals(2, matches.size());
            assertTrue(matches.containsKey(authorizedDonor));
            assertTrue(matches.containsKey(secondDonor));
        });
    }
}