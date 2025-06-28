package organizer.exceptionmanager;
//specific exception type for db and file system issues
public class DataAccessException extends OasisUserException {

    public DataAccessException(String message) {
        super("Error in data access: " + message);
    }
    
    public DataAccessException(String message, Throwable cause) {
        super("Error in data access: " + message, cause);
    }
    
    // Constructor for file not found error
    public static DataAccessException fileNotFound(String filename) {
        return new DataAccessException("File " + filename + " not found");
    }
    
    // for reading file errors
    public static DataAccessException readError(String filename, Throwable cause) {
        return new DataAccessException("Cannot read file " + filename + " ", cause);
    }
    
    // for writing on file errors
    public static DataAccessException writeError(String filename, Throwable cause) {
        return new DataAccessException("Cannot write to file " + filename + " ", cause);
    }

}
