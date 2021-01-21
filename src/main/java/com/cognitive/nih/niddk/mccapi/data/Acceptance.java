package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


// http://hl7.org/fhir/StructureDefinition/goal-acceptance

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class Acceptance {
    private MccReference individual;
    private String code;
    private MccCodeableConcept priority;
}


