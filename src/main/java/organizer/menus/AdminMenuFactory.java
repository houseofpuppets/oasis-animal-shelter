package organizer.menus;

import organizer.entities.Person;
import organizer.entities.Admin;

// Factory for creating admin menus
public class AdminMenuFactory extends MenuFactory {
    
    // Creates and returns admin menu
    @Override
    public Menu createMenu(Person person) {
        return new AdminMenu((Admin) person);
    }
}
