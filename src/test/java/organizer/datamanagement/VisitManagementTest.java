package organizer.datamanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import organizer.entities.*;
import organizer.activities.*;
import organizer.exceptionmanager.*;
import java.util.List;
import java.util.Iterator;


 //Test class for VisitManagement 
 
 
class VisitManagementTest {

   private VisitManagement visitManager;
    private Donor testDonor1;
    private Donor testDonor2;
    private Donor testDonor3;

    @BeforeEach
    void setUp() {
        visitManager = new VisitManagement();
        
        // Create test donors with different status
        testDonor1 = new Donor("F", "Maria", "Rossi", "1985-06-15", Role.DONOR);
        testDonor2 = new Donor("M", "Giovanni", "Verdi", "1980-03-20", Role.DONOR);
        testDonor3 = new Donor("F", "Anna", "Bianchi", "1990-12-10", Role.DONOR);
    }

    @AfterEach
    void tearDown() {
        visitManager = null;
        testDonor1 = null;
        testDonor2 = null;
        testDonor3 = null;
    }

    // VISIT BOOKING TESTS

    @Test
    @DisplayName("Should book visit successfully with valid data")
    void testSuccessfulVisitBooking() {
        assertDoesNotThrow(() -> {
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            
            assertNotNull(visit);
            assertEquals(testDonor1, visit.getVisitor());
            assertEquals("2024-12-25", visit.getDate());
            assertEquals(PeriodTime.Morning, visit.getTime());
            assertTrue(visit.isActive());
            assertTrue(visit.getVisitId() > 0);
            
            // Check donor status changed to Visitor (if was None)
            assertEquals(Status.Visitor, testDonor1.getStatusDonator());
        });
    }

