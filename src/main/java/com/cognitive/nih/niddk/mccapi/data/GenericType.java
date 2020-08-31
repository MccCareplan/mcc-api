package com.cognitive.nih.niddk.mccapi.data;

public class GenericType {
    private String valueType;  //Quantity, Range, CodeableConcept, String, Boolean, Integer, Ratio
    private String stringValue;
    private int integerValue;
    private boolean booleanValue;
    private MccCodeableConcept codeableConceptValue;
    private MccQuantity quantity;
    private MccRange range;
    private MccRatio ratio;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }


    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public MccCodeableConcept getCodeableConceptValue() {
        return codeableConceptValue;
    }

    public void setCodeableConceptValue(MccCodeableConcept codeableConceptValue) {
        this.codeableConceptValue = codeableConceptValue;
    }

    public MccQuantity getQuantity() {
        return quantity;
    }

    public void setQuantity(MccQuantity quantity) {
        this.quantity = quantity;
    }

    public MccRange getRange() {
        return range;
    }

    public void setRange(MccRange range) {
        this.range = range;
    }

    public MccRatio getRatio() {
        return ratio;
    }

    public void setRatio(MccRatio ratio) {
        this.ratio = ratio;
    }
}
