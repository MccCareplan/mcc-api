package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
   This is a time based extension to quantity
 */
@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccDuration extends MccQuantity {
    public static final String fhirType = "Duration";

}
