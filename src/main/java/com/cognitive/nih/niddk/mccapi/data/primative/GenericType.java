package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class GenericType {
    public static final String fhirType = "Type";
    private String valueType;
    private String stringValue;
    private int integerValue;
    private boolean booleanValue;
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


    public static GenericType fromString(String in)
    {
        GenericType out = new GenericType();
        out.setStringValue(in);
        out.setValueType("String");
        return out;
    }
}