    @Test
    @DisplayName("Should book visit for donor who is already Adopter")
    void testBookVisitForAdopterDonor() {
        assertDoesNotThrow(() -> {
            // Set donor as Adopter first
            testDonor1.setStatusDonator(Status.Adopter);
            
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Afternoon);
            
            assertNotNull(visit);
            // Status should remain Adopter, not change to Visitor
            assertEquals(Status.Adopter, testDonor1.getStatusDonator());
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid date format")
    void testInvalidDateFormat() {
        // Test various invalid date formats
        assertThrows(OasisUserException.class, () -> {
            visitManager.bookVisit(testDonor1, "25-12-2024", PeriodTime.Morning); // Wrong format
        });
        
        assertThrows(OasisUserException.class, () -> {
            visitManager.bookVisit(testDonor1, "2024/12/25", PeriodTime.Morning); // Wrong separator
        });
        
        assertThrows(OasisUserException.class, () -> {
            visitManager.bookVisit(testDonor1, "24-12-25", PeriodTime.Morning); // Short year
        });
        
        assertThrows(OasisUserException.class, () -> {
            visitManager.bookVisit(testDonor1, "invalid-date", PeriodTime.Morning); // Invalid
        });
    }

    @Test
    @DisplayName("Should reject invalid parameters")
    void testInvalidParametersInBooking() {
        // Test null date - this should definitely fail due to validateDateFormat
        assertThrows(Exception.class, () -> {
            visitManager.bookVisit(testDonor1, null, PeriodTime.Morning);
        }, "Booking with null date should throw an exception");
        
        // Test null donor - this should fail when trying to set status
        assertThrows(Exception.class, () -> {
            visitManager.bookVisit(null, "2024-12-25", PeriodTime.Morning);
        }, "Booking with null donor should throw an exception");
    }

    @Test
    @DisplayName("Should handle null PeriodTime (current implementation allows it)")
    void testNullPeriodTimeHandling() {
        // Current implementation apparently accepts null PeriodTime
        // Let's test that it doesn't crash
        assertDoesNotThrow(() -> {
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", null);
            assertNotNull(visit);
            assertNull(visit.getTime()); // Should be null as passed
            assertEquals("2024-12-25", visit.getDate());
            assertEquals(testDonor1, visit.getVisitor());
        }, "Booking with null PeriodTime should be handled gracefully");
    }

    @Test
    @DisplayName("Should generate unique visit IDs")
    void testUniqueVisitIds() {
        assertDoesNotThrow(() -> {
            Visit visit1 = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            Visit visit2 = visitManager.bookVisit(testDonor2, "2024-12-25", PeriodTime.Afternoon);
            Visit visit3 = visitManager.bookVisit(testDonor3, "2024-12-26", PeriodTime.Morning);
            
            // All IDs should be different
            assertNotEquals(visit1.getVisitId(), visit2.getVisitId());
            assertNotEquals(visit2.getVisitId(), visit3.getVisitId());
            assertNotEquals(visit1.getVisitId(), visit3.getVisitId());
            
            // IDs should be sequential
            assertEquals(visit1.getVisitId() + 1, visit2.getVisitId());
            assertEquals(visit2.getVisitId() + 1, visit3.getVisitId());
        });
    }

    // CAPACITY AND AVAILABILITY TESTS

    @Test
    @DisplayName("Should handle maximum capacity per period")
    void testMaximumCapacityPerPeriod() {
        assertDoesNotThrow(() -> {
            String testDate = "2024-12-25";
            PeriodTime testPeriod = PeriodTime.Morning;
            
            // Book 10 visits (maximum capacity)
            for (int i = 0; i < 10; i++) {
                Donor donor = new Donor("F", "User" + i, "Test", "1990-01-01", Role.DONOR);
                Visit visit = visitManager.bookVisit(donor, testDate, testPeriod);
                assertNotNull(visit);
            }
            
            // 11th visit should fail due to capacity
            Donor extraDonor = new Donor("M", "Extra", "User", "1990-01-01", Role.DONOR);
            assertThrows(OasisUserException.class, () -> {
                visitManager.bookVisit(extraDonor, testDate, testPeriod);
            });
        });
    }

    @Test
    @DisplayName("Should allow bookings in different periods on same date")
    void testDifferentPeriodsCapacity() {
        assertDoesNotThrow(() -> {
            String testDate = "2024-12-25";
            
            // Book maximum in morning
            for (int i = 0; i < 10; i++) {
                Donor donor = new Donor("F", "Morning" + i, "Test", "1990-01-01", Role.DONOR);
                visitManager.bookVisit(donor, testDate, PeriodTime.Morning);
            }
            
            // Should still be able to book in afternoon
            Visit afternoonVisit = visitManager.bookVisit(testDonor1, testDate, PeriodTime.Afternoon);
            assertNotNull(afternoonVisit);
            assertEquals(PeriodTime.Afternoon, afternoonVisit.getTime());
        });
    }

    @Test
    @DisplayName("Should count visits per date and period correctly")
    void testVisitCountingPerDateAndPeriod() {
        assertDoesNotThrow(() -> {
            String date1 = "2024-12-25";
            String date2 = "2024-12-26";
            
            // Book some visits
            visitManager.bookVisit(testDonor1, date1, PeriodTime.Morning);
            visitManager.bookVisit(testDonor2, date1, PeriodTime.Morning);
            visitManager.bookVisit(testDonor3, date1, PeriodTime.Afternoon);
            
            visitManager.bookVisit(testDonor1, date2, PeriodTime.Morning);
            
            // Test counting
            assertEquals(2, visitManager.getVisitsForDateAndPeriod(date1, PeriodTime.Morning));
            assertEquals(1, visitManager.getVisitsForDateAndPeriod(date1, PeriodTime.Afternoon));
            assertEquals(1, visitManager.getVisitsForDateAndPeriod(date2, PeriodTime.Morning));
            assertEquals(0, visitManager.getVisitsForDateAndPeriod(date2, PeriodTime.Afternoon));
        });
    }

    // VISIT RESCHEDULING TESTS

    @Test
    @DisplayName("Should reschedule visit successfully")
    void testSuccessfulVisitRescheduling() {
        assertDoesNotThrow(() -> {
            // Book initial visit
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            String visitId = String.valueOf(visit.getVisitId());
            
            // Reschedule visit
            visitManager.rescheduleVisit(visitId, "2024-12-26", PeriodTime.Afternoon);
            
            // Verify changes
            Visit rescheduledVisit = visitManager.findVisitById(visitId);
            assertEquals("2024-12-26", rescheduledVisit.getDate());
            assertEquals(PeriodTime.Afternoon, rescheduledVisit.getTime());
            assertTrue(rescheduledVisit.isActive());
        });
    }

    @Test
    @DisplayName("Should throw exception when rescheduling non-existent visit")
    void testRescheduleNonExistentVisit() {
        assertThrows(OasisUserException.class, () -> {
            visitManager.rescheduleVisit("999", "2024-12-26", PeriodTime.Afternoon);
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid date in rescheduling")
    void testRescheduleWithInvalidDate() {
        assertDoesNotThrow(() -> {
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            String visitId = String.valueOf(visit.getVisitId());
            
            assertThrows(OasisUserException.class, () -> {
                visitManager.rescheduleVisit(visitId, "invalid-date", PeriodTime.Afternoon);
            });
        });
    }

    // VISIT CANCELLATION TESTS

    @Test
    @DisplayName("Should cancel visit successfully")
    void testSuccessfulVisitCancellation() {
        assertDoesNotThrow(() -> {
            // Book visit
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            String visitId = String.valueOf(visit.getVisitId());
            
            // Check initial status
            assertEquals(Status.Visitor, testDonor1.getStatusDonator());
            
            // Cancel visit
            visitManager.cancelVisit(visitId);
            
            // Verify cancellation
            Visit cancelledVisit = visitManager.findVisitById(visitId);
            assertFalse(cancelledVisit.isActive());
            
            // Check donor status changed back to None
            assertEquals(Status.None, testDonor1.getStatusDonator());
        });
    }

    @Test
    @DisplayName("Should not change status when cancelling visit for Adopter")
    void testCancelVisitForAdopter() {
        assertDoesNotThrow(() -> {
            // Set donor as Adopter
            testDonor1.setStatusDonator(Status.Adopter);
            
            // Book visit
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            String visitId = String.valueOf(visit.getVisitId());
            
            // Status should remain Adopter
            assertEquals(Status.Adopter, testDonor1.getStatusDonator());
            
            // Cancel visit
            visitManager.cancelVisit(visitId);
            
            // Status should still be Adopter, not None
            assertEquals(Status.Adopter, testDonor1.getStatusDonator());
        });
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-existent visit")
    void testCancelNonExistentVisit() {
        assertThrows(OasisUserException.class, () -> {
            visitManager.cancelVisit("999");
        });
    }

    // VISIT LOOKUP AND RETRIEVAL TESTS

    @Test
    @DisplayName("Should find visit by ID successfully")
    void testFindVisitById() {
        assertDoesNotThrow(() -> {
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            String visitId = String.valueOf(visit.getVisitId());
            
            Visit foundVisit = visitManager.findVisitById(visitId);
            assertNotNull(foundVisit);
            assertEquals(visit.getVisitId(), foundVisit.getVisitId());
            assertEquals(visit.getVisitor(), foundVisit.getVisitor());
            assertEquals(visit.getDate(), foundVisit.getDate());
            assertEquals(visit.getTime(), foundVisit.getTime());
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid visit ID format")
    void testFindVisitByInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            visitManager.findVisitById("invalid-id");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            visitManager.findVisitById("abc123");
        });
    }

    @Test
    @DisplayName("Should throw exception for non-existent visit ID")
    void testFindNonExistentVisitById() {
        assertThrows(IllegalArgumentException.class, () -> {
            visitManager.findVisitById("999");
        });
    }

    @Test
    @DisplayName("Should get visits for specific donor")
    void testGetVisitsForDonor() {
        assertDoesNotThrow(() -> {
            // Book multiple visits for different donors
            Visit visit1 = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            Visit visit2 = visitManager.bookVisit(testDonor1, "2024-12-26", PeriodTime.Afternoon);
            Visit visit3 = visitManager.bookVisit(testDonor2, "2024-12-25", PeriodTime.Afternoon);
            
            // Get visits for testDonor1
            List<Visit> donor1Visits = visitManager.getVisitsForDonor(testDonor1);
            assertEquals(2, donor1Visits.size());
            assertTrue(donor1Visits.contains(visit1));
            assertTrue(donor1Visits.contains(visit2));
            assertFalse(donor1Visits.contains(visit3));
            
            // Get visits for testDonor2
            List<Visit> donor2Visits = visitManager.getVisitsForDonor(testDonor2);
            assertEquals(1, donor2Visits.size());
            assertTrue(donor2Visits.contains(visit3));
        });
    }

    @Test
    @DisplayName("Should get active visits only")
    void testGetActiveVisits() {
        assertDoesNotThrow(() -> {
            // Book some visits
            Visit visit1 = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            Visit visit2 = visitManager.bookVisit(testDonor2, "2024-12-26", PeriodTime.Afternoon);
            Visit visit3 = visitManager.bookVisit(testDonor3, "2024-12-27", PeriodTime.Morning);
            
            // Cancel one visit
            visitManager.cancelVisit(String.valueOf(visit2.getVisitId()));
            
            // Get active visits
            List<Visit> activeVisits = visitManager.getActiveVisits();
            assertEquals(2, activeVisits.size());
            assertTrue(activeVisits.contains(visit1));
            assertFalse(activeVisits.contains(visit2)); // Should be excluded (cancelled)
            assertTrue(activeVisits.contains(visit3));
        });
    }

    @Test
    @DisplayName("Should get visits for specific date")
    void testGetVisitsForDate() {
        assertDoesNotThrow(() -> {
            String targetDate = "2024-12-25";
            String otherDate = "2024-12-26";
            
            // Book visits on different dates
            Visit visit1 = visitManager.bookVisit(testDonor1, targetDate, PeriodTime.Morning);
            Visit visit2 = visitManager.bookVisit(testDonor2, targetDate, PeriodTime.Afternoon);
            Visit visit3 = visitManager.bookVisit(testDonor3, otherDate, PeriodTime.Morning);
            
            // Get visits for target date
            List<Visit> dateVisits = visitManager.getVisitsForDate(targetDate);
            assertEquals(2, dateVisits.size());
            assertTrue(dateVisits.contains(visit1));
            assertTrue(dateVisits.contains(visit2));
            assertFalse(dateVisits.contains(visit3));
        });
    }

    // AVAILABILITY ITERATOR TESTS

    @Test
    @DisplayName("Should create availability iterator")
    void testAvailabilityIterator() {
        Iterator<String> iterator = visitManager.getAvailableSlotsIterator(7);
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
    }

    @Test
    @DisplayName("Should get next available slots")
    void testGetNextAvailableSlots() {
        List<String> slots = visitManager.getNextAvailableSlots(5, 7);
        assertNotNull(slots);
        assertTrue(slots.size() <= 5);
        assertTrue(slots.size() <= 14); // 7 days * 2 periods per day
        
        // Each slot should contain date, period, and available spaces info
        for (String slot : slots) {
            assertTrue(slot.contains("Morning") || slot.contains("Afternoon"));
            assertTrue(slot.contains("spaces available"));
        }
    }

    @Test
    @DisplayName("Should check availability from tomorrow")
    void testHasAvailabilityFromTomorrow() {
        // Should have availability for the next few days
        assertTrue(visitManager.hasAvailabilityFromTomorrow(7));
        assertTrue(visitManager.hasAvailabilityFromTomorrow(1));
    }

    @Test
    @DisplayName("Should reduce available slots when booking visits")
    void testAvailabilityAfterBooking() {
        assertDoesNotThrow(() -> {
            String testDate = "2025-01-15";
            PeriodTime testPeriod = PeriodTime.Morning;
            
            // Check initial availability
            long initialCount = visitManager.getVisitsForDateAndPeriod(testDate, testPeriod);
            
            // Book a visit
            visitManager.bookVisit(testDonor1, testDate, testPeriod);
            
            // Check availability decreased
            long newCount = visitManager.getVisitsForDateAndPeriod(testDate, testPeriod);
            assertEquals(initialCount + 1, newCount);
        });
    }

    // EDGE CASES AND ERROR HANDLING

    @Test
    @DisplayName("Should handle empty visit lists gracefully")
    void testEmptyVisitLists() {
        // Initially, all lists should be empty but not null
        assertNotNull(visitManager.getAllVisits());
        assertTrue(visitManager.getAllVisits().isEmpty());
        
        assertNotNull(visitManager.getActiveVisits());
        assertTrue(visitManager.getActiveVisits().isEmpty());
        
        assertNotNull(visitManager.getVisitsForDonor(testDonor1));
        assertTrue(visitManager.getVisitsForDonor(testDonor1).isEmpty());
        
        assertNotNull(visitManager.getVisitsForDate("2024-12-25"));
        assertTrue(visitManager.getVisitsForDate("2024-12-25").isEmpty());
        
        assertEquals(0, visitManager.getVisitsForDateAndPeriod("2024-12-25", PeriodTime.Morning));
    }

    @Test
    @DisplayName("Should handle multiple bookings by same donor")
    void testMultipleBookingsSameDonor() {
        assertDoesNotThrow(() -> {
            // Book multiple visits for same donor
            Visit visit1 = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            Visit visit2 = visitManager.bookVisit(testDonor1, "2024-12-26", PeriodTime.Afternoon);
            Visit visit3 = visitManager.bookVisit(testDonor1, "2024-12-27", PeriodTime.Morning);
            
            // All should be successful
            assertNotNull(visit1);
            assertNotNull(visit2);
            assertNotNull(visit3);
            
            // All should have unique IDs
            assertNotEquals(visit1.getVisitId(), visit2.getVisitId());
            assertNotEquals(visit2.getVisitId(), visit3.getVisitId());
            
            // Donor should have Visitor status (set by first booking)
            assertEquals(Status.Visitor, testDonor1.getStatusDonator());
            
            // Should be able to retrieve all visits for this donor
            List<Visit> donorVisits = visitManager.getVisitsForDonor(testDonor1);
            assertEquals(3, donorVisits.size());
        });
    }

    @Test
    @DisplayName("Should handle date boundary conditions")
    void testDateBoundaryConditions() {
        // Test various date formats that should be valid
        assertDoesNotThrow(() -> {
            visitManager.bookVisit(testDonor1, "2024-01-01", PeriodTime.Morning); // New Year
            visitManager.bookVisit(testDonor2, "2024-12-31", PeriodTime.Afternoon); // End of year
            visitManager.bookVisit(testDonor3, "2024-02-29", PeriodTime.Morning); // Leap year
        });
    }

    // DISPLAY METHODS TESTS (These would typically test output, here we test they don't crash)

    @Test
    @DisplayName("Should handle display methods without crashing")
    void testDisplayMethods() {
        assertDoesNotThrow(() -> {
            // Test with empty visits
            visitManager.displayScheduledVisits();
            visitManager.displayAllAvailableSlots(7);
            visitManager.displayUserVisits(testDonor1);
            
            // Book some visits and test again
            visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            visitManager.bookVisit(testDonor2, "2024-12-26", PeriodTime.Afternoon);
            
            visitManager.displayScheduledVisits();
            visitManager.displayAllAvailableSlots(7);
            visitManager.displayUserVisits(testDonor1);
        });
    }

    // INTEGRATION TESTS

    @Test
    @DisplayName("Should handle complete visit lifecycle")
    void testCompleteVisitLifecycle() {
        assertDoesNotThrow(() -> {
            // 1. Book visit
            Visit visit = visitManager.bookVisit(testDonor1, "2024-12-25", PeriodTime.Morning);
            assertNotNull(visit);
            assertTrue(visit.isActive());
            assertEquals(Status.Visitor, testDonor1.getStatusDonator());
            
            String visitId = String.valueOf(visit.getVisitId());
            
            // 2. Reschedule visit
            visitManager.rescheduleVisit(visitId, "2024-12-26", PeriodTime.Afternoon);
            Visit rescheduledVisit = visitManager.findVisitById(visitId);
            assertEquals("2024-12-26", rescheduledVisit.getDate());
            assertEquals(PeriodTime.Afternoon, rescheduledVisit.getTime());
            assertTrue(rescheduledVisit.isActive());
            
            // 3. Cancel visit
            visitManager.cancelVisit(visitId);
            Visit cancelledVisit = visitManager.findVisitById(visitId);
            assertFalse(cancelledVisit.isActive());
            assertEquals(Status.None, testDonor1.getStatusDonator());
            
            // 4. Verify visit appears in getters but marked as inactive
            List<Visit> allVisits = visitManager.getAllVisits();
            assertTrue(allVisits.contains(cancelledVisit));
            
            List<Visit> activeVisits = visitManager.getActiveVisits();
            assertFalse(activeVisits.contains(cancelledVisit));
        });
    }
   

}
