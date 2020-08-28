package com.cognitive.nih.niddk.mccapi.data;

public class GoalTarget {
    private MccCodeableConcept measure;
    private GenericValue value;

    private String dueType;
    private String due;
    //TODO - Add dueDate and durDuration
    public GoalTarget()
    {

    }

    public MccCodeableConcept getMeasure() {
        return measure;
    }

    public void setMeasure(MccCodeableConcept measure) {
        this.measure = measure;
    }

    public GenericValue getValue() {
        return value;
    }

    public void setValue(GenericValue value) {
        this.value = value;
    }

    public String getDueType() {
        return dueType;
    }

    public void setDueType(String dueType) {
        this.dueType = dueType;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }
}
