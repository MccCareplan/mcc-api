package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;

/**
 * Thsio class is a common holder the the recording of a medications - It is used by medication summary request to marry
 * MedicationRequests and MedicationStatements
 */
public class MccMedicationRecord {
    private String type;  //MedicationStatement or MedicationRequesti
    private String fhirId;
    private Boolean inConflict;
    private String[] conflictsWith;

    //FHIR
    private String status;
    private MccCodeableConcept statusReason;
    private MccCodeableConcept category;
    private MccCodeableConcept medication; //Will pull through references
    //effectiveDataTime
    //EffectivePeriod
    //dateAsserted
    private MccCodeableConcept reasonCode;
    private MccDosage dosage;  //Eq.
    private String note;

    private MccReference informationSource;

    //Request Only
    //status - request = 	active | on-hold | cancelled | completed | entered-in-error | stopped | draft | unknown
    //         statement =  active | completed | entered-in-error | intended | stopped | on-hold | unknown | not-taken
    //intent - We want order | original-order | reflex-order | filler-order | instance-order
    private String priority;
    //Supporting Information
    //authoredOn
    //requester
    //performer
    //performerType
    private MccReference reasonReference;
    private MccReference baseOn;
    //despenseRequest
    //substitution
    //priorPrescription
    //detectedIssue
    //eventHistory

}

