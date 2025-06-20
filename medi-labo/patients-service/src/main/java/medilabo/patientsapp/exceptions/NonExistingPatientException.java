package medilabo.patientsapp.exceptions;

public class NonExistingPatientException extends RuntimeException {

    private String message;

    public NonExistingPatientException() {
    }
    public NonExistingPatientException(String message) {
        super(message);
    }
}
