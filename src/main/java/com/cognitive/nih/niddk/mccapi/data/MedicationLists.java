package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.mappers.MedicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
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

        //Medication Request Status: 	active | on-hold | cancelled | completed | entered-in-error | not-taken | draft | unknown
        activeMedReqKeys.put("active",active);
        activeMedReqKeys.put("on-hold",inactive);
        activeMedReqKeys.put("cancelled",inactive);
        activeMedReqKeys.put("completed",inactive);
        activeMedReqKeys.put("entered-in-error",ignore);
        activeMedReqKeys.put("not-taken",inactive);
        activeMedReqKeys.put("unknown",inactive);

        //Medication Statement Status: 	active | completed | entered-in-error | intended | stopped | on-hold | unknown | not-taken
        activeMedStmtKeys.put("active",active);
        activeMedStmtKeys.put("completed",inactive);
        activeMedStmtKeys.put("entered-in-error",ignore);
        activeMedStmtKeys.put("intended",inactive);
        activeMedStmtKeys.put("stopped",inactive);
        activeMedStmtKeys.put("on-hold",inactive);
        activeMedStmtKeys.put("unknown",inactive);
        activeMedStmtKeys.put("not-taken",inactive);

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
        MccMedicationRecord mr = MedicationMapper.fhir2local(ms,ctx);
        String status = mr.getStatus();
        Integer s = activeMedStmtKeys.get(status);
        if (s != null) {
            int active = s.intValue();

            switch (active) {
                case ACTIVE_LIST: {
                    activeMedications.add(mr);
                }
                case INACTIVE_LIST: {
                    inactiveMedications.add(mr);
                }
                case IGNORE: {
                    log.debug("Ignoring status ");
                    break;
                }
                default: {
                    log.debug("Code error - Unhandled status swithc");
                }
            }
        }
        else
        {
            log.warn("Unknown Medication Status: "+status);
        }
    }

    public void addMedicationRequest(MedicationRequest mreq, Context ctx)
    {
        MccMedicationRecord mr = MedicationMapper.fhir2local(mreq,ctx);
        String status = mr.getStatus();
        Integer s = activeMedReqKeys.get(status);
        if (s != null) {
            int active = s.intValue();

            switch (active) {
                case ACTIVE_LIST: {
                    activeMedications.add(mr);
                }
                case INACTIVE_LIST: {
                    inactiveMedications.add(mr);
                }
                case IGNORE: {
                    log.debug("Ignoring status ");
                    break;
                }
                default: {
                    log.debug("Code error - Unhandled status swithc");
                }
            }
        }
        else
        {
            log.warn("Unknown Medication Status: "+status);
        }

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
