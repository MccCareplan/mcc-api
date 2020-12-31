package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccDateTime {
    public static final String fhirType = "dateTime";
    @NotBlank
    private Date rawDate;
    @NotBlank
    private String date;


}
