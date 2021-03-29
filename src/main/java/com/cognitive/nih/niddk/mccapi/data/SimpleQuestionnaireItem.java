package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class SimpleQuestionnaireItem {
    private String FHIRId;
    private String type="QuestionnaireResponse";
    private MccDate authored;
    private QuestionnaireResponseItem item;
}
