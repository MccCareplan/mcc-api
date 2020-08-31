package com.cognitive.nih.niddk.mccapi.data;

public class MccRange {

    private MccQuantity high;
    private MccQuantity low;

    public MccQuantity getHigh() {
        return high;
    }

    public void setHigh(MccQuantity high) {
        this.high = high;
    }

    public MccQuantity getLow() {
        return low;
    }

    public void setLow(MccQuantity low) {
        this.low = low;
    }
}
