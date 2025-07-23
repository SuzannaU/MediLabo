package medilabo.risksapp.model;

public class Note {

    private int patientId;
    private String content;

    public Note() {
    }

    public Note(String content) {
        this.content = content;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
