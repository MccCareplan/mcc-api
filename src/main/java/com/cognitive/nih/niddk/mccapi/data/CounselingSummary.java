package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.FuzzyDate;
import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class CounselingSummary {
    @NotBlank
    private MccCodeableConcept topic;
    @NotBlank
    private String type;
    @NotBlank
    private String FHIRId;
    private String displayDate;
    private FuzzyDate date;
    private MccCodeableConcept outcome;
    @NotBlank
    private String status;
    private String performer;
    private String reasons;
    //Type  - Procedure  - Service Request (E
    //FHIRId
    //status
    //category
    //performer
    //
}
