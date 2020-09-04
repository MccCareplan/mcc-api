package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccGoal {
    private String id;
    private String FHIRId;

    private String statusDate;
    private String statusReason;
    private String lifecycleStatus;
    private String categorySummary;
    private MccReference expressedBy; //Reference
    private MccCodeableConcept[] categories;
    private MccCodeableConcept priority;
    private MccCodeableConcept description;
    private boolean useStartConcept;
    private String startDateText;  //date or concept expressed as test
    private MccCodeableConcept startConcept;
    private MccDate startDate;
    private GoalTarget[] targets;
    private MccReference[] addresses;
    private String[] notes;
    private MccCodeableConcept[] outcomeCodes;
    private String outcomeReference;
    public MccGoal()
    {

    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getFHIRId() {
        return FHIRId;
    }

    public void setFHIRId(String FHIRId) {
        this.FHIRId = FHIRId;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public String getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        this.categorySummary = categorySummary;
    }

    public MccReference getExpressedBy() {
        return expressedBy;
    }

    public void setExpressedBy(MccReference expressedBy) {
        this.expressedBy = expressedBy;
    }



    public MccCodeableConcept[] getCategories() {
        return categories;
    }

    public void setCategories(MccCodeableConcept[] categories) {
        this.categories = categories;
    }

    public MccCodeableConcept getPriority() {
        return priority;
    }

    public void setPriority(MccCodeableConcept priority) {
        this.priority = priority;
    }

    public MccCodeableConcept getDescription() {
        return description;
    }

    public void setDescription(MccCodeableConcept description) {
        this.description = description;
    }

    public String getStartDateText() {
        return startDateText;
    }

    public void setStartDateText(String startDateText) {
        this.startDateText = startDateText;
    }

    public MccDate getStartDate() {
        return startDate;
    }

    public void setStartDate(MccDate startDate) {
        this.startDate = startDate;
    }

    public boolean isUseStartConcept() {
        return useStartConcept;
    }

    public void setUseStartConcept(boolean useStartConcept) {
        this.useStartConcept = useStartConcept;
    }

    public MccCodeableConcept getStartConcept() {
        return startConcept;
    }

    public void setStartConcept(MccCodeableConcept startConcept) {
        this.startConcept = startConcept;
    }

    public GoalTarget[] getTargets() {
        return targets;
    }

    public void setTargets(GoalTarget[] targets) {
        this.targets = targets;
    }

    public MccReference[] getAddresses() {
        return addresses;
    }

    public void setAddresses(MccReference[] addresses) {
        this.addresses = addresses;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    public MccCodeableConcept[] getOutcomeCodes() {
        return outcomeCodes;
    }

    public void setOutcomeCodes(MccCodeableConcept[] outcomeCodes) {
        this.outcomeCodes = outcomeCodes;
    }

    public String getOutcomeReference() {
        return outcomeReference;
    }

    public void setOutcomeReference(String outcomeReference) {
        this.outcomeReference = outcomeReference;
    }
}
