package com.cognitive.nih.niddk.mccapi.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GoalLists {
    private ArrayList<GoalSummary> activeClinicalGoals = new ArrayList<>();
    private ArrayList<GoalSummary> inactiveClinicalGoals = new ArrayList<>();
    private ArrayList<GoalSummary> activePatientGoals = new ArrayList<>();
    private ArrayList<GoalSummary> inactivePatientGoals = new ArrayList<>();
    private ArrayList<GoalTarget> activeTargets = new ArrayList<>();
    private static HashMap<String,Integer> activeKeys = new HashMap<>();

    private static final int ACTIVE_LIST = 0;
    private static final int INACTIVE_LIST = 1;
    private static final int IGNORE = 2;


    private static Logger logger;


    static {

        logger = LoggerFactory.getLogger(GoalLists.class);

        //Lifecycle Status
        //  proposed | planned | accepted | active | on-hold | completed | cancelled | entered-in-error | rejected

        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer inactive = Integer.valueOf(INACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);

        //	active | recurrence | relapse
        activeKeys.put("proposed",active);
        activeKeys.put("planned",active);
        activeKeys.put("accepted",active);
        activeKeys.put("on-hold",active);
        activeKeys.put("completed",inactive);
        activeKeys.put("cancelled",inactive);
        activeKeys.put("rejected",active);
        activeKeys.put("active",active);
        activeKeys.put("entered-in-error",ignore);
    }

    public GoalLists()
    {
    }

    public void addSummary(GoalSummary goal)
    {

        Integer activeStatus = activeKeys.get(goal.getLifecycleStatus());
        if (activeStatus == null)
        {
            activeStatus = Integer.valueOf(IGNORE);
            logger.warn("Lifecycle status of {} is not known, ignoreing this goal",goal.getLifecycleStatus());
        }
        switch (activeStatus.intValue())
        {
            case ACTIVE_LIST:
            {
                if(goal.getExpressedByType().compareTo("Patient")==0)
                {
                    activePatientGoals.add(goal);
                }
                else
                {
                    activeClinicalGoals.add(goal);
                }
                addActiveTargets(goal);
                break;
            }
            case INACTIVE_LIST:
            {
                if(goal.getExpressedByType().compareTo("Patient")==0)
                {
                    inactivePatientGoals.add(goal);
                }
                else
                {
                    inactiveClinicalGoals.add(goal);
                }
                break;
            }
            default:
            {
                break;
            }

        }
    }


    private void addActiveTargets(GoalSummary g)
    {
        GoalTarget[] targets =g.getTargets();
       if (targets!= null)
       {
            activeTargets.addAll(Arrays.asList(targets));
       }
    }

    public GoalSummary[] getActiveClinicalGoals() {

        GoalSummary[] out = new GoalSummary[activeClinicalGoals.size()];
        return activeClinicalGoals.toArray(out);
    }
    public GoalSummary[] getInactiveClinicalGoals() {

        GoalSummary[] out = new GoalSummary[inactiveClinicalGoals.size()];
        return inactiveClinicalGoals.toArray(out);
    }
    public GoalSummary[] getActivePatientGoals() {

        GoalSummary[] out = new GoalSummary[activePatientGoals.size()];
        return activePatientGoals.toArray(out);
    }

    public GoalSummary[] getInactivePatientGoals() {

        GoalSummary[] out = new GoalSummary[inactivePatientGoals.size()];
        return inactivePatientGoals.toArray(out);
    }

    public GoalTarget[] getActiveTargets()
    {
        GoalTarget[] out = new GoalTarget[activeTargets.size()];
        return activeTargets.toArray(out);
    }
}
