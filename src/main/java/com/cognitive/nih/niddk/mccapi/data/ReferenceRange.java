package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccQuantity;
import com.cognitive.nih.niddk.mccapi.data.primative.MccRange;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class ReferenceRange {

    private MccQuantity low;
    private MccQuantity high;
    private MccCodeableConcept type;
    private MccCodeableConcept[] appliesTo;
    private MccRange age;
    private String text;
}
