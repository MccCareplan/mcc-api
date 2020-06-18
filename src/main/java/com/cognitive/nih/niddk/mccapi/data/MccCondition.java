package com.cognitive.nih.niddk.mccapi.data;

public class MccCondition {
    private String FHIRId;
    private MccCodeableConcept clinicalStatus;
    private MccCodeableConcept verifiationStatus;
    private MccCodeableConcept[] categories;
    private MccCodeableConcept severity;
    private MccCodeableConcept code;
    private String onset;
    private String abatement;
    private String recordedDate;
    private String recorder;
    private String asserter;
    private String note;
    //TODO: Consider Identifier
    //TODO: Deal with stage
    //TODO: Deal with evidence;


    public String getFHIRId() {
        return FHIRId;
    }

    public void setFHIRId(String FHIRId) {
        this.FHIRId = FHIRId;
    }

    public MccCodeableConcept getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(MccCodeableConcept clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public MccCodeableConcept getVerifiationStatus() {
        return verifiationStatus;
    }

    public void setVerifiationStatus(MccCodeableConcept verifiationStatus) {
        this.verifiationStatus = verifiationStatus;
    }

    public MccCodeableConcept[] getCategories() {
        return categories;
    }

    public void setCategories(MccCodeableConcept[] categories) {
        this.categories = categories;
    }

    public MccCodeableConcept getSeverity() {
        return severity;
    }

    public void setSeverity(MccCodeableConcept severity) {
        this.severity = severity;
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public String getAbatement() {
        return abatement;
    }

    public void setAbatement(String abatement) {
        this.abatement = abatement;
    }

    public String getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(String recordedDate) {
        this.recordedDate = recordedDate;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getAsserter() {
        return asserter;
    }

    public void setAsserter(String asserter) {
        this.asserter = asserter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
