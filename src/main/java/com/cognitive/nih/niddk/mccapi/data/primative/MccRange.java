package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccRange {
    public static final String fhirType = "Range";

    private MccQuantity high;
    private MccQuantity low;
}
