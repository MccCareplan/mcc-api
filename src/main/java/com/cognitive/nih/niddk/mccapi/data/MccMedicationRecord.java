/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Thsio class is a common holder the the recording of a medications - It is used by medication summary request to marry
 * MedicationRequests and MedicationStatements
 */
@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class MccMedicationRecord {
    @NotBlank
    private String type;  //MedicationStatement or MedicationRequest
    @NotBlank
    private String fhirId;
    private Boolean inConflict;
    private String[] conflictsWith;

    //FHIR
    @NotBlank
    private String status;
    private MccCodeableConcept[] statusReasons;
    private MccCodeableConcept[] categories;
    private MccCodeableConcept medication; //Will pull through references
    //private MccDateTime effectiveDataTime;
    //private MccPeriod effectivePeriod;
    //dateAsserted
    private MccCodeableConcept[] reasons;
    private MccDosage[] dosages;  //Eq.
    private String note;

    private MccReference informationSource;   //Statement Only

    //Request Only
    //status - request = 	active | on-hold | cancelled | completed | entered-in-error | stopped | draft | unknown
    //         statement =  active | completed | entered-in-error | intended | stopped | on-hold | unknown | not-taken
    //intent - We want order | original-order | reflex-order | filler-order | instance-order
    private String priority;  //Request Only
    //Supporting Information
    //authoredOn
    //requester (requester)
    private MccReference requester;
    //performer
    //performerType
    private MccReference[] reasonReferences;
    //private MccReference baseOn;
    //despenseRequest
    //substitution
    //priorPrescription
    private MccReference[] detectedIssues;
    private String onCareplans;
    //eventHistory

}

