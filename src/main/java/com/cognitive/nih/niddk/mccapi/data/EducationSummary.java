package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class EducationSummary {
    @NotBlank
    private MccCodeableConcept topic;
    @NotBlank
    private String type;
    @NotBlank
    private String FHIRId;
    private String displayDate;
    private GenericType date;
    private MccCodeableConcept outcome;
    @NotBlank
    private String status;

    //Type  - Procedure  - Service Request (E
    //FHIRId
    //status
    //category
    //performer
    //
}
