package organizer.menus;

import organizer.entities.Person;
import organizer.entities.Admin;
import organizer.entities.Donor;

// Abstract factory for creating different types of menus
public abstract class MenuFactory {
    
    // Abstract method to create menu - implemented by concrete factories
    public abstract Menu createMenu(Person person);
    
    // Factory method that returns appropriate factory based on user type
    public static MenuFactory getFactory(Person person) {
        if (person instanceof Admin) {
            Admin admin = (Admin) person;
            // Return veterinarian factory if user is veterinarian, admin factory otherwise
            return admin.isVeterinarian() ? 
                new VeterinarianMenuFactory() : 
                new AdminMenuFactory();
        }
        
        if (person instanceof Donor) {
            // Return external user factory for donors
            return new ExternalUserMenuFactory();
        }
        
        // Throw exception for unsupported user types
        throw new IllegalArgumentException("Unsupported user type: " + 
            person.getClass().getSimpleName());
    }
}
