package organizer.menus;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import organizer.entities.*;
import organizer.services.DataService;
import organizer.datamanagement.VisitManagement;
import organizer.activities.Visit;
import organizer.activities.PeriodTime;
import organizer.exceptionmanager.OasisUserException;



class AdminMenuTest {

    @Mock
    private Admin mockAdmin;
    
    @TempDir
    Path tempDir;
    
    private AdminMenu adminMenu;
    private Path animalFile;
    private Path peopleFile;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Create test files in temp directory
        animalFile = tempDir.resolve("Animal-list.txt");
        peopleFile = tempDir.resolve("People-list.txt");
        
        Files.write(animalFile, Arrays.asList(
            "Animal M Rex Dog",
            "Animal F Luna Cat",
            "Animal M Rocky Rabbit"
        ));
        
        Files.write(peopleFile, Arrays.asList(
            "Admin M John Smith 1980-05-15 Veterinarian VETERINARIAN",
            "Admin F Sarah Johnson 1985-03-20 Administrator ADMIN",
            "Donor M Mike Brown 1990-07-10 Donor DONOR"
        ));
        
        // Mock admin setup
        when(mockAdmin.getName()).thenReturn("John");
        when(mockAdmin.getAdminRole()).thenReturn(Role.ADMIN);
        when(mockAdmin.isVeterinarian()).thenReturn(false);
        
