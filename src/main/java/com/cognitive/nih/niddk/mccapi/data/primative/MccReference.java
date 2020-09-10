package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccReference {
    public static final String fhirType = "Reference";
    private String reference;
    private String display;
    private String type;

}
