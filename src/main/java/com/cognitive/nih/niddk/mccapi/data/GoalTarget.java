package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDuration;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class GoalTarget {
    private MccCodeableConcept measure;
    private GenericType value;

    private String dueType;
    private String due;
    private String dueAsText;
    private MccDuration dueDuration;
    private MccDate dueDate;

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
        this.dueType = "String";
    }


    public String getDueAsText() {
        return dueAsText;
    }

    public void setDueAsText(String dueAsText) {
        this.dueAsText = dueAsText;
    }

    public MccDuration getDueDuration() {
        return dueDuration;
    }

    public void setDueDuration(MccDuration dueDuration) {
        this.dueDuration = dueDuration;
        this.dueType = "Duration";
    }

    public MccDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(MccDate dueDate) {
        this.dueDate = dueDate;
        this.dueType = "Date";
    }
}
