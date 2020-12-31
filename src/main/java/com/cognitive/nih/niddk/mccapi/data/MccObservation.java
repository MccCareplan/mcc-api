package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

@Slf4j
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccObservation {
    @NotBlank
    private String FHIRId;
    @NotBlank
    private MccCodeableConcept code;
    @NotBlank
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
    public @Data class Effective implements Comparable
    {
        private String type;
        private MccDateTime dateTime;
        private MccPeriod period;
        private MccTiming timing;
        private MccInstant instant;

        @Override
        public int compareTo(Object o) {
            if (o instanceof Effective)
            {
                Effective in = (Effective) o;
                if (in.type.compareTo(this.type)==0)
                {
                    switch(type)
                    {
                        case MccDateTime.fhirType:
                        {
                            return this.dateTime.getRawDate().compareTo(in.getDateTime().getRawDate());
                        }
                        case MccPeriod.fhirType:
                        {
                            return this.period.compareTo(in.period);
                        }
                        case MccTiming.fhirType:
                        {
                            return this.timing.compareTo(in.timing);
                        }
                        case MccInstant.fhirType:
                        {
                            return this.instant.compareTo(in.instant);
                        }
                        default:
                        {
                            return 0;
                        }

                    }
                }
                else
                {
                    switch(type)
                    {
                        case MccPeriod.fhirType:
                        {
                            switch (in.type)
                            {
                                case MccDateTime.fhirType:
                                {
                                    return period.compareTo(in.dateTime);
                                }
                                default: {
                                    log.warn("Mismatch Observation Effective Comparison, "+type+" and "+in.type);
                                    return 0;
                                }
                            }
                        }
                        default:
                        {
                            log.warn("Mismatch Observation Effective Comparison, "+type+" and "+in.type);
                        }
                    }
                }
            }
            return 0;
        }

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
