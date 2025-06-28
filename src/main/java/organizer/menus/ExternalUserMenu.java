package organizer.menus;

import java.util.List;
import java.util.Scanner;
import organizer.entities.Donor;
import organizer.activities.Visit;
import organizer.activities.PeriodTime;
import organizer.datamanagement.VisitManagement;
import organizer.clients.ShelterClient;
import organizer.exceptionmanager.*;

// Menu for external users (donors) with visit and adoption features
public class ExternalUserMenu implements Menu {
    // Core components for external user operations
    private final Donor donor;                    // Current donor user
    private final VisitManagement visitManager;   // Handles visit operations
    private final ShelterClient shelterClient;    // Handles adoption operations
    private final Scanner scanner;                // Input handler
    private boolean sessionActive;               // Session state

    // Constructor initializes all components
    public ExternalUserMenu(Donor donor) {
        this.donor = donor;
        this.visitManager = new VisitManagement();
        this.shelterClient = new ShelterClient();
        this.scanner = new Scanner(System.in);
        this.sessionActive = true;
    }

    // Displays menu options for external users
    @Override
    public void displayOptions() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("EXTERNAL USER MENU - " + donor.getName());
        System.out.println("Status: " + donor.getStatusDonator());
        System.out.println("==================================================");
        
        System.out.println("VISIT MANAGEMENT:");
        System.out.println("  1. Book Visit");
        System.out.println("  2. View My Visits");
        System.out.println("  3. Reschedule Visit");
        System.out.println("  4. Cancel Visit");
        System.out.println("  5. Check Available Slots");
        
        System.out.println();
        System.out.println("ADOPTION SERVICES:");
        System.out.println("  6. View Available Animals");
        System.out.println("  7. Adopt Animal");
        
        System.out.println();
        System.out.println("SESSION:");
        System.out.println("  8. Logout");
        
        System.out.print("\nSelect option (1-8): ");
    }

    // Processes user's menu choice with exception handling
    @Override
    public void processUserChoice(int choice) {
        try {
            // Use exception shielding for all operations
            OasisExceptionShieldingHandler.executeWithShield(() -> {
                switch (choice) {
                    case 1 -> scheduleVisit();          // Book new visit
                    case 2 -> displayUserVisits();      // Show user's visits
                    case 3 -> rescheduleExistingVisit(); // Modify existing visit
                    case 4 -> cancelExistingVisit();     // Cancel visit
                    case 5 -> showAvailableSlots();      // Check availability
                    case 6 -> showAvailableAnimals();    // View animals
                    case 7 -> initiateAdoption();        // Start adoption process
                    case 8 -> logout();                  // End session
                    default -> System.out.println("Invalid option. Please select 1-8.");
                }
            }, "external user menu operation");
            
        } catch (OasisUserException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    // Books a new visit with date and time validation
    private void scheduleVisit() throws Exception {
        System.out.println();
        System.out.println("VISIT BOOKING");
        System.out.println("--------------------");
        
        // Get visit date from user
        System.out.print("Enter visit date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        
        // Validate date format
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }
        
        // Get time period selection
        System.out.println("Select time period:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.print("Choose: ");
        
        int timeChoice = Integer.parseInt(scanner.nextLine().trim());
        if (timeChoice != 1 && timeChoice != 2) {
            throw new IllegalArgumentException("Invalid time choice. Select 1 or 2");
        }
        
        // Convert choice to enum
        PeriodTime period = (timeChoice == 1) ? PeriodTime.Morning : PeriodTime.Afternoon;

        // Book the visit
        Visit visit = visitManager.bookVisit(donor, date, period);
        System.out.println("Visit scheduled successfully. ID: " + visit.getVisitId());
    }

    // Displays all visits for current user
    private void displayUserVisits() {
        List<Visit> visits = visitManager.getVisitsForDonor(donor);
        
        // Check if user has any visits
        if (visits.isEmpty()) {
            System.out.println("No visits found.");
            return;
        }

        // Display visits in table format
        System.out.println();
        System.out.println("YOUR VISITS");
        System.out.println("--------------------------------------------------");
        System.out.println("ID    DATE         TIME       STATUS");
        System.out.println("--------------------------------------------------");
        
        // Print each visit
        visits.forEach(visit -> {
            String status = visit.isActive() ? "Active" : "Cancelled";
            System.out.println(visit.getVisitId() + "     " + visit.getDate() + "   " + 
                visit.getTime() + "   " + status);
        });
    }

    // Allows user to reschedule an existing visit
    private void rescheduleExistingVisit() throws Exception {
        // Get visit ID to reschedule
        System.out.print("Enter visit ID: ");
        String visitId = scanner.nextLine().trim();
        
        // Get new date
        System.out.print("Enter new date (YYYY-MM-DD): ");
        String newDate = scanner.nextLine().trim();
        
        // Validate new date format
        if (!newDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format");
        }
        
        // Get new time period
        System.out.println("Select new time:");
        System.out.println("1. Morning  2. Afternoon");
        System.out.print("Choose: ");
        
        int timeChoice = Integer.parseInt(scanner.nextLine().trim());
        PeriodTime newPeriod = (timeChoice == 1) ? PeriodTime.Morning : PeriodTime.Afternoon;

        // Perform the reschedule
        visitManager.rescheduleVisit(visitId, newDate, newPeriod);
        System.out.println("Visit rescheduled successfully.");
    }

    // Cancels an existing visit
    private void cancelExistingVisit() throws Exception {
        // Get visit ID to cancel
        System.out.print("Enter visit ID to cancel: ");
        String visitId = scanner.nextLine().trim();

        // Cancel the visit
        visitManager.cancelVisit(visitId);
        System.out.println("Visit cancelled successfully.");
    }

    // Shows available visit slots to user
    private void showAvailableSlots() {
        // Get search parameters from user
        System.out.print("Days to check ahead: ");
        int daysAhead = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Maximum slots to display: ");
        int maxSlots = Integer.parseInt(scanner.nextLine().trim());

        // Get available slots
        List<String> slots = visitManager.getNextAvailableSlots(maxSlots, daysAhead);
        
        // Check if any slots found
        if (slots.isEmpty()) {
            System.out.println("No available slots found.");
            return;
        }

        // Display available slots
        System.out.println();
        System.out.println("AVAILABLE SLOTS");
        System.out.println("------------------------------");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i));
        }
    }

    // Initiates the adoption process for current user
    private void initiateAdoption() throws Exception {
        System.out.println();
        System.out.println("ADOPTION PROCESS");
        System.out.println("--------------------");
        
        // Initialize shelter system and process adoption request
        shelterClient.initializeSystem("Animal-list.txt", "People-list.txt");
        shelterClient.processAdoptionRequest(donor);
    }

    // Shows all animals available for adoption
    private void showAvailableAnimals() throws Exception {
        // Initialize shelter system and get available animals
        shelterClient.initializeSystem("Animal-list.txt", "People-list.txt");
        var animals = shelterClient.getAvailableAnimals();
        
        // Check if any animals available
        if (animals.isEmpty()) {
            System.out.println("No animals currently available for adoption.");
            return;
        }

        // Display available animals
        System.out.println();
        System.out.println("AVAILABLE ANIMALS");
        System.out.println("--------------------------------------------------");
        animals.forEach(System.out::println);
    }

    // Logs out current user and ends session
    @Override
    public void logout() {
        this.sessionActive = false;
        System.out.println("Logged out successfully.");
    }

    // Returns current session status
    @Override
    public boolean isSessionActive() {
        return sessionActive;
    }
}
