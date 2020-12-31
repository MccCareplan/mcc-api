package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDate;
import com.cognitive.nih.niddk.mccapi.data.primative.MccDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class ReferralSummary {

    @NotBlank
    private MccCodeableConcept purpose;
    @NotBlank
    private String FHIRId;
    private GenericType date;
    @NotBlank
    private String displayDate;
    @NotBlank
    private String referrer;
    @NotBlank
    private String receiver;
    @NotBlank
    private String status;
    @NotBlank
    private MccCodeableConcept performerType;

}
