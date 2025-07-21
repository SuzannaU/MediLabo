package medilabo.risksapp.model;

import java.time.LocalDate;

public class Patient {

    private int patientId;
    private String gender;
    private LocalDate birthdate;
    private String age;
    private int numOfTriggers;
    private RiskLevel riskLevel;

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getNumOfTriggers() {
        return numOfTriggers;
    }

    public void setNumOfTriggers(int numOfTriggers) {
        this.numOfTriggers = numOfTriggers;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
