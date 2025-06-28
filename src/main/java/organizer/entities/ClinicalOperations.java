package organizer.entities;
import java.time.LocalDate;
import java.util.List;
// i create an interface which methods are implemented by Animal Class
public interface ClinicalOperations {
    void performVaccination(String vaccineType, LocalDate date);
    void setSterilizationStatus(boolean sterilized, LocalDate date, Admin veterinarian);
    void setUnderMedicalCare(boolean underCare, Admin veterinarian, String condition);
    void addMedicalNote(String note, LocalDate date, Admin veterinarian);
    boolean isVaccinated();
    boolean isSterilized();
    boolean isUnderMedicalCare();
    List<String> getMedicalHistory();
}
