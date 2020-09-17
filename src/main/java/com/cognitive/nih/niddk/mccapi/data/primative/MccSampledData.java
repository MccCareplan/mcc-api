package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccSampledData {

    public static final String fhirType = "SampledData";

    private MccSimpleQuantity origin;
    private BigDecimal period; //decimal
    private BigDecimal factor; //decimal
    private BigDecimal lowerlimit; //decimal
    private BigDecimal upperlimit; //decimal
    private int dimensions;
    private String data;

}
