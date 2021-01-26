package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@JsonInclude(value = JsonInclude.Include. NON_EMPTY)
public @Data class GenericType {
    public static final String fhirType = "Type";
    @NotBlank
    private String valueType;
    private String stringValue;
    private Integer integerValue;
    private Boolean booleanValue;
    private MccId idValue;
    private MccCodeableConcept codeableConceptValue;
    private MccQuantity quantityValue;
    private MccRange rangeValue;
    private MccRatio ratioValue;
    private MccPeriod periodValue;
    private MccDate dateValue;
    private MccTime timeValue;
    private MccDateTime dateTimeValue;
    private MccSampledData sampledDataValue;
    private MccDuration durationValue;
    private MccTiming timingValue;
    private MccInstant instantValue;
    private MccIdentifer identiferValue;
    private MccCoding codingValue;
    private BigDecimal decimalValue;
}
