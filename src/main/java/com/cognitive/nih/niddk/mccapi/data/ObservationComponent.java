/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class ObservationComponent {
    @NotBlank
    private MccCodeableConcept code;
    @NotBlank
    private GenericType value;
    private MccCodeableConcept[] interpretation;
    private MccCodeableConcept dataAbsentReason;
    private ReferenceRange[] referenceRanges;
}
