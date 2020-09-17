package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccQuantity {
    public static final String fhirType = "Quantity";

    private String unit;
    private String comparator;
    private BigDecimal value;
    private String system;
    private String code;
    private String display;

}
