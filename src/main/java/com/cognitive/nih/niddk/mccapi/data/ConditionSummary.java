/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.util.FHIRCodeSets;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;

import javax.validation.constraints.NotBlank;
import java.util.*;
@Slf4j
public class ConditionSummary {
    @NotBlank
    private MccCodeableConcept code;


    private HashSet<CodeableConcept> categories;
    @NotBlank
    private ArrayList<ConditionHistory> history;
    private String profileId;
    private Date firstRecorded;
    private String firstRecordedAsText;
    private ConditionHistory mostRecentValid = null;


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
        if (history.size()>1) {
            //Sort as required
            Collections.sort(history);

        }
        //Find first recorded
        for (ConditionHistory ch: history)
        {
            if (firstRecorded == null )
            {
                firstRecorded = ch.getRecorded();
                firstRecordedAsText = ch.getRecordedAsText();
            }
            else
            {
                Date d = ch.getRecorded();
                if (d!= null)
                {
                    if (d.compareTo(firstRecorded)<0)
                    {
                        firstRecorded = d;
                        firstRecordedAsText = ch.getRecordedAsText();
                    }
                }
            }
        }

        mostRecentValid = findMostRecentValid();
        if (mostRecentValid != null)
        {
            code = mostRecentValid.getCode();
        }
        else
        {
            //Grab the last entry and make it'd code the current one
            ConditionHistory f = history.get(history.size()-1);
            code = f.getCode();
            log.warn("Condition History with out a valid Recent Entry, using code "+code);

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
        if (mostRecentValid == null) {
            return "Missing";
        } else {
            return mostRecentValid.getClinicalStatus();
        }
    }

    public String getVerificationStatus() {
        if (mostRecentValid == null) {
            return "Missing";
        } else {
            String out = mostRecentValid.getVerificationStatus();
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

    public Date getFirstRecorded()
    {
       return firstRecorded;
    }

    public String getFirstRecordedAsText()
    {
        return firstRecordedAsText;
    }

    public void mergeInCategories(List<CodeableConcept> c) {
        if (c != null) {
            categories.addAll(c);
        }
    }

    public String getCategories() {
        CodeableConcept[] concepts = new CodeableConcept[categories.size()];
        concepts = categories.toArray(concepts);

        return FHIRHelper.getConceptCodes(concepts, FHIRCodeSets.US_CORE_CONDITION_CATEGORY_SET);
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    protected ConditionHistory findMostRecentValid()
    {

        //Valid entries must have a Clinical Status, Futhermore this status must no be missing | undefined
        //If a verification status is present it muist not be entered-in-error
        for (int i = history.size()-1; i>-1; i--)
        {
            ConditionHistory c = history.get(i);
            String cstat = c.getClinicalStatus();
            if (cstat != null) {
                //Filter weird clinical status
                if (cstat.compareTo("missing") == 0) continue;
                if (cstat.compareTo("undefined") == 0) continue;


                String vStat = c.getVerificationStatus();
                if (vStat != null)
                {
                    if (vStat.compareTo("entered-in-error")==0) continue;;
                }
                return c;
            }
        }
        return null;
    }
}
