/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class QuestionnaireResponseItem {
    @NotBlank
    private String linkid;
    private String text;
    private QuestionnaireResponseItemAnswer[] answers;
    private QuestionnaireResponseItem[] items;

}
