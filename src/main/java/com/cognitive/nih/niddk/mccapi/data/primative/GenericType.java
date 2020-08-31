package com.cognitive.nih.niddk.mccapi.data;

public class GenericType {
    private String valueType;  //Quantity, Range, CodeableConcept, String, Boolean, Integer, Ratio
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

//.... effectiveTiming			Timing
//..... valueSampledData			SampledData

//
//.... effectiveInstant Instanc

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

    public MccQuantity getQuantityValue() {
        return quantityValue;
    }

    public void setQuantityValue(MccQuantity quantityValue) {
        this.quantityValue = quantityValue;
    }

    public MccRange getRangeValue() {
        return rangeValue;
    }

    public void setRangeValue(MccRange rangeValue) {
        this.rangeValue = rangeValue;
    }

    public MccRatio getRatioValue() {
        return ratioValue;
    }

    public void setRatioValue(MccRatio ratioValue) {
        this.ratioValue = ratioValue;
    }

    public MccPeriod getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(MccPeriod periodValue) {
        this.periodValue = periodValue;
    }

    public MccDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(MccDate dateValue) {
        this.dateValue = dateValue;
    }

    public MccTime getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(MccTime timeValue) {
        this.timeValue = timeValue;
    }

    public MccDateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(MccDateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }
}
