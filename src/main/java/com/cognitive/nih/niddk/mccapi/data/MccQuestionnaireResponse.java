/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@JsonInclude(JsonInclude.Include. NON_NULL)

@Data
public class MccQuestionnaireResponse {
    @NotBlank
    private String id;
    @NotBlank
    private String FHIRId;
    private String status;

    private MccDate authored;

    private String questionnaire;
    private String author;
    private String source;
    private String subject;


    //We simplify the the structure of the FHIR QuestionnaireResponseItem and collapse all items into a single array with a path element
    private QuestionnaireResponseItem[] items;
}
