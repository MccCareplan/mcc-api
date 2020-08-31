package com.cognitive.nih.niddk.mccapi.data;

public class MccPeriod {
    private MccDate start;
    private MccDate end;

    public MccDate getStart() {
        return start;
    }

    public void setStart(MccDate start) {
        this.start = start;
    }

    public MccDate getEnd() {
        return end;
    }

    public void setEnd(MccDate end) {
        this.end = end;
    }
}
