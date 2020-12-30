package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class CounselingSummary {

    private MccCodeableConcept topic;
    private String type;
    private String FHIRId;
    private String displayDate;
    private GenericType date;
    private MccCodeableConcept outcome;
    private String status;

    //Type  - Procedure  - Service Request (E
    //FHIRId
    //status
    //category
    //performer
    //
}
