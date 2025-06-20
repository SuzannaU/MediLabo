package medilabo.patientsapp.repository;

import medilabo.patientsapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepo extends JpaRepository<Patient, Integer> {
}
