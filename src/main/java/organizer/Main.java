package organizer;


import java.util.List;
import java.util.Scanner;

import organizer.activities.Adoption;
import organizer.datamanagement.AnimalUploading;
import organizer.datamanagement.PeopleUpdloading;
import organizer.entities.*;
import organizer.exceptionmanager.OasisExceptionShieldingHandler;
import organizer.exceptionmanager.OasisUserException;
import organizer.services.DataService;
import organizer.menus.*;
import java.util.logging.Logger;
import java.util.logging.Level;


// Main application class that manages the entire shelter system
public class Main {
    
    // Logger for tracking application errors and events
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    // File paths for animal and people data
    private static final String ANIMAL_FILE = "Animal-list.txt";
    private static final String PEOPLE_FILE = "People-list.txt";
    
    // Core components of the application
    private final DataService dataService;    // Handles data loading and processing
    private final Scanner scanner;           // Handles user input
    private Person currentUser;             // Currently logged in user
    private Menu currentMenu;              // Current active menu
    
    // Constructor initializes all components
    public Main() {
        this.dataService = new DataService();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        this.currentMenu = null;
    }
    
    // Entry point of the application
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }
    
    // Main application loop that controls the entire program flow
    public void run() {
        try {
            // Show welcome screen to user
            displayWelcomeMessage();
            
            // Load system data and validate files
            initializeSystem();
            
            // Main loop: handle login and user sessions
            while (true) {
                if (currentUser == null) {
                    // If no user is logged in, show login screen
                    if (!performLogin()) {
                        break; // User chose to exit
                    }
                }
                // Run the user's menu session
                runUserSession();
            }
            
        } catch (Exception e) {
            // Log critical errors and terminate application
            logger.log(Level.SEVERE, "Critical application error", e);
            System.err.println("Critical system error. Application will terminate.");
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Clean up resources before closing
            cleanup();
        }
    }
    
    // Displays the welcome screen with shelter logo
    private void displayWelcomeMessage() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     🐾 OASIS ANIMAL SHELTER 🐾       ║");
        System.out.println("║        Management System             ║");
        System.out.println("║                                      ║");
        System.out.println("║  Welcome to the Shelter Management  ║");
        System.out.println("║           Platform                   ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }
    
    // Initializes the system by loading data files
    private void initializeSystem() throws OasisUserException {
        System.out.println("Initializing system...");
        
        // Use exception shielding to handle file loading errors
        OasisExceptionShieldingHandler.executeWithShield(() -> {
            dataService.loadAnimals(ANIMAL_FILE);    // Load animal data
            dataService.loadPersons(PEOPLE_FILE);    // Load people data
            System.out.println("System initialized successfully");
        }, "system initialization");
    }
    
    // Handles the login process and account creation
    private boolean performLogin() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("           USER LOGIN");
        System.out.println("========================================");
        
        // Login menu loop
        while (true) {
            System.out.println();
            System.out.println("Options:");
            System.out.println("1. Login with credentials");
            System.out.println("2. Create new donor account");
            System.out.println("3. Exit application");
            System.out.print("Choose option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1 -> {
                        // Try to authenticate existing user
                        if (authenticateUser()) {
                            return true;
                        }
                    }
                    case 2 -> {
                        // Create new donor account
                        if (createNewDonorAccount()) {
                            return true;
                        }
                    }
                    case 3 -> {
                        // Exit the application
                        System.out.println("Thank you for using Oasis. Goodbye!");
                        return false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    // Authenticates existing users by name and surname
    private boolean authenticateUser() {
        try {
            // Get user credentials
            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine().trim();
            
            System.out.print("Enter your surname: ");
            String surname = scanner.nextLine().trim();
            
            // Validate input is not empty
            if (firstName.isEmpty() || surname.isEmpty()) {
                System.out.println("Name and surname cannot be empty.");
                return false;
            }
            
            // Search for user in the system with exception protection
            return OasisExceptionShieldingHandler.executeWithShield(() -> {
                List<Person> people = dataService.loadPersons(PEOPLE_FILE);
                
                // Find user by matching name and surname (case insensitive)
                Person user = people.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(firstName) && 
                               p.getSurname().equalsIgnoreCase(surname))
                    .findFirst()
                    .orElse(null);
                
                if (user != null) {
                    // User found - set as current user and create menu
                    currentUser = user;
                    createUserMenu();
                    System.out.println("Login successful! Welcome " + user.getName() + 
                                     " (" + user.getRole() + ")");
                    return true;
                } else {
                    // User not found
                    System.out.println("User not found. Please check your credentials.");
                    return false;
                }
            }, "user authentication");
            
        } catch (OasisUserException e) {
            System.out.println("Authentication error: " + e.getMessage());
            return false;
        }
    }
    
    // Creates a new donor account with user input
    private boolean createNewDonorAccount() {
        try {
            System.out.println();
            System.out.println("Creating new donor account...");
            
            // Collect user information with validation
            System.out.print("Sex (M/F): ");
            String sex = validateSexInput();
            
            System.out.print("First name: ");
            String firstName = validateNameInput("first name");
            
            System.out.print("Surname: ");
            String surname = validateNameInput("surname");
            
            System.out.print("Birth date (YYYY-MM-DD): ");
            String birthDate = validateDateInput();
            
            // Create new donor and save to file
            Donor newDonor = Donor.createNewDonor(sex, firstName, surname, birthDate, Role.DONOR);
            
            // Set as current user and create menu
            currentUser = newDonor;
            createUserMenu();
            
            System.out.println("Account created successfully! Welcome " + firstName + "!");
            return true;
            
        } catch (Exception e) {
            System.out.println("Account creation failed: " + e.getMessage());
            return false;
        }
    }
    
    // Validates sex input (M or F only)
    private String validateSexInput() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("M") || input.equals("F")) {
                return input;
            }
            System.out.print("Invalid sex. Please enter M or F: ");
        }
    }
    
    // Validates name input (2-20 letters only)
    private String validateNameInput(String fieldName) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 2 && input.length() <= 20 && input.matches("[a-zA-Z]+")) {
                return input;
            }
            System.out.print("Invalid " + fieldName + ". Use 2-20 letters only: ");
        }
    }
    
    // Validates date input in YYYY-MM-DD format
    private String validateDateInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return input;
            }
            System.out.print("Invalid date format. Use YYYY-MM-DD: ");
        }
    }
    
    // Creates appropriate menu based on user type using Factory pattern
    private void createUserMenu() {
        MenuFactory factory = MenuFactory.getFactory(currentUser);
        currentMenu = factory.createMenu(currentUser);
    }
    
    // Runs the user session with their specific menu
    private void runUserSession() {
        try {
            // Menu loop - continue until user logs out
            while (currentMenu != null && currentMenu.isSessionActive()) {
                // Display menu options
                currentMenu.displayOptions();
                
                try {
                    // Get user choice and process it
                    int choice = Integer.parseInt(scanner.nextLine().trim());
                    currentMenu.processUserChoice(choice);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                } catch (Exception e) {
                    System.out.println("Operation failed: " + e.getMessage());
                    logger.log(Level.WARNING, "Menu operation failed", e);
                }
                
                // Pause before showing menu again
                if (currentMenu != null && currentMenu.isSessionActive()) {
                    System.out.println();
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                }
            }
        } finally {
            // Reset user and menu when session ends
            currentUser = null;
            currentMenu = null;
            System.out.println("Session ended. Returning to login screen.");
        }
    }
    
    // Cleans up resources before application termination
    private void cleanup() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("Application cleanup completed.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during cleanup", e);
        }
    }
}




  