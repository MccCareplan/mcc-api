package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccObservation {

    private String FHIRId;
    private MccCodeableConcept code;
    private String status;
    private MccReference[] basedOn;
    private Effective effective;
    private GenericType value;
    private String note;
    private ReferenceRange[] referenceRanges;
    private ObservationComponent[] components;
    private MccCodeableConcept[] category;
    private MccCodeableConcept dataAbsentReason;
    //bodySite
    //method
    //specimen
    //device
    //hasMember
    //derivedFrom

    //Based on ? Careplan


    public Effective defineEffective()
    {
        effective = new Effective();
        return effective;
    }

    @JsonInclude(JsonInclude.Include. NON_NULL)
    public @Data class Effective
    {
        private String type;
        private MccDateTime dateTime;
        private MccPeriod period;
        private MccTiming timing;
        private MccInstant instant;

        public void setDateTime(MccDateTime dateTime) {
            this.dateTime = dateTime;
            type= MccDateTime.fhirType;
        }


        public void setPeriod(MccPeriod period) {
            this.period = period;
            type = MccPeriod.fhirType;
        }


        public void setTiming(MccTiming timing) {
            this.timing = timing;
            type = MccTiming.fhirType;
        }

    }
}
