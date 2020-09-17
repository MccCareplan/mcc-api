package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/*
   This is a time based extension to quantity
 */
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccDuration extends MccQuantity {
    public static final String fhirType = "Duration";

}
