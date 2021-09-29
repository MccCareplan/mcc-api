/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class MccIdentifer {
    public static final String fhirType = "Identifier";

    private String use;
    private MccCodeableConcept type;
    private String system;
    private String value;
    private MccPeriod period;
    private MccReference assigner;

}
