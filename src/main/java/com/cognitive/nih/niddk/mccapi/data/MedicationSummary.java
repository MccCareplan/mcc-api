/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class MedicationSummary {
    @NotBlank
    private String type;  //MedicationStatement or MedicationRequest
    @NotBlank
    private String fhirId;
    private Boolean inConflict;
    private String[] conflictsWith;
    @NotBlank
    private String status;
    private String categories;
    @NotBlank
    private String medication;
    private String dosages;
    private String requestedBy;
    private String reasons;
    private String issues;
    private String priority;
    private String onCareplans;
}
