package medilabo.patientsapp.exceptions;

/**
 * Custom Exception used to signal a non-existing patient.
 */
public class NonExistingPatientException extends RuntimeException {

    private String message;

    public NonExistingPatientException() {
    }
    public NonExistingPatientException(String message) {
        super(message);
    }
}
