package medilabo.patientsapp.repository;

import medilabo.patientsapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Patient repository used to perform CRUD operations on the database.
 */
public interface PatientRepo extends JpaRepository<Patient, Integer> {
}
