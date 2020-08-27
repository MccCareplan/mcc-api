package com.cognitive.nih.niddk.mccapi.data;

public class MccReference {

    private String reference;
    private String display;
    private String type;

    public MccReference()
    {

    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
