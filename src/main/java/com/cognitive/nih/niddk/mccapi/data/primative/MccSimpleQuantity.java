/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccSimpleQuantity {

    public static final String fhirType = "SimpleQuantity";

    private String unit;
    private BigDecimal value;
    private String system;
    private String code;
    private String display;

}
