package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccPeriod {

    public static final String fhirType = "Period";

    private MccDate start;
    private MccDate end;

    public MccDate getStart() {
        return start;
    }

    public void setStart(MccDate start) {
        this.start = start;
    }

    public MccDate getEnd() {
        return end;
    }

    public void setEnd(MccDate end) {
        this.end = end;
    }
}
