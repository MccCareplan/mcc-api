package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.managers.ProfileManager;
import com.cognitive.nih.niddk.mccapi.mappers.CodeableConceptMapper;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.matcher.CodeableConceptMatcher;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;

import java.util.ArrayList;
import java.util.HashMap;


public class ConditionLists {
    private ArrayList<ConditionSummary> activeConditions;
    private ArrayList<ConditionSummary> inactiveConditions;
    private ArrayList<ConditionSummary> activeConcerns;
    private ArrayList<ConditionSummary> inactiveConcerns;
    private ArrayList<ConditionSummary> conditions;

    private static final int ACTIVE_LIST = 0;
    private static final int INACTIVE_LIST = 1;
    private static final int IGNORE = 2;

    private static HashMap<String,Integer> activeKeys = new HashMap<>();

    static {
        // Clinical Status
        //	active | recurrence | relapse vs.  inactive | remission | resolved  + missing | undefined
        //
        // Verification Status
        //  unconfirmed | provisional | differential | confirmed | refuted | entered-in-error + missing | undefined
        //
        // Category in handle in logic problem-list-item | encounter-diagnosis | health-concern

        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer inactive = Integer.valueOf(INACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);
        //	active | recurrence | relapse
        activeKeys.put("active:confirmed",active);
        activeKeys.put("active:provisional",active);
        activeKeys.put("active:missing",active);
        activeKeys.put("active:undefined",active);
        activeKeys.put("active:differential",active);
        activeKeys.put("active:unconfirmed",active);
        activeKeys.put("active:refuted",inactive);
        activeKeys.put("active:entered-in-error",ignore);

        activeKeys.put("recurrence:confirmed",active);
        activeKeys.put("recurrence:provisional",active);
        activeKeys.put("recurrence:missing",active);
        activeKeys.put("recurrence:undefined",active);
        activeKeys.put("recurrence:differential",active);
        activeKeys.put("recurrence:unconfirmed",active);
        activeKeys.put("recurrence:refuted",inactive);
        activeKeys.put("recurrence:entered-in-error",ignore);

        activeKeys.put("relapse:confirmed",active);
        activeKeys.put("relapse:provisional",active);
        activeKeys.put("relapse:missing",active);
        activeKeys.put("relapse:undefined",active);
        activeKeys.put("relapse:differential",active);
        activeKeys.put("relapse:unconfirmed",active);
        activeKeys.put("relapse:refuted",inactive);
        activeKeys.put("relapse:entered-in-error",ignore);

        // inactive | remission | resolved
        activeKeys.put("inactive:confirmed",inactive);
        activeKeys.put("inactive:provisional",inactive);
        activeKeys.put("inactive:missing",inactive);
        activeKeys.put("inactive:undefined",inactive);
        activeKeys.put("inactive:differential",inactive);
        activeKeys.put("inactive:unconfirmed",inactive);
        activeKeys.put("inactive:refuted",inactive);
        activeKeys.put("inactive:entered-in-error",ignore);

        activeKeys.put("remission:confirmed",inactive);
        activeKeys.put("remission:provisional",inactive);
        activeKeys.put("remission:missing",inactive);
        activeKeys.put("remission:undefined",inactive);
        activeKeys.put("remission:differential",inactive);
        activeKeys.put("remission:unconfirmed",inactive);
        activeKeys.put("remission:refuted",inactive);
        activeKeys.put("remission:entered-in-error",ignore);

        activeKeys.put("resolved:confirmed",inactive);
        activeKeys.put("resolved:provisional",inactive);
        activeKeys.put("resolved:missing",inactive);
        activeKeys.put("resolved:undefined",inactive);
        activeKeys.put("resolved:differential",inactive);
        activeKeys.put("resolved:unconfirmed",inactive);
        activeKeys.put("resolved:refuted",inactive);
        activeKeys.put("resolved:entered-in-error",ignore);
    }

    public ConditionLists()
    {
        activeConditions = new ArrayList<>();
        inactiveConditions = new ArrayList<>();
        activeConcerns = new ArrayList<>();
        inactiveConcerns = new ArrayList<>();
        conditions = new ArrayList<>();
    }

    public ConditionSummary[] getActiveConditions() {

        ConditionSummary[] out = new ConditionSummary[activeConditions.size()];
        return activeConditions.toArray(out);
    }


    public ConditionSummary[] getInactiveConditions() {

        ConditionSummary[] out = new ConditionSummary[inactiveConditions.size()];
        return inactiveConditions.toArray(out);
    }

    public ConditionSummary[] getActiveConcerns() {

        ConditionSummary[] out = new ConditionSummary[activeConcerns.size()];
        return activeConcerns.toArray(out);
    }


    public ConditionSummary[] getInactiveConcerns() {

        ConditionSummary[] out = new ConditionSummary[inactiveConcerns.size()];
        return inactiveConcerns.toArray(out);
    }



    public void addCondition(Condition c, Context ctx)
    {
        // We need to logic for groups by profile/common translations - This is esp. true of staged conditions
        // As a general rue we want group all condition that have the same base concept into the common concept.
        // The most current representative of the concept should be the displayed concept

        
        ConditionSummary summary = findConditionIfAlreadySeen(c);
        if (summary == null)
        {
            summary = new ConditionSummary();
            summary.setCode(CodeableConceptMapper.fhir2local(c.getCode(), ctx));
            summary.setProfileId(ProfileManager.getProfileManager().getProfilesForConcept(c.getCode()));
            conditions.add(summary);
        }

        ConditionHistory history = ConditionMapper.fhir2History(c, ctx);
        summary.addToHistory(history);
    }

    public ConditionSummary findConditionIfAlreadySeen(Condition c)
    {
        //Find the code of the condition
        CodeableConcept cc = c.getCode();
        for (ConditionSummary csum: conditions)
        {
            if (CodeableConceptMatcher.matches(csum.getCode(),cc))
            {
                return csum;
            }
        }
        return null;
    }

    public void categorizeConditions()
    {

        for (ConditionSummary c: this.conditions)
        {
            StringBuilder buf = new StringBuilder();
            buf.append(c.getClinicalStatus());
            buf.append(":");
            buf.append(c.getVerificationStatus());
            String activeKey = buf.toString();

            Integer activeStatus = activeKeys.get(activeKey);

            // problem-list-item | encounter-diagnosis | health-concern
            String categories = c.getCategories();
            boolean isProblemOrEncounter = categories.contains("problem-list-item") || categories.contains("encounter-diagnosis");
            boolean isHealthConcern = categories.contains("health-concern");

            if (activeStatus != null)
            {
                switch (activeStatus.intValue())
                {
                    case ACTIVE_LIST:
                    {
                        if (isHealthConcern)
                        {
                            this.activeConcerns.add(c);
                        }

                        if (isProblemOrEncounter)
                        {
                            this.activeConditions.add(c);
                        }
                        break;
                    }
                    case INACTIVE_LIST:
                    {
                        if (isProblemOrEncounter) {
                            this.inactiveConditions.add(c);
                        }
                        if (isHealthConcern)
                        {
                            this.inactiveConcerns.add(c);
                        }
                        break;
                    }
                    case IGNORE:
                    {
                        //Maybe log an debug message
                        break;
                    }
                    default:
                    {
                        //TODO:  log an warning message
                        break;

                    }
                }
            }
        }
    }
}
