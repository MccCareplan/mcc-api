package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class MccTiming implements Comparable {

    public static final String fhirType = "Timing";

    private MccDateTime[] event;
    private MccCodeableConcept code;
    private Repeat repeat;
    private String readable;

    @Override
    public int compareTo(Object o) {
        if (o instanceof MccTiming)
        {
            MccTiming in = (MccTiming) o;

            //First we compare the events
            //return readable.compareTo(in.readable);
        }
        log.warn("Comparison between MccTiming and "+o.getClass().getName()+" not yet supported");
        return 0;
    }

    public MccDateTime[] getEvent() {
        return event;
    }

    public void setEvent(MccDateTime[] event) {
        this.event = event;
    }

    public MccCodeableConcept getCode() {
        return code;
    }

    /*
        Examaples 	BID | TID | QID | AM | PM | QD | QOD | +
     */

    public Repeat defineRepeat()
    {
        repeat = new Repeat();
        return repeat;
    }

    @JsonInclude(JsonInclude.Include. NON_NULL)
    public @Data class Repeat
    {
        private Bounds bounds;
        private int count;
        private int countMax;
        private String duration;
        private String durationMax;
        private String durationUnit; //    	s | min | h | d | wk | mo | a - unit of time (UCUM)
        private int frequency;
        private int frequencyMax;
        private String period;
        private String periodMax;
        private String periodUnit;
        private String[] dayOfWeek;
        private MccTime[] timeOfDay;
        private String[] when;
        private int offset;
        private String readable;



        public Bounds defineBounds()
        {
            this.bounds = new Bounds();
            return bounds;
        }

        @JsonInclude(JsonInclude.Include. NON_NULL)
        public @Data class Bounds
        {

            private String type;
            private MccRange range;
            private MccPeriod period;
            private MccDuration duration;


            public MccRange getRange() {
                return range;
            }

            public void setRange(MccRange range) {
                this.range = range;
                this.type = "range";
            }

            public void setPeriod(MccPeriod period) {
                this.period = period;
                this.type = "period";
            }


            public void setDuration(MccDuration duration) {
                this.duration = duration;
                this.type = "duration";
            }
        }

    }

}
