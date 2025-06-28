package organizer.menus;

import organizer.entities.Person;
import organizer.entities.Donor;

// Factory for creating external user (donor) menus
public class ExternalUserMenuFactory extends MenuFactory {
    
    // Creates and returns external user menu
    @Override
    public Menu createMenu(Person person) {
        return new ExternalUserMenu((Donor) person);
    }
}
