package organizer.datamanagement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

import organizer.activities.PeriodTime;
import organizer.datamanagement.VisitManagement;

//Iterator implemented for finding available visit slots. Used to manage visit bookings and their availability

public class AvailableSlotsIterator implements Iterator<String> {
   private LocalDate currentDate;
    private final LocalDate maxDate;
    private final VisitManagement visitManager;
    private final List<PeriodTime> availablePeriods;
    private int periodIndex; //pointer to the elements of periods list
    
    
    public AvailableSlotsIterator(VisitManagement visitManager, int maxDaysAhead) {
        this.currentDate = LocalDate.now().plusDays(1); //it is possibile to book a visit for the following days not the current one
        this.maxDate = LocalDate.now().plusDays(maxDaysAhead);//how many days we want to see the availability
        this.visitManager = visitManager; //we need lists of that class
        this.availablePeriods = Arrays.asList(PeriodTime.Morning, PeriodTime.Afternoon); // fixed list composed of only two elements
        this.periodIndex = 0;
    }
    
    @Override
    public boolean hasNext() {
        // i look for the next available slot through a cycle that visits the days established setting the max day
        while (currentDate.isBefore(maxDate) || currentDate.equals(maxDate)) {
            
            // internal cycle check the availability of the periods of date in consideration 
            while (periodIndex < availablePeriods.size()) {
                String dateStr = formatDate(currentDate);
                PeriodTime period = availablePeriods.get(periodIndex);
                
                // if this date/period has availability return true
                if (hasAvailableSlots(dateStr, period)) {
                    return true;
                }
                
                // if in the period considered there's not availability we pass to the second one
                periodIndex++;
            }
            
            // if in both the period there's not availability we pass to the following day
            currentDate = currentDate.plusDays(1);
            periodIndex = 0; // Reset periodi per il nuovo giorno
        }
        
        return false; //no availability found in entire range
    }
    
    @Override
    public String next() {
        //with this method we go on discovering what are the available date and period that the method hasNext() has found
        if (!hasNext()) {
            throw new NoSuchElementException("No more available slots");
        }
        
        String dateStr = formatDate(currentDate);
        PeriodTime period = availablePeriods.get(periodIndex);
        
        long currentBookings = visitManager.getVisitsForDateAndPeriod(dateStr, period);
        int availableSpaces = (int)(10 - currentBookings);
        
        String result = dateStr + " " + period + " (" + availableSpaces + " spaces available)";
        
        periodIndex++; // prepares next slot
        return result;
    }

    //method that defines date format 

    private String formatDate(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    //checks if specified date and period has available capacity  (10 for period)
    private boolean hasAvailableSlots(String date, PeriodTime period) {
        long visitsCount = visitManager.getVisitsForDateAndPeriod(date, period);
        return visitsCount < 10;
    }
}
