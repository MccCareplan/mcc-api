package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccCarePlan implements MccType {
    private String title;
    private String dateLastRevised;
    private MccCondition[] addresses;
    private String addressesSummary;
    private String categorySummary;
    private MccCodeableConcept[] categories;
    private String id;
    @NotBlank
    private String FHIRId;
    private String periodStarts;
    private String periodEnds;
    @NotBlank
    private String status;
    @NotBlank
    private String intent;
    private String description;
    private String notes;
    private String dateResourceLastUpdated;

}
