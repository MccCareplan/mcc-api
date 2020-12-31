package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.util.FHIRCodeSets;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.*;

public class ConditionSummary {
    private MccCodeableConcept code;


    private HashSet<CodeableConcept> categories;
    private ArrayList<ConditionHistory> history;
    private String profileId;



    public ConditionSummary() {
        history = new ArrayList<>();
        categories = new HashSet<>();
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }

    public void finalizeHistory()
    {
        if (history.size()>0) {
            //Sort as required
            Collections.sort(history);
            //Grab the last entry and make it'd code the current one
            ConditionHistory f = history.get(history.size()-1);
            code = f.getCode();
        }

    }
    public ConditionHistory[] getHistory() {
        ConditionHistory[] out = new ConditionHistory[history.size()];
        return history.toArray(out);
    }

    public void setHistory(ConditionHistory[] history) {
        this.history = new ArrayList<ConditionHistory>(Arrays.asList(history));
    }

    public void addToHistory(ConditionHistory h) {
        history.add(h);
        mergeInCategories(h.getCategoriesList());
    }

    public String getClinicalStatus() {
        if (history.isEmpty()) {
            return "Missing";
        } else {
            ConditionHistory e = history.get(history.size() - 1);
            return e.getClinicalStatus();
        }
    }

    public String getVerificationStatus() {
        if (history.isEmpty()) {
            return "Missing";
        } else {
            ConditionHistory e = history.get(history.size() - 1);
            String out = e.getVerificationStatus();
            return out == null ? "Undefined" : out;
        }
    }

    public String getFirstOnset() {
        if (history.isEmpty()) {
            return "Missing";
        } else {
            ConditionHistory e = history.get(0);
            return e.getOnset();
        }
    }

    public void mergeInCategories(List<CodeableConcept> c) {
        if (c != null) {
            categories.addAll(c);
        }
    }

    public String getCategories() {
        CodeableConcept[] concepts = new CodeableConcept[categories.size()];
        concepts = categories.toArray(concepts);

        return Helper.getConceptCodes(concepts, FHIRCodeSets.US_CORE_CONDITION_CATEGORY_SET);
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
