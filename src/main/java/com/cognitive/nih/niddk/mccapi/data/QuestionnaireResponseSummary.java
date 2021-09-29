/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class QuestionnaireResponseSummary {
    @NotBlank
    private String FHIRId;
    @NotBlank
    private String status;
    private Date authored;
    @NotBlank
    private String questionnaire;
    private String author;
    private String source;
    private String subject;


    // Reviewer
    // Reason
    // Pertaines to Goal
}
