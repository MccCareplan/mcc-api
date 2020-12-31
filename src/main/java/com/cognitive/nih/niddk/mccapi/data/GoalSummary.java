package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class GoalSummary {
    @NotBlank
    private String FHIRId;
    @NotBlank
    private String priority; //Extracted Code
    private String expressedByType;
    @NotBlank
    private String description;
    private MccCodeableConcept achievementStatus;
    private String achievementText;
    @NotBlank
    private String lifecycleStatus;
    private String startDateText;
    private String targetDateText;
    private String addresses;
    private String expressedBy;
    private GoalTarget[] targets;
}
