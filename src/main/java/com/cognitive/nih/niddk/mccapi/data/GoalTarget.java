package com.cognitive.nih.niddk.mccapi.data;

public class GoalTarget {
    private MccCodeableConcept measure;
    private GenericType value;

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

    public GenericType getValue() {
        return value;
    }

    public void setValue(GenericType value) {
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
