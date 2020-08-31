package com.cognitive.nih.niddk.mccapi.data.primative;

public class MccRatio {
    public static final String fhirType = "Ratio";

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
