package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccRange {
    public static final String fhirType = "Range";

    private MccQuantity high;
    private MccQuantity low;

    public MccQuantity getHigh() {
        return high;
    }

    public void setHigh(MccQuantity high) {
        this.high = high;
    }

    public MccQuantity getLow() {
        return low;
    }

    public void setLow(MccQuantity low) {
        this.low = low;
    }
}
