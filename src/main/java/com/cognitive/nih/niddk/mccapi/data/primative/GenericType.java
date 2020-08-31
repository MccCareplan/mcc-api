package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class GenericType {
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

    public MccSampledData getSampledDataValue() {
        return sampledDataValue;
    }

    public void setSampledDataValue(MccSampledData sampledDataValue) {
        this.sampledDataValue = sampledDataValue;
    }

    public MccDuration getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(MccDuration durationValue) {
        this.durationValue = durationValue;
    }

    public MccTiming getTimingValue() {
        return timingValue;
    }

    public void setTimingValue(MccTiming timingValue) {
        this.timingValue = timingValue;
    }

    public MccInstant getInstantValue() {
        return instantValue;
    }

    public void setInstantValue(MccInstant instantValue) {
        this.instantValue = instantValue;
    }

    public MccIdentifer getIdentiferValue() {
        return identiferValue;
    }

    public void setIdentiferValue(MccIdentifer identiferValue) {
        this.identiferValue = identiferValue;
    }
}
