package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccGoal {
    @NotBlank
    private String id;
    @NotBlank
    private String FHIRId;

    private String statusDate;
    private String statusReason;
    @NotBlank
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

}
