package com.cognitive.nih.niddk.mccapi.data;

public class MccCarePlan implements MccType {
    private String title;
    private String dateLastRevised;
    private MccCondition[] addresses;
    private String addressesSummary;
    private String categorySummary;
    private MccCodeableConcept[] categories;
    private String id;
    private String FHIRId;
    private String periodStarts;
    private String periodEnds;
    private String status;
    private String intent;
    private String description;
    private String notes;
    private String dateResourceLastUpdated;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDateLastRevised() {
        return dateLastRevised;
    }

    public void setDateLastRevised(String dateLastRevised) {
        this.dateLastRevised = dateLastRevised;
    }

    public MccCondition[] getAddresses() {
        return addresses;
    }

    public void setAddresses(MccCondition[] addresses) {
        this.addresses = addresses;
    }


    public String getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        this.categorySummary = categorySummary;
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

    public String getPeriodStarts() {
        return periodStarts;
    }

    public void setPeriodStarts(String periodStarts) {
        this.periodStarts = periodStarts;
    }

    public String getPeriodEnds() {
        return periodEnds;
    }

    public void setPeriodEnds(String periodEnds) {
        this.periodEnds = periodEnds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDateResourceLastUpdated() {
        return dateResourceLastUpdated;
    }

    public void setDateResourceLastUpdated(String dateResourceLastUpdated) {
        this.dateResourceLastUpdated = dateResourceLastUpdated;
    }

    public String getAddressesSummary() {
        return addressesSummary;
    }

    public void setAddressesSummary(String addressesSummary) {
        this.addressesSummary = addressesSummary;
    }

    public MccCodeableConcept[] getCategories() {
        return categories;
    }

    public void setCategories(MccCodeableConcept[] categories) {
        this.categories = categories;
    }
}
