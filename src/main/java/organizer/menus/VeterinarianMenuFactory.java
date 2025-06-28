package organizer.menus;
import organizer.entities.Person;
import organizer.entities.Admin;

// Factory for creating veterinarian menus
public class VeterinarianMenuFactory extends MenuFactory {
    
    // Creates and returns veterinarian menu
    @Override
    public Menu createMenu(Person person) {
        return new VeterinarianMenu((Admin) person);
    }
}
