package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class Counseling {


    private MccCodeableConcept topic;
    private String type;
    private String FHIRId;
    private MccDate date;
    private MccCodeableConcept outcome;
    private String status;

}
