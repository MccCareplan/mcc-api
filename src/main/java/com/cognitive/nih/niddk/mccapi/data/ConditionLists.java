package com.cognitive.nih.niddk.mccapi.data;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;

public class ConditionLists {
    private ConditionSummary[] activeConditions;
    private ConditionSummary[] inactiveConditions;
    ArrayList<ConditionSummary> conditions;

    public ConditionLists()
    {
        conditions = new ArrayList<>();
    }

    public ConditionSummary[] getActiveConditions() {
        return activeConditions;
    }

    public void setActiveConditions(ConditionSummary[] activeConditions) {
        this.activeConditions = activeConditions;
    }

    public ConditionSummary[] getInactiveConditions() {
        return inactiveConditions;
    }

    public void setInactiveConditions(ConditionSummary[] inactiveConditions) {
        this.inactiveConditions = inactiveConditions;
    }

    public void addCondtion(Condition c)
    {
        ConditionSummary priorSummary = findCondtionIfAlreadySeen(c);
        //Check
    }

    public ConditionSummary indCondtionIfAlreadySeen(Condition c)
    {
        ConditionSummary out = null;
        //Find the code of the condition

        return out;
    }

    public void fillActiveAndInactiveLists()
    {

    }
}
