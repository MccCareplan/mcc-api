package com.cognitive.nih.niddk.mccapi.data;

import java.util.ArrayList;
import java.util.Arrays;

public class ConditionSummary {
    private MccCodeableConcept code;
    private ArrayList<ConditionHistory> history;

    public ConditionSummary()
    {
        history = new ArrayList<>();
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }

    public ConditionHistory[] getHistory() {
        ConditionHistory[] out = new ConditionHistory[history.size()];
        return history.toArray(out);
    }

    public void setHistory(ConditionHistory[] history) {
        this.history = new ArrayList<ConditionHistory>(Arrays.asList(history));
    }

    public void addToHistory(ConditionHistory h)
    {
        history.add(h);
    }

}
