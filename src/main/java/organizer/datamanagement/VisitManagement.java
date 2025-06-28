package organizer.datamanagement;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Iterator;

import organizer.activities.Visit;
import organizer.entities.Donor;
import organizer.entities.Status;
import organizer.activities.PeriodTime;
import organizer.exceptionmanager.OasisExceptionShieldingHandler;
import organizer.exceptionmanager.OasisUserException;


/*
Visit management: allowing booking, modification, cancellation and availability checking
 */

public class VisitManagement {

    private final List<Visit> allVisits = new ArrayList<>();                    // All visits in the system
    private Map<Integer, Visit> visitLookup = new HashMap<>();           // Access by  ID
    private static final int MAX_VISITS_PER_PERIOD = 10;
 
    //books a new visit with validation and slots checking

    public Visit bookVisit(Donor donor, String date, PeriodTime time) 
            throws OasisUserException {
        try {
             //PeriodTime and donor cannot be null
            if (time == null) {
            throw new IllegalArgumentException("PeriodTime cannot be null");
             }
           if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null");
           }
            // Valid date format (yyy/mm/dd). Checks it

            validateDateFormat(date);

            // Checks capacity for requested time slot

            validateCapacityAvailable(date, time);

            //create and register the visit in our List/Map

            Visit newVisit = new Visit(donor, date, time);
            allVisits.add(newVisit);
            visitLookup.put(newVisit.getVisitId(),newVisit);
            if(donor.getStatusDonator().equals(Status.None)){
            donor.setStatusDonator(Status.Visitor); // person acquires Visitor status booking a visit only if he is not Adopter
            }
            System.out.println("Visit booked successfully:ID "+newVisit.getVisitId());

            return newVisit;

         } catch(Exception e){
            throw new OasisUserException("Visit booking failed:"+e.getMessage());
         }

        }
            
            
    
  //method to modify visit
    public void rescheduleVisit(String visitId, String newDate, PeriodTime newTime) 
            throws OasisUserException {
        OasisExceptionShieldingHandler.executeWithShield(() -> {
            Visit visit = findVisitById(visitId);
            validateDateFormat(newDate);
            visit.reschedule(newDate, newTime);
        }, "visit rescheduling");
      }


    //cancel visit

    public void cancelVisit(String visitId) throws OasisUserException {
        OasisExceptionShieldingHandler.executeWithShield(() -> {
            Visit visit = findVisitById(visitId);
            if(visit.getVisitor().getStatusDonator().equals(Status.Visitor)){
            visit.getVisitor().setStatusDonator(Status.None);
            }
            
           visit.cancel();
        }, "Visit Cancellation");
    }

    //methods for visit information

    public Visit findVisitById(String visitId){ // String as user input is a string

        try{
           
            int id = Integer.parseInt(visitId);
            Visit visit = visitLookup.get(id);
            if(visit == null) {

                throw new IllegalArgumentException("Visit not founf with ID: " +visitId);
        
            }

             return visit;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Invalid visit ID format" +visitId);
        }

    }
    
    //visits for a specific donor
    
      public List<Visit> getVisitsForDonor(Donor donor) {
        return allVisits.stream()
                    .filter(v -> v.getVisitor().equals(donor))
                    .collect(Collectors.toList());
    }
    
   
    //all visits booked not removed

    public List<Visit> getActiveVisits() {
        return allVisits.stream()
                    .filter(Visit::isActive)
                    .collect(Collectors.toList());
    }
    
  
    // returns all visits for a specific date
    public List<Visit> getVisitsForDate(String date) {
        return allVisits.stream()
                    .filter(v -> v.getDate().equals(date))
                    .collect(Collectors.toList());
    }
    

    //helper method for iterator. It counts how many book visits for period in a date
    public long getVisitsForDateAndPeriod(String date, PeriodTime period) {
        return allVisits.stream()
            .filter(v -> v.getDate().equals(date))
            .filter(v -> v.getTime().equals(period))
            .filter(Visit::isActive)
            .count();
    }
    
    
    //availability checking method within specified days

    public Iterator<String> getAvailableSlotsIterator(int maxDaysAhead) {
        return new AvailableSlotsIterator(this, maxDaysAhead);
    }
  
    //returns list of the next n available slot (n defined by count)

    public List<String> getNextAvailableSlots(int count, int maxDaysAhead) {
        List<String> availableSlots = new ArrayList<>();
        Iterator<String> iterator = getAvailableSlotsIterator(maxDaysAhead);
        
        while (iterator.hasNext() && availableSlots.size() < count) {
            availableSlots.add(iterator.next());
        }
        
        return availableSlots;
    }
    
  //checks if there are slots available in a timeframe

    public boolean hasAvailabilityFromTomorrow(int daysAhead) {
        return getAvailableSlotsIterator(daysAhead).hasNext();
    }

    
//Display methods
 
    //Display all scheduled visits in the formatted output

    public void displayScheduledVisits() {
        List<Visit> activeList = getActiveVisits();
        
        if (activeList.isEmpty()) {
            System.out.println("Not upcoming visits");
            return;
        }
        
        System.out.println("\n---UPCOMING VISITS---");
        activeList.stream().forEach(System.out::println);
    }
    
  //Display all the available slots for specified number of days 

    public void displayAllAvailableSlots(int daysAhead ) {

         System.out.println("\n--- Available slots(Next " + daysAhead + " Days) ---");
        
        Iterator<String> iterator = getAvailableSlotsIterator(daysAhead);
        
        if (!iterator.hasNext()) {
            System.out.println("No available slots in the next " + daysAhead + " days.");
            return;
        }
        
        int count = 1;
        while (iterator.hasNext()) {
            System.out.println(count+"."+iterator.next());
            count++;
        }
        
        System.out.println("\nTotal available slots: " + (count - 1));


    }

    //Display all visits for a specific donor

     public void displayUserVisits(Donor donor) {
        List<Visit> userVisits = getVisitsForDonor(donor);
        
        if (userVisits.isEmpty()) {
            System.out.println("No visit found for "+donor.getName());
            return;
        }
        
        System.out.println("\n---  VISITS for "+donor.getName()+"---");
        userVisits.forEach(System.out::println);
    }
    
    //method that checks the valid format

    private void validateDateFormat(String date) {
        if(!date.matches("^\\d{4}-\\d{2}-\\d{2}$")){
            throw new IllegalArgumentException("Invalid date format! Use YYYY-MM-DD");
        }
    }

    //Check the capcity of a requested time slot

    private void validateCapacityAvailable(String date, PeriodTime time){

        long currentBookings = getVisitsForDateAndPeriod(date, time);
        if(currentBookings >= MAX_VISITS_PER_PERIOD){
            throw new IllegalArgumentException("Time slot full. Maximum "+ MAX_VISITS_PER_PERIOD+"visitors per period");
        }

    }
   
    
    
   //return all visits
    public List<Visit> getAllVisits() {
        return new ArrayList<>(allVisits);
    }

}


