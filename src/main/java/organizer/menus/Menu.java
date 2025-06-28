package organizer.menus;

// Interface that defines basic menu operations
public interface Menu {
    void displayOptions();           // Shows menu options to user
    void processUserChoice(int choice);  // Handles user's menu selection
    void logout();                   // Logs out current user
    boolean isSessionActive();       // Checks if session is still active
}
