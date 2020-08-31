package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class ObservationComponent {
    private MccCodeableConcept code;
    private GenericType value;
    private MccCodeableConcept[] interpretation;
    private MccCodeableConcept dataAbsentReason;
    private ReferenceRange[] referenceRanges;

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }

    public GenericType getValue() {
        return value;
    }

    public void setValue(GenericType value) {
        this.value = value;
    }

    public MccCodeableConcept[] getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(MccCodeableConcept[] interpretation) {
        this.interpretation = interpretation;
    }

    public MccCodeableConcept getDataAbsentReason() {
        return dataAbsentReason;
    }

    public void setDataAbsentReason(MccCodeableConcept dataAbsentReason) {
        this.dataAbsentReason = dataAbsentReason;
    }

    public ReferenceRange[] getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(ReferenceRange[] referenceRanges) {
        this.referenceRanges = referenceRanges;
    }
}
