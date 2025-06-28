package organizer.exceptionmanager;

import java.io.IOException;
import java.util.logging.Logger;
import organizer.exceptionmanager.*;

 //provides protection from technical exceptions
 //converts technical errors into user-friendly business exceptions
public class OasisExceptionShieldingHandler {

    // Logger instance for this class - used to record all errors for debugging
    private static final Logger logger = Logger.getLogger(OasisExceptionShieldingHandler.class.getName());
    
    
       //interface for operations that may throw exceptions
      //This allows us to use lambda expressions with the shield
     
    @FunctionalInterface
    public interface RiskyOperation {
        void execute() throws Exception;
    }
    
    
      //Functional interface for operations that return a value and may throw exceptions
     //This allows us to use lambda expressions for operations that need to return something
     
    @FunctionalInterface
    public interface RiskySupplier<T> {
        T get() throws Exception;
    }
    
    
     //Main Exception Shielding method; executes risky operations with comprehensive exception protection
     //Converts technical exceptions to user-friendly messages while mantaining debugging info
   
     
    public static void executeWithShield(RiskyOperation operation, String context) 
            throws OasisUserException {
        try {
            // Execute the risky operation (file reading, object creation, etc.)
            operation.execute();
            
        } catch (OasisUserException e) {
            // Business exceptions are already user-friendly, so pass them through unchanged
            // These are exceptions we created specifically for the shelter domain
            throw e;
            
        } catch (SexException | LengthException | AgeException | DateException | EmailException e) {
            // Custom validation exceptions are already user-friendly
            logger.warning("Validation error: " + e.getMessage());
            // Wrap them in  business exception 
            throw new OasisUserException("Invalid data: " + e.getMessage());
            
        }  catch (IOException e) {
            // handles file errors
            // Log the technical details
            logger.severe("File error in " + context + ": " + e.getMessage());
          
            String userMessage = createIOErrorMessage(e, context);
            throw new DataAccessException(userMessage, e);
            
        } catch (SecurityException e) {
            // Security-related errors 
            logger.warning("Security error in " + context + ": " + e.getMessage());
            throw new OasisUserException("Access denied: " + e.getMessage());
            
        } catch (IllegalArgumentException e) {
            // Invalid argument errors in user input
            logger.info("Invalid argument in " + context + ": " + e.getMessage());
            throw new OasisUserException("Invalid input: " + e.getMessage());
        
        } catch (Exception e) {
            //  any other unexpected errors
         
            logger.severe("Unexpected error in " + context + ": " + e.getMessage());
           
            throw new OasisUserException("Errore imprevisto. Contattare l'amministratore.");
        }
    }
    
   //Exception Shielding method for operations that return a value

    public static <T> T executeWithShield(RiskySupplier<T> supplier, String context) 
            throws OasisUserException {
        try {
            // Execute the risky operation and return its result
            return supplier.get();
            
        } catch (OasisUserException e) {
            //  already user-friendly, so pass them through unchanged
            throw e;
            
        } catch (SexException | LengthException | AgeException | DateException | EmailException e) {
            //  custom validation exceptions  already user-friendly
            logger.warning("Validation error: " + e.getMessage());
            // Wrap them 
            throw new OasisUserException("Invalid data: " + e.getMessage());
            
        } catch (IOException e) {
            //provides specific error messages for file errors
            logger.severe("File error in " + context + ": " + e.getMessage());
            String userMessage = createIOErrorMessage(e, context);
            throw new DataAccessException(userMessage, e);
            
        } catch (SecurityException e) {
            // security-related errors
            logger.warning("Security error in " + context + ": " + e.getMessage());
            throw new OasisUserException("Access denied: " + e.getMessage());
            
        } catch (IllegalArgumentException e) {
            // Invalid argument errors for input issues
            logger.info("Invalid argument in " + context + ": " + e.getMessage());
            throw new OasisUserException("Invalid input: " + e.getMessage());
            
        }    catch (Exception e) {
            // Catch-all for any other unexpected errors
            logger.severe("Unexpected error in " + context + ": " + e.getMessage());
            throw new OasisUserException("Errore imprevisto. Contattare l'amministratore.");
        }
    }
    
   //Helper method to create specific user-friendly messages for I/O errors
    //This method analyzes the technical IOException and converts it to a user-friendly message
    private static String createIOErrorMessage(IOException e, String context) {
        // Convert the exception message to lowercase for easier matching
        String message = e.getMessage().toLowerCase();
        
        // Check for specific types of I/O errors and provide appropriate user messages
        if (message.contains("no such file") || message.contains("file not found")) {
            // File doesn't exist
            return "File not found during " + context;
        } else if (message.contains("access denied")) {
            // Permission problems
            return "Denied access to file during " + context;
        } else {
            // generic I/O error 
            return "File system error during " + context;
        }
    }

}
