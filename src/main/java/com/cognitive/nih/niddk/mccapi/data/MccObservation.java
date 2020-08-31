package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccObservation {

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

    public String getFHIRId() {
        return FHIRId;
    }

    public void setFHIRId(String FHIRId) {
        this.FHIRId = FHIRId;
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }

    public MccReference[] getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(MccReference[] basedOn) {
        this.basedOn = basedOn;
    }

    public Effective defineEffective()
    {
        effective = new Effective();
        return effective;
    }

    public GenericType getValue() {
        return value;
    }

    public void setValue(GenericType value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReferenceRange[] getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(ReferenceRange[] referenceRanges) {
        this.referenceRanges = referenceRanges;
    }

    public ObservationComponent[] getComponents() {
        return components;
    }

    public void setComponents(ObservationComponent[] components) {
        this.components = components;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MccCodeableConcept[] getCategory() {
        return category;
    }

    public void setCategory(MccCodeableConcept[] category) {
        this.category = category;
    }

    public MccCodeableConcept getDataAbsentReason() {
        return dataAbsentReason;
    }

    public void setDataAbsentReason(MccCodeableConcept dataAbsentReason) {
        this.dataAbsentReason = dataAbsentReason;
    }


    public class Effective
    {
        private String type;
        private MccDateTime dateTime;
        private MccPeriod period;
        private MccTiming timing;
        private MccInstant instant;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public MccDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(MccDateTime dateTime) {
            this.dateTime = dateTime;
            type= MccDateTime.fhirType;
        }

        public MccPeriod getPeriod() {
            return period;
        }

        public void setPeriod(MccPeriod period) {
            this.period = period;
            type = MccPeriod.fhirType;
        }

        public MccTiming getTiming() {
            return timing;
        }

        public void setTiming(MccTiming timing) {
            this.timing = timing;
            type = MccTiming.fhirType;
        }

        public MccInstant getInstant() {
            return instant;
        }

        public void setInstant(MccInstant instant) {
            this.instant = instant;
        }
    }
}
