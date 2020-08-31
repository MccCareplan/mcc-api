package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccRange;
import com.cognitive.nih.niddk.mccapi.data.primative.MccSimpleQuantity;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class ReferenceRange {

    private MccSimpleQuantity low;
    private MccSimpleQuantity high;
    private MccCodeableConcept type;
    private MccCodeableConcept[] appliesTo;
    private MccRange age;
    private String text;

    public MccSimpleQuantity getLow() {
        return low;
    }

    public void setLow(MccSimpleQuantity low) {
        this.low = low;
    }

    public MccSimpleQuantity getHigh() {
        return high;
    }

    public void setHigh(MccSimpleQuantity high) {
        this.high = high;
    }

    public MccCodeableConcept getType() {
        return type;
    }

    public void setType(MccCodeableConcept type) {
        this.type = type;
    }

    public MccCodeableConcept[] getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(MccCodeableConcept[] appliesTo) {
        this.appliesTo = appliesTo;
    }

    public MccRange getAge() {
        return age;
    }

    public void setAge(MccRange age) {
        this.age = age;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
