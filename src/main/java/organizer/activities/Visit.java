package organizer.activities;

import organizer.entities.Donor;

//it manages visits (creation, modification and cancellation). Use of a autoincrementing ID for unique visit identification
public class Visit {

    private int visitId;
    private static int nextAvailableId = 1; //shared with all visit instances
    private Donor visitor;
    private String scheduledDate;
    private PeriodTime scheduledTime;
    private boolean active;

    //with this constructor with three elements it is possible to create a visit. It assigns a unique id and sets visit as active
    public Visit(Donor visitor, String date, PeriodTime time) {

        this.visitId = nextAvailableId++; // 
        this.visitor = visitor;
        this.scheduledDate = date;
        this.scheduledTime = time;
        this.active=true;

    }
  //method to modify visit
    public void reschedule(String newDate, PeriodTime newTime){
        if(!active){
            throw new IllegalStateException("Cannot reschedule visit not active"); // exception for status not suitable for operation
        }
        
        this.scheduledDate = newDate;
        this.scheduledTime = newTime;
        
        System.out.println("Visit "+visitId+"rescheduled to "+newDate+" "+newTime);

    }

    //method to cancel the visit
    public void cancel(){
       
        this.active=false;
        System.out.println("Visit"+ visitId+"has been cancelled");

    }

    //method to verify if the visit is active
    public boolean isActive(){
        
       return active;

    }

   public int getVisitId() { 
        return visitId;
     }
    public Donor getVisitor() {
         return visitor;
        }
    public String getDate() { 
        return scheduledDate;
        }
    public PeriodTime getTime() {
         return scheduledTime;
        }
   
  


    
}




