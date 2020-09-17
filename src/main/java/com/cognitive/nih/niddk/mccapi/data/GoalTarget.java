package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDuration;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class GoalTarget {
    private MccCodeableConcept measure;
    private GenericType value;

    private String dueType;
    private String due;
    private String dueAsText;
    private MccDuration dueDuration;
    private MccDate dueDate;


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

    public void setDueDuration(MccDuration dueDuration) {
        this.dueDuration = dueDuration;
        this.dueType = "Duration";
    }


    public void setDueDate(MccDate dueDate) {
        this.dueDate = dueDate;
        this.dueType = "Date";
    }
}
