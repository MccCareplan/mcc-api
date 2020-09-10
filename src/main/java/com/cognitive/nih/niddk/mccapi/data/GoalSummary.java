package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class GoalSummary {
    private String FHIRId;

    private String priority; //Extracted Code
    private String expressedByType;
    private String description;
    private MccCodeableConcept achievementStatus;
    private String lifecycleStatus;
    private String startDateText;
    private String targetDateText;
    private GoalTarget[] targets;
}
