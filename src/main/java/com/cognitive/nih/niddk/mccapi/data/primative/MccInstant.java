package com.cognitive.nih.niddk.mccapi.data.primative;

public class MccInstant {
    public static final String fhirType = "Instant";

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
