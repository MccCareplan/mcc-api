package com.cognitive.nih.niddk.mccapi.data;

public class MccCodeableConcept implements MccType {

    private MccCoding[] coding;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MccCoding[] getCoding() {
        return coding;
    }

    public void setCoding(MccCoding[] coding) {
        this.coding = coding;
    }


}
