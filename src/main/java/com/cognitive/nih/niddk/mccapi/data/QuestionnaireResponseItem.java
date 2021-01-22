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
    @NotBlank
    private String path;

    private String text;
    private GenericType[] answers;
}