        // Capture system output
        originalOut = System.out;
        originalIn = System.in;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        // Create AdminMenu instance
        adminMenu = new AdminMenu(mockAdmin);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        if (adminMenu != null && adminMenu.isSessionActive()) {
            adminMenu.logout();
        }
    }

    @Test
    @DisplayName("Should create AdminMenu with valid admin")
    void testConstructor_ValidAdmin_Success() {
        // Given
        Admin admin = mock(Admin.class);
        when(admin.getName()).thenReturn("TestAdmin");
        
        // When
        AdminMenu menu = new AdminMenu(admin);
        
        // Then
        assertNotNull(menu);
        assertTrue(menu.isSessionActive());
    }

    @Test
    @DisplayName("Should display admin menu options correctly")
    void testDisplayOptions_ValidAdmin_DisplaysCorrectMenu() {
        // When
        adminMenu.displayOptions();
        
        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("ADMIN MENU - John"));
        assertTrue(output.contains("Role: ADMIN"));
        assertTrue(output.contains("DATA MANAGEMENT:"));
        assertTrue(output.contains("1. View Animals"));
        assertTrue(output.contains("2. View Staff"));
        assertTrue(output.contains("3. View Scheduled Visits"));
        assertTrue(output.contains("ADMINISTRATION:"));
        assertTrue(output.contains("4. Add New Animal"));
        assertTrue(output.contains("5. Add Staff Member"));
        assertTrue(output.contains("SESSION:"));
        assertTrue(output.contains("6. Logout"));
        assertTrue(output.contains("Select option (1-6):"));
    }

    @Test
    @DisplayName("Should handle logout correctly")
    void testProcessUserChoice_Logout_Success() {
        // Given
        assertTrue(adminMenu.isSessionActive());
        
        // When
        adminMenu.processUserChoice(6);
        
        // Then
        assertFalse(adminMenu.isSessionActive());
        String output = outputStream.toString();
        assertTrue(output.contains("Admin logged out successfully"));
    }

    @Test
    @DisplayName("Should handle invalid menu choice")
    void testProcessUserChoice_InvalidChoice_ShowsErrorMessage() {
        // When
        adminMenu.processUserChoice(99);
        
        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid option") || output.contains("Please select 1-6"));
        // Should still be active after invalid choice
        assertTrue(adminMenu.isSessionActive());
    }

    @Test
    @DisplayName("Should handle view animals choice")
    void testProcessUserChoice_ViewAnimals_AttemptsToLoadAnimals() {
        // Create test files in current directory (where the app expects them)
        createTestFilesInCurrentDirectory();
        
        // When
        adminMenu.processUserChoice(1);
        
        // Then
        String output = outputStream.toString();
        // Should either show animals or show an error, but shouldn't crash
        assertFalse(output.isEmpty());
        
        // Clean up
        cleanupTestFilesInCurrentDirectory();
    }

    @Test
    @DisplayName("Should handle view staff choice")
    void testProcessUserChoice_ViewStaff_AttemptsToLoadStaff() {
        // Create test files in current directory
        createTestFilesInCurrentDirectory();
        
        // When
        adminMenu.processUserChoice(2);
        
        // Then
        String output = outputStream.toString();
        // Should either show staff or show an error, but shouldn't crash
        assertFalse(output.isEmpty());
        
        // Clean up
        cleanupTestFilesInCurrentDirectory();
    }

    @Test
    @DisplayName("Should handle view visits choice")
    void testProcessUserChoice_ViewVisits_DisplaysVisits() {
        // When
        adminMenu.processUserChoice(3);
        
        // Then
        String output = outputStream.toString();
        // Should show either visits or "No visits scheduled"
        assertTrue(output.contains("No visits scheduled") || 
                  output.contains("SCHEDULED VISITS") ||
                  output.isEmpty()); // Empty is also acceptable if no visits
    }

    @Test
    @DisplayName("Should return correct session status")
    void testIsSessionActive_ReturnsCorrectStatus() {
        // Initially active
        assertTrue(adminMenu.isSessionActive());
        
        // After logout
        adminMenu.logout();
        assertFalse(adminMenu.isSessionActive());
    }

    @Test
    @DisplayName("Should handle boundary values for menu choices")
    void testProcessUserChoice_BoundaryValues_HandlesCorrectly() {
        // Test minimum valid boundary
        outputStream.reset();
        adminMenu.processUserChoice(1);
        // Should not crash and should remain active (assuming it doesn't fail on file operations)
        
        // Reset if needed
        if (!adminMenu.isSessionActive()) {
            adminMenu = new AdminMenu(mockAdmin);
        }
        
        // Test below minimum boundary
        outputStream.reset();
        adminMenu.processUserChoice(0);
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid") || output.contains("Please select"));
        assertTrue(adminMenu.isSessionActive()); // Should remain active
        
        // Test above maximum boundary
        outputStream.reset();
        adminMenu.processUserChoice(7);
        output = outputStream.toString();
        assertTrue(output.contains("Invalid") || output.contains("Please select"));
        assertTrue(adminMenu.isSessionActive()); // Should remain active
    }

    @Test
    @DisplayName("Should handle admin with different names correctly")
    void testAdminWithDifferentNames_DisplaysCorrectly() {
        // Given
        Admin differentAdmin = mock(Admin.class);
        when(differentAdmin.getName()).thenReturn("Alice");
        when(differentAdmin.getAdminRole()).thenReturn(Role.ADMIN);
        
        AdminMenu aliceMenu = new AdminMenu(differentAdmin);
        
        // When
        aliceMenu.displayOptions();
        
        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("ADMIN MENU - Alice"));
        assertFalse(output.contains("ADMIN MENU - John"));
    }

    @Test
    @DisplayName("Should handle veterinarian role display")
    void testDisplayOptions_VeterinarianRole_ShowsCorrectRole() {
        // Given
        when(mockAdmin.getAdminRole()).thenReturn(Role.VETERINARIAN);
        AdminMenu vetMenu = new AdminMenu(mockAdmin);
        
        // When
        vetMenu.displayOptions();
        
        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("Role: VETERINARIAN"));
    }

    @Test
    @DisplayName("Should maintain session state correctly during non-logout operations")
    void testSessionState_NonLogoutOperations_MaintainsState() {
        // Given - fresh session
        assertTrue(adminMenu.isSessionActive());
        
        // When - perform operations that don't logout
        adminMenu.processUserChoice(3); // View visits (safe operation)
        
        // Then - session should still be active
        assertTrue(adminMenu.isSessionActive());
    }

    @Test
    @DisplayName("Should handle multiple logout calls gracefully")
    void testLogout_MultipleCalls_HandlesGracefully() {
        // Given
        assertTrue(adminMenu.isSessionActive());
        
        // When - logout multiple times
        adminMenu.logout();
        assertFalse(adminMenu.isSessionActive());
        
        // Should handle second logout gracefully
        assertDoesNotThrow(() -> adminMenu.logout());
        assertFalse(adminMenu.isSessionActive());
    }

    // Helper methods to avoid Scanner blocking issues
    private void createTestFilesInCurrentDirectory() {
        try {
            Files.write(Path.of("Animal-list.txt"), Arrays.asList(
                "Animal M Rex Dog",
                "Animal F Luna Cat"
            ));
            Files.write(Path.of("People-list.txt"), Arrays.asList(
                "Admin M John Smith 1980-05-15 Veterinarian VETERINARIAN",
                "Donor M Mike Brown 1990-07-10 Donor DONOR"
            ));
        } catch (Exception e) {
            // If we can't create files, tests will show appropriate error handling
        }
    }
    
    private void cleanupTestFilesInCurrentDirectory() {
        try {
            Files.deleteIfExists(Path.of("Animal-list.txt"));
            Files.deleteIfExists(Path.of("People-list.txt"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}