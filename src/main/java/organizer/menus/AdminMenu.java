package organizer.menus;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import organizer.entities.Admin;
import organizer.entities.Animal;
import organizer.entities.Person;
import organizer.entities.Role;
import organizer.services.DataService;
import organizer.datamanagement.VisitManagement;
import organizer.activities.Visit;
import organizer.exceptionmanager.*;

// Menu for admin users with data management and administration features
public class AdminMenu implements Menu {
    // Core components for admin operations
    protected final Admin admin;                    // Current admin user
    protected final DataService dataService;       // Data management service
    protected final VisitManagement visitManager;  // Visit management service
    protected final Scanner scanner;               // Input handler
    protected boolean sessionActive;              // Session state
    protected static final Logger logger = Logger.getLogger(AdminMenu.class.getName());

    // Constructor initializes all components
    public AdminMenu(Admin admin) {
        this.admin = admin;
        this.dataService = new DataService();
        this.visitManager = new VisitManagement();
        this.scanner = new Scanner(System.in);
        this.sessionActive = true;
    }

    // Displays admin menu options
    @Override
    public void displayOptions() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("ADMIN MENU - " + admin.getName());
        System.out.println("Role: " + admin.getAdminRole());
        System.out.println("==================================================");
        
        System.out.println("DATA MANAGEMENT:");
        System.out.println("  1. View Animals");
        System.out.println("  2. View Staff");
        System.out.println("  3. View Scheduled Visits");
        
        System.out.println();
        System.out.println("ADMINISTRATION:");
        System.out.println("  4. Add New Animal");
        System.out.println("  5. Add Staff Member");
        
        System.out.println();
        System.out.println("SESSION:");
        System.out.println("  6. Logout");
        
        System.out.print("\nSelect option (1-6): ");
    }

    // Processes admin's menu choice with exception handling
    @Override
    public void processUserChoice(int choice) {
        try {
            // Use exception shielding for all operations
            OasisExceptionShieldingHandler.executeWithShield(() -> {
                switch (choice) {
                    case 1 -> displayAnimals();         // Show all animals
                    case 2 -> displayStaff();           // Show staff members
                    case 3 -> displayScheduledVisits(); // Show scheduled visits
                    case 4 -> addNewAnimal();           // Add new animal
                    case 5 -> addStaffMember();         // Add new staff
                    case 6 -> logout();                 // End session
                    default -> System.out.println("Invalid option. Please select 1-6.");
                }
            }, "admin menu operation");
            
        } catch (OasisUserException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    // Displays all animals in the system with statistics
    protected void displayAnimals() throws Exception {
        // Load animals from file
        List<Animal> animals = dataService.loadAnimals("Animal-list.txt");
        
        // Check if any animals exist
        if (animals.isEmpty()) {
            System.out.println("No animals registered.");
            return;
        }

        // Display animals and statistics
        System.out.println();
        System.out.println("REGISTERED ANIMALS");
        System.out.println("==================================================");
        animals.forEach(System.out::println);
        
        // Show additional statistics
        dataService.displayAnimalStatistics(animals);
    }

    // Displays all staff members in the system
    protected void displayStaff() throws Exception {
        // Load all people and filter for staff
        List<Person> staff = dataService.loadPersons("People-list.txt");
        List<Person> adminStaff = staff.stream()
            .filter(person -> person instanceof Admin)
            .toList();

        // Check if any staff exists
        if (adminStaff.isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }

        // Display staff members
        System.out.println();
        System.out.println("STAFF MEMBERS");
        System.out.println("==================================================");
        adminStaff.forEach(System.out::println);
    }

    // Displays all scheduled visits
    protected void displayScheduledVisits() {
        // Get active visits
        List<Visit> activeVisits = visitManager.getActiveVisits();
        
        // Check if any visits scheduled
        if (activeVisits.isEmpty()) {
            System.out.println("No visits scheduled.");
            return;
        }

        // Display visits in table format
        System.out.println();
        System.out.println("SCHEDULED VISITS");
        System.out.println("------------------------------------------------------------");
        System.out.println("ID    VISITOR          DATE         TIME");
        System.out.println("------------------------------------------------------------");
        
        // Print each visit
        activeVisits.forEach(visit -> {
            System.out.println(visit.getVisitId() + "     " + 
                visit.getVisitor().getName() + "          " +
                visit.getDate() + "   " + 
                visit.getTime());
        });
    }

    // Adds a new animal to the system
    protected void addNewAnimal() throws Exception {
        System.out.println();
        System.out.println("ADD NEW ANIMAL");
        System.out.println("--------------------");
        
        // Get animal information from user with validation
        System.out.print("Sex (M/F): ");
        String sex = validateSexInput();
        
        System.out.print("Name: ");
        String name = validateAnimalNameInput();
        
        System.out.print("Species: ");
        String species = validateSpeciesInput();

        // Create and save new animal
        Animal newAnimal = Animal.createNewAnimal(sex, name, species);
        System.out.println("Animal added successfully: " + newAnimal.getName());
    }

    // Adds a new staff member to the system
    protected void addStaffMember() throws Exception {
        System.out.println();
        System.out.println("ADD STAFF MEMBER");
        System.out.println("--------------------");
        
        // Get staff information from user with validation
        System.out.print("Sex (M/F): ");
        String sex = validateSexInput();
        
        System.out.print("First name: ");
        String name = validateNameInput();
        
        System.out.print("Surname: ");
        String surname = validateNameInput();
        
        System.out.print("Birth date (YYYY-MM-DD): ");
        String birthDate = validateDateInput();
        
        System.out.print("Profession: ");
        String profession = validateProfessionInput();
        
        System.out.print("Role (ADMIN/VETERINARIAN): ");
        String roleStr = validateRoleInput();

        // Create and save new staff member
        Role role = Role.valueOf(roleStr.toUpperCase());
        Admin newStaff = Admin.createNewAdmin(sex, name, surname, birthDate, profession, role);
        System.out.println("Staff member added successfully: " + newStaff.getName());
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
    private String validateNameInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 2 && input.length() <= 20 && input.matches("[a-zA-Z]+")) {
                return input;
            }
            System.out.print("Invalid name. Use 2-20 letters only: ");
        }
    }
    
    // Validates animal name input (2-20 characters, can include numbers)
    private String validateAnimalNameInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 2 && input.length() <= 20) {
                return input;
            }
            System.out.print("Invalid name. Use 2-20 characters: ");
        }
    }
    
    // Validates species input (2-20 letters only)
    private String validateSpeciesInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 2 && input.length() <= 20 && input.matches("[a-zA-Z]+")) {
                return input;
            }
            System.out.print("Invalid species. Use 2-20 letters only: ");
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
    
    // Validates profession input (2-30 characters)
    private String validateProfessionInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.length() >= 2 && input.length() <= 30) {
                return input;
            }
            System.out.print("Invalid profession. Use 2-30 characters: ");
        }
    }
    
    // Validates role input (ADMIN or VETERINARIAN only)
    private String validateRoleInput() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("ADMIN") || input.equals("VETERINARIAN")) {
                return input;
            }
            System.out.print("Invalid role. Enter ADMIN or VETERINARIAN: ");
        }
    }

    // Logs out current admin and ends session
    @Override
    public void logout() {
        this.sessionActive = false;
        System.out.println("Admin logged out successfully.");
    }

    // Returns current session status
    @Override
    public boolean isSessionActive() {
        return sessionActive;
    }
}
