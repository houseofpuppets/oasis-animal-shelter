package organizer.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import organizer.exceptionmanager.*;
import java.time.LocalDate;


  // Test class for Admin 
 //Tests admin creation, validation, role management, and clinical operations ( for vet)
 
class AdminTest {

    private Admin adminUser;
    private Admin veterinarian;
    private Animal testAnimal;

    @BeforeEach
    void setUp() {
        // Create test admin with ADMIN role
        adminUser = new Admin("F", "Sara", "Giovanelli", "1985-03-15", 
                             "Amministratore", Role.ADMIN);
        
        // Create test veterinarian with VETERINARIAN role
        veterinarian = new Admin("M", "Marco", "Severo", "1978-08-22", 
                                "Veterinario", Role.VETERINARIAN);
        
        // Create test animal for clinical operations
        testAnimal = new Animal("F", "Bella", "gatto");
    }

    @AfterEach
    void tearDown() {
        adminUser = null;
        veterinarian = null;
        testAnimal = null;
    }

    // CONSTRUCTOR TESTS 

    @Test
    @DisplayName("Should create admin with valid ADMIN role")
    void testValidAdminCreation() {
        // Test admin user creation
        assertEquals("F", adminUser.getSex());
        assertEquals("Sara", adminUser.getName());
        assertEquals("Giovanelli", adminUser.getSurname());
        assertEquals("1985-03-15", adminUser.getBirthDate());
        assertEquals("Amministratore", adminUser.getProfession());
        assertEquals(Role.ADMIN, adminUser.getAdminRole());
        
        // Test roles
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isVeterinarian());
        assertFalse(adminUser.canPerformClinicalOperations());
        
    
    }

    @Test
    @DisplayName("Should create admin with valid VETERINARIAN role")
    void testValidVeterinarianCreation() {
        // Test veterinarian creation
        assertEquals("M", veterinarian.getSex());
        assertEquals("Marco", veterinarian.getName());
        assertEquals("Severo", veterinarian.getSurname());
        assertEquals("Veterinario", veterinarian.getProfession());
        assertEquals(Role.VETERINARIAN, veterinarian.getAdminRole());
        
        // Test rol
        assertFalse(veterinarian.isAdmin());
        assertTrue(veterinarian.isVeterinarian());
        assertTrue(veterinarian.canPerformClinicalOperations());
        
    }

    @Test
    @DisplayName("Should throw PersonCreationException for invalid data")
    void testInvalidAdminCreation() {
        // Test invalid sex
        assertThrows(PersonCreationException.class, () -> {
            Admin.createNewAdmin("X", "Giovanni", "Rossi", "1980-01-01", "Manager", Role.ADMIN);
        });
        
        // Test invalid name (too short)
        assertThrows(PersonCreationException.class, () -> {
            Admin.createNewAdmin("M", "J", "Davide", "1980-01-01", "Manager", Role.ADMIN);
        });
        
        // Test invalid name (too long)
        assertThrows(PersonCreationException.class, () -> {
            Admin.createNewAdmin("M", "ahahahahhdnrkfmnkedmneidkdòlfòòfòfòfòfò", "Doe", "1980-01-01", "Manager", Role.ADMIN);
        });
        
        // Test invalid birth date format
        assertThrows(PersonCreationException.class, () -> {
            Admin.createNewAdmin("F", "Jane", "Doe", "1980/01/01", "Manager", Role.ADMIN); //it needs -
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid admin role")
    void testInvalidAdminRole() {
        // Admin can only have ADMIN or VETERINARIAN roles, not DONOR
        assertThrows(IllegalArgumentException.class, () -> {
            new Admin("M", "Giovanni", "Bianchi", "1980-01-01", "Manager", Role.DONOR);
        });
    }

    // ROLE VALIDATION TESTS 

    @Test
    @DisplayName("Should validate admin roles correctly")
    void testRoleValidation() {
        // Test valid roles
        assertDoesNotThrow(() -> {
            new Admin("M", "Giuseppe", "Volpe", "1980-01-01", "Manager", Role.ADMIN);
        });
        
        assertDoesNotThrow(() -> {
            new Admin("F", "Gianni", "Simeone", "1975-05-10", "Veterinario", Role.VETERINARIAN);
        });
    }

    @Test
    @DisplayName("Should correctly identify admin vs veterinarian")
    void testRoleIdentification() {
        // Admin user should be admin, not veterinarian
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isVeterinarian());
        
        // Veterinarian should be veterinarian, not admin
        assertFalse(veterinarian.isAdmin());
        assertTrue(veterinarian.isVeterinarian());
    }

    // CLINICAL OPERATIONS TESTS -- test 
    //testing permissions
    
    @Test
    @DisplayName("Should prevent non-veterinarian admin from performing clinical operations")
    void testNonVeterinarianClinicalOperations() {
        // Admin (non-veterinarian) should not be able to perform clinical operations
        assertThrows(UnsupportedOperationException.class, () -> {
            adminUser.performClinicalOperation(testAnimal, "vaccination", "Rabies vaccine");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            adminUser.performClinicalOperation(testAnimal, "sterilization", "Standard procedure");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            adminUser.performClinicalOperation(testAnimal, "medical_care", "Monitoring");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            adminUser.performClinicalOperation(testAnimal, "medical_note", "Checkup note");
        });
    }

   

    //  PROFESSION MANAGEMENT TESTS --testing getter and setter

    @Test
    @DisplayName("Should get and set profession correctly")
    void testProfessionManagement() {
        assertEquals("Amministratore", adminUser.getProfession());
        
        // Change profession
        adminUser.setProfession("Senior Amministratore");
        assertEquals("Senior Amministratore", adminUser.getProfession());
        
        // Test veterinarian profession
        assertEquals("Veterinario", veterinarian.getProfession());
    }

    // FACTORY METHODS TESTS 

    @Test
    @DisplayName("Should create new admin with saving")
    void testCreateNewAdmin() {
        assertDoesNotThrow(() -> {
            Admin newAdmin = Admin.createNewAdmin("M", "Robert", "Brown", "1982-12-01", 
                                                 "Manager", Role.ADMIN);
            assertNotNull(newAdmin);
            assertEquals("Robert", newAdmin.getName());
            assertEquals("Brown", newAdmin.getSurname());
            assertEquals("Manager", newAdmin.getProfession());
            assertEquals(Role.ADMIN, newAdmin.getAdminRole());
        });
    }

    @Test
    @DisplayName("Should load admin from file without saving")
    void testLoadFromFile() {
        assertDoesNotThrow(() -> {
            Admin loadedAdmin = Admin.loadFromFile("F", "Emily", "Devoti", "1979-07-15", 
                                                  "Veterinario", Role.VETERINARIAN);
            assertNotNull(loadedAdmin);
            assertEquals("Emily", loadedAdmin.getName());
            assertEquals("Devoti", loadedAdmin.getSurname());
            assertEquals("Veterinario", loadedAdmin.getProfession());
            assertEquals(Role.VETERINARIAN, loadedAdmin.getAdminRole());
        });
    }

    @Test
    @DisplayName("Should throw PersonCreationException for invalid data in factory methods")
    void testFactoryMethodsWithInvalidData() {
        // Test createNewAdmin with invalid data
        assertThrows(PersonCreationException.class, () -> 
            Admin.createNewAdmin("X", "InvalidSex", "Test", "1980-01-01", "Manager", Role.ADMIN));
        
        assertThrows(PersonCreationException.class, () -> 
            Admin.createNewAdmin("M", "A", "TooShort", "1980-01-01", "Manager", Role.ADMIN));
        
        // Test loadFromFile with invalid data
        assertThrows(PersonCreationException.class, () -> 
            Admin.loadFromFile("Y", "InvalidSex", "Test", "1980-01-01", "Manager", Role.VETERINARIAN));
        
        assertThrows(PersonCreationException.class, () -> 
            Admin.loadFromFile("F", "", "EmptyName", "1980-01-01", "Manager", Role.ADMIN));
    }

    // Getters and setters to test inheritance from Person

    @Test
    @DisplayName("Should correctly inherit from Person class")
    void testPersonInheritance() {
        
        assertNotNull(adminUser.getBirthDate());
        assertTrue(adminUser.getAge() > 0);
        assertNotNull(adminUser.getRegistrationDate());
        
        
        // Test that admin is instance of Person
        assertTrue(adminUser instanceof Person);
        assertTrue(adminUser instanceof Being);
    }

    //testing case-insensitive clinical operations

    @Test
    @DisplayName("Should handle case-insensitive clinical operations")
    void testCaseInsensitiveClinicalOperations() {
        // Test different cases
        assertDoesNotThrow(() -> {
            veterinarian.performClinicalOperation(testAnimal, "VACCINATION", "Rabies");
        });
        
        assertDoesNotThrow(() -> {
            veterinarian.performClinicalOperation(testAnimal, "Sterilization", "Standard");
        });
        
        assertDoesNotThrow(() -> {
            veterinarian.performClinicalOperation(testAnimal, "medical_CARE", "Monitoring");
        });
        
        assertDoesNotThrow(() -> {
            veterinarian.performClinicalOperation(testAnimal, "Medical_Note", "Note");
        });
    }

   

    // TO STRING TEST

    @Test
    @DisplayName("Should generate correct toString representation")
    void testToString() {
        String adminString = adminUser.toString();
        
        // Check that toString contains key information
        assertTrue(adminString.contains("Sara"));
        assertTrue(adminString.contains("Giovanelli"));
        assertTrue(adminString.contains("Amministratore"));
        assertTrue(adminString.contains("ADMIN"));
        assertTrue(adminString.contains("Admin")); // Class name
        
        // Should also contain inherited Person information
        assertTrue(adminString.contains("F"));
        assertNotNull(adminString);
        assertFalse(adminString.isEmpty());
    }

    @Test
    @DisplayName("Should differentiate toString between admin and veterinarian")
    void testToStringDifferences() {
        String adminString = adminUser.toString();
        String veterinarianString = veterinarian.toString();
        
        // Admin string should contain ADMIN role
        assertTrue(adminString.contains("ADMIN"));
        assertFalse(adminString.contains("VETERINARIAN"));
        
        // Veterinarian string should contain VETERINARIAN role
        assertTrue(veterinarianString.contains("VETERINARIAN"));
        assertFalse(veterinarianString.contains("ADMIN"));
    }

    // PERMISSION TESTS

    @Test
    @DisplayName("Should correctly determine clinical operation permissions")
    void testClinicalOperationPermissions() {
        // Only veterinarians can perform clinical operations
        assertTrue(veterinarian.canPerformClinicalOperations());
        assertFalse(adminUser.canPerformClinicalOperations());
        
        // Test with different admin types
        Admin anotherAdmin = new Admin("M", "John", "Manager", "1985-01-01", "Admin", Role.ADMIN);
        Admin anotherVet = new Admin("F", "Dr. Lisa", "Vet", "1982-01-01", "Vet", Role.VETERINARIAN);
        
        assertFalse(anotherAdmin.canPerformClinicalOperations());
        assertTrue(anotherVet.canPerformClinicalOperations());
    }
}
