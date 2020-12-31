package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.FuzzyDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.Comparator;
import java.util.List;

public class ConditionHistory implements Comparable<ConditionHistory> {
    private MccCodeableConcept code;
    private String onset;
    private String abatement;
    private String FHIRid;
    private String clinicalStatus;
    private String verificationStatus;
    private List<CodeableConcept> categories;

    private FuzzyDate onsetDate;
    private FuzzyDate abatementDate;

    @Override
    public int compareTo(ConditionHistory o) {
        if (onsetDate != null && o.onsetDate != null)
        {
            int r = onsetDate.compareTo(o.onsetDate);

            if (r == 0)
            {
                if (abatementDate != null && o.abatementDate != null)
                {
                    return abatementDate.compareTo(o.abatementDate);
                }
                if (abatementDate == null)
                {
                    return 1;
                }
                return -1;
            }
            return r;

        }
        if (onsetDate != null)
        {
            //The onset of the first is not known - sort it first
            return 1;
        }
        else
        {
            return -1;
        }

    }

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public String getAbatement() {
        return abatement;
    }

    public void setAbatement(String abatement) {
        this.abatement = abatement;
    }

    public String getFHIRid() {
        return FHIRid;
    }

    public void setFHIRid(String FHIRid) {
        this.FHIRid = FHIRid;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    @JsonIgnore
    public FuzzyDate getOnsetDate() {
        return onsetDate;
    }

    @JsonIgnore
    public void setOnsetDate(FuzzyDate onsetDate) {
        this.onsetDate = onsetDate;
    }

    @JsonIgnore
    public FuzzyDate getAbatementDate() {
        return abatementDate;
    }

    @JsonIgnore
    public void setAbatementDate(FuzzyDate abatementDate) {
        this.abatementDate = abatementDate;
    }

    @JsonIgnore
    public List<CodeableConcept> getCategoriesList() {
        return categories;
    }

    /*
    public CodeableConcept getCategory()
    {
        return categories.get(0);

    }
    */
    public String getCategories()
    {
        return Helper.getConceptsAsDisplayString(categories);
    }

    @JsonIgnore
    public void setCategoriesList(List<CodeableConcept> categories) {
        this.categories = categories;
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }
}
