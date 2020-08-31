package com.cognitive.nih.niddk.mccapi.data.primative;

public class MccTime {
    public static final String fhirType = "Time";
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
