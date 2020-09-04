package com.cognitive.nih.niddk.mccapi.data;

import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;

import java.util.ArrayList;
import java.util.HashMap;


public class MedicationLists {
    private ArrayList<MccMedicationRecord> activeMedications;
    private ArrayList<MccMedicationRecord> inactiveMedications;
    private HashMap<String, MccMedicationRecord> actMedConflictMap;

    private static final int ACTIVE_LIST = 0;
    private static final int INACTIVE_LIST = 1;
    private static final int IGNORE = 2;



    private static HashMap<String,Integer> activeMedReqKeys = new HashMap<>();
    private static HashMap<String,Integer> activeMedStmtKeys = new HashMap<>();

    static {

        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer inactive = Integer.valueOf(INACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);
        //	active | recurrence | relapse
        activeMedReqKeys.put("active:confirmed",active);
        activeMedReqKeys.put("active:provisional",active);
        activeMedReqKeys.put("active:missing",active);
        activeMedReqKeys.put("active:undefined",active);
        activeMedReqKeys.put("active:differential",active);
        activeMedReqKeys.put("active:unconfirmed",active);
        activeMedReqKeys.put("active:refuted",inactive);
        activeMedReqKeys.put("active:entered-in-error",ignore);

        activeMedReqKeys.put("recurrence:confirmed",active);
        activeMedReqKeys.put("recurrence:provisional",active);
        activeMedReqKeys.put("recurrence:missing",active);
        activeMedReqKeys.put("recurrence:undefined",active);
        activeMedReqKeys.put("recurrence:differential",active);
        activeMedReqKeys.put("recurrence:unconfirmed",active);
        activeMedReqKeys.put("recurrence:refuted",inactive);
        activeMedReqKeys.put("recurrence:entered-in-error",ignore);

        activeMedReqKeys.put("relapse:confirmed",active);
        activeMedReqKeys.put("relapse:provisional",active);
        activeMedReqKeys.put("relapse:missing",active);
        activeMedReqKeys.put("relapse:undefined",active);
        activeMedReqKeys.put("relapse:differential",active);
        activeMedReqKeys.put("relapse:unconfirmed",active);
        activeMedReqKeys.put("relapse:refuted",inactive);
        activeMedReqKeys.put("relapse:entered-in-error",ignore);

        // inactive | remission | resolved
        activeMedReqKeys.put("inactive:confirmed",inactive);
        activeMedReqKeys.put("inactive:provisional",inactive);
        activeMedReqKeys.put("inactive:missing",inactive);
        activeMedReqKeys.put("inactive:undefined",inactive);
        activeMedReqKeys.put("inactive:diffe77rential",inactive);
        activeMedReqKeys.put("inactive:unconfirmed",inactive);
        activeMedReqKeys.put("inactive:refuted",inactive);
        activeMedReqKeys.put("inactive:entered-in-error",ignore);

        activeMedReqKeys.put("remission:confirmed",inactive);
        activeMedReqKeys.put("remission:provisional",inactive);
        activeMedReqKeys.put("remission:missing",inactive);
        activeMedReqKeys.put("remission:undefined",inactive);
        activeMedReqKeys.put("remission:differential",inactive);
        activeMedReqKeys.put("remission:unconfirmed",inactive);
        activeMedReqKeys.put("remission:refuted",inactive);
        activeMedReqKeys.put("remission:entered-in-error",ignore);

        activeMedReqKeys.put("resolved:confirmed",inactive);
        activeMedReqKeys.put("resolved:provisional",inactive);
        activeMedReqKeys.put("resolved:missing",inactive);
        activeMedReqKeys.put("resolved:undefined",inactive);
        activeMedReqKeys.put("resolved:differential",inactive);
        activeMedReqKeys.put("resolved:unconfirmed",inactive);
        activeMedReqKeys.put("resolved:refuted",inactive);
        activeMedReqKeys.put("resolved:entered-in-error",ignore);
    }

    public MedicationLists()
    {
        activeMedications = new ArrayList<>();
        inactiveMedications = new ArrayList<>();
        //
        //medications = new HashMap<>();
    }

    public MccMedicationRecord[] getActiveMedications() {

        MccMedicationRecord[] out = new MccMedicationRecord[activeMedications.size()];
        return activeMedications.toArray(out);
    }


    public MccMedicationRecord[] getInactiveMedications() {

        MccMedicationRecord[] out = new MccMedicationRecord[inactiveMedications.size()];
        return inactiveMedications.toArray(out);
    }

    public void addMedicationStatement(MedicationStatement ms, Context ctx)
    {

    }

    public void addMedicationRequest(MedicationRequest mr, Context ctx)
    {
    }

    private boolean duplicateAndConflictCheck(MccMedicationRecord mr)
    {
        // Returns true id the recorded should be considered a duplicate.
        // When a conflict is detected it will return false, but the conflicting resources will be updated


        // Get the code of medication
        // If not null
        //    Check if we have seen this code before?
        //       if yes - Does the dosage match?
        //          No - Then
        return false;
    }


}
