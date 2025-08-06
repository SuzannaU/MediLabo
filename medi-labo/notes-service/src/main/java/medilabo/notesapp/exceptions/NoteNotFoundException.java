package medilabo.notesapp.exceptions;

public class NoteNotFoundException extends RuntimeException {

    private String message;

    public NoteNotFoundException() {
    }

    public NoteNotFoundException(String message) {
        super(message);
    }
}
