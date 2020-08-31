package com.cognitive.nih.niddk.mccapi.data;

public class MccRatio {
    private MccQuantity numerator;
    private MccQuantity denominator;

    public MccQuantity getNumerator() {
        return numerator;
    }

    public void setNumerator(MccQuantity numerator) {
        this.numerator = numerator;
    }

    public MccQuantity getDenominator() {
        return denominator;
    }

    public void setDenominator(MccQuantity denominator) {
        this.denominator = denominator;
    }
}
