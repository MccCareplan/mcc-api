package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccCodeableConcept implements MccType {
    public static final String fhirType = "CodeableConcept";

    private MccCoding[] coding;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MccCoding[] getCoding() {
        return coding;
    }

    public void setCoding(MccCoding[] coding) {
        this.coding = coding;
    }


}
