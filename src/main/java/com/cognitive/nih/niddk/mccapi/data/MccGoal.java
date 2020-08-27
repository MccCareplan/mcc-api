package com.cognitive.nih.niddk.mccapi.data;

public class Goal {
    private String id;
    private String FHIRId;

    private String title;
    private String statusDate;
    private String statusReason;
    private String lifecycleStatus;
    private String address;
    private String categorySummary;
    private MccCodeableConcept[] categories;
    private String priority;
    private String description;
    private String startText;  //date or concept
    private boolean useStartConcept;
    private MccCodeableConcept[] startConcept;
    //Add private Date startDate
    private String periodEnds;
    private String status;
    private String intent;
    private String notes;
    private String dateResourceLastUpdated;
}
