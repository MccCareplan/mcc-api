package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class MccCodeableConcept implements MccType {
    public static final String fhirType = "CodeableConcept";

    private MccCoding[] coding;
    private String text;


}
