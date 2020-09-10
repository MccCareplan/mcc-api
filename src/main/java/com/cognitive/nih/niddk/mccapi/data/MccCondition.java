package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccIdentifer;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccCondition {
    private String FHIRId;
    private MccCodeableConcept clinicalStatus;
    private MccCodeableConcept verifiationStatus;
    private MccCodeableConcept[] categories;
    private MccCodeableConcept severity;
    private MccCodeableConcept code;
    private String onset;
    private String abatement;
    private MccDate recordedDate;
    private MccReference recorder;
    private MccReference asserter;
    private String note;
    private String profileId;
    private MccIdentifer[] identifer;
    //TODO: Consider Identifier
    //TODO: Deal with stage
    //TODO: Deal with evidence;


}
