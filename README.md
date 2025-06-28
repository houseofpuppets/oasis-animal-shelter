OASIS ANIMAL SHELTER MANAGEMENT SYSTEM

APPLICATION OVERVIEW AND FUNCTIONALITY

The Oasis Animal Shelter Management System is a Java-based application designed to streamline the operations of animal shelters. This system manages the complex relationships between shelter animals, staff members, and external donors through an intuitive role-based interface.

The system serves as a digital hub for an animal shelter where abandoned or rescued animals receive care and find new homes. The shelter operates through the work of internal staff (administrators, veterinarians, and care personnel) and the  support of external donors who contribute through distance adoptions and scheduled visits.
.
This system management is composed of three different menu; which is loaded depends on the role of the person that logs in. Admin people can visualize animal and staff lists and modify them; they can also monitor the upcomimg visits booked from external people.
Veterinarians have the same menu of Admin people but with more privileges: they can input and modify clinical parameters about animal health status.
The last menu appears to external people(donors): they can book a visit at the shelter or adopt an animal from distance to contribute to care costs.

LISTS OF FUNCTIONALITIES:

 Administrators

View and manage complete animal registry
Oversee staff member database
Monitor upcoming scheduled visits
Add new animals and staff members to the system
Generate comprehensive reports

  Veterinarians

All administrator privileges plus:
Perform clinical operations on individual animals (vaccination, sterilization, medical care)
Execute bulk operations on animal groups with exclusion capabilities
Maintain detailed medical histories and health records
Generate health statistics and veterinary reports
Manage animal groups for efficient healthcare delivery

  External Donors

Schedule visits to the shelter with flexible time slots
View and manage their visit history
Browse available animals for adoption
Process adoption requests for distance adoption programs
Check available visit slots and reschedule as needed

TECHNOLOGIES AND PATTERN USED

-COLLECTIONS: used to handle groups of data regarding animals or internal/external people 
(For example
private List<Animal> adoptableAnimals;
private HashMap<String, Animal> adopted;
private HashMap<Donor, Animal> adoptionMatches;
private Map<Integer, Visit> visitLookup;
private Set<String> speciesSet = new TreeSet<>();
)

-JAVA I/O :  file management for uploading people and animals from saved files and persisting new members automatically.
-STREAM API&LAMBDA : present in all the project to process data ( to map, filter collections of data)
-CUSTOM ANNOTATION: in OasisExceptionShieldingHandler class
-GENERICS: to implement type safety and reduced casting
(for example
  List<Animal>, Map<String, Animal>, Set<String>
public interface RiskySupplier<T> { T get() throws Exception; }
)
-LOGGING: only in OasisExceptionShieldingHandler e Main for debugging
-JUNIT: to test the core classes of the project
-MOCKITO: used only in isolated unit testing
-INVERSION OF CONTROL: implemented for menu creation within factory pattern, improving testability
-FACTORY PATTERN: implemented in menu creation; it creates appropriate menu interfaces based on user roles. Enables easy addition of new user roles without modifying existing code. 
-COMPOSITE PATTERN: implemented in package groups to allow veterinarians to perform clinical operations on single animals or entire groups seamlessly.
-ITERATOR PATTERN: implemented to handle visit booking and available slot management. Enables efficient scheduling by automatically advancing through dates and time periods while respecting capacity constraints.
-EXCEPTION SHIELDING PATTERN: Converts technical exceptions into user-friendly business exceptions
-FACADE PATTERN: ShelterClient class acts as facade for adoption operations. Hides complexity and coordinates Adoption, DataService, and file management.
-TEMPLATE PATTERN: abstract being ensures that the overall structure of entities structure remains consistent

SETUP AND EXECUTION INSTRUCTIONS

SETUP : 

Import project in your IDE

Project Structure Setup
oasis-animal-shelter/
├── src/
│   └── organizer/
│       ├── activities/
│       ├── clients/
│       ├── datamanagement/
│       ├── entities/
│       ├── exceptionmanager/
│       ├── groups/
│       ├── menus/
│       └── services/
├── Animal-list.txt
├── People-list.txt
└── README.md

-The system requires two data files in the project root:
Animal-list.txt (Format: Animal [Gender] [Name] [Species])

People-list.txt (Format: [Type] [Gender] [Name] [Surname] [BirthDate] [Profession] [Role])

-Run organizer.Main class
-Follow the interactive prompts

-LOG IN
Existing User: Enter first name and surname from People-list.txt
New Donor: Create a new donor account through the registration process

![Diagramma UML](out/diagram.svg)

LIMITATIONS

It is a management software that can be designed for a shelter that has just started its activity, as it lacks many implementations that reflect real life. The limits are, for example, the lack of payment classes, a basic management of visiting hours and a basic development of activities, a limited possibility of choosing an animal to adopt, a log in based on name and surname. Advanced iterators patterns and other ones could be implemented if the project were to develop.