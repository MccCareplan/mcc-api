package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccDateTime {
    public static final String fhirType = "DateTime";

    private Date rawDate;
    private String date;


}
