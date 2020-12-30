package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class ReferralSummary {


    private MccCodeableConcept purpose;
    private String FHIRId;
    private GenericType date;
    private String displayDate;
    private String referrer;
    private String receiver;
    private String status;
    private MccCodeableConcept performerType;

}
