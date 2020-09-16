package com.cognitive.nih.niddk.mccapi.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class MedicationSummary {
    private String type;  //MedicationStatement or MedicationRequest
    private String fhirId;
    private Boolean inConflict;
    private String[] conflictsWith;
    private String status;
    private String categories;
    private String medication;
    private String dosages;
    private String requestedBy;
    private String reasons;
    private String issues;
    private String priority;
    private String onCareplans;
}
