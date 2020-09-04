package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccTiming {

    public static final String fhirType = "Timing";

    private MccDateTime[] event;
    private MccCodeableConcept code;
    private Repeat repeat;


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
    public void setCode(MccCodeableConcept code) {
        this.code = code;
    }
    public Repeat defineRepeat()
    {
        repeat = new Repeat();
        return repeat;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public class Repeat
    {
        private Bounds bounds;
        private int count;
        private int countMax;
        private String duration;
        private String durationMax;
        private String durationUnit;
        private int frequency;
        private int frequencyMax;
        private String period;
        private String periodMax;
        private String periodUnit;
        private String[] dayOfWeek;
        private MccTime[] timeOfDay;
        private String[] when;
        private int offset;


        public Bounds getBounds() {
            return bounds;
        }

        public void setBounds(Bounds bounds) {
            this.bounds = bounds;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCountMax() {
            return countMax;
        }

        public void setCountMax(int countMax) {
            this.countMax = countMax;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDurationMax() {
            return durationMax;
        }

        public void setDurationMax(String durationMax) {
            this.durationMax = durationMax;
        }

        public String getDurationUnit() {
            return durationUnit;
        }

        /*
        	s | min | h | d | wk | mo | a - unit of time (UCUM)
         */
        public void setDurationUnit(String durationUnit) {
            this.durationUnit = durationUnit;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public int getFrequencyMax() {
            return frequencyMax;
        }

        public void setFrequencyMax(int frequencyMax) {
            this.frequencyMax = frequencyMax;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getPeriodMax() {
            return periodMax;
        }

        public void setPeriodMax(String periodMax) {
            this.periodMax = periodMax;
        }

        public String getPeriodUnit() {
            return periodUnit;
        }

        public void setPeriodUnit(String periodUnit) {
            this.periodUnit = periodUnit;
        }

        public String[] getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String[] dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public MccTime[] getTimeOfDay() {
            return timeOfDay;
        }

        public void setTimeOfDay(MccTime[] timeOfDay) {
            this.timeOfDay = timeOfDay;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }




        public Bounds defineBounds()
        {
            this.bounds = new Bounds();
            return bounds;
        }

        public String[] getWhen() {
            return when;
        }

        public void setWhen(String[] when) {
            this.when = when;
        }

        public class Bounds
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

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public MccPeriod getPeriod() {
                return period;
            }

            public void setPeriod(MccPeriod period) {
                this.period = period;
                this.type = "period";
            }

            public MccDuration getDuration() {
                return duration;
            }

            public void setDuration(MccDuration duration) {
                this.duration = duration;
                this.type = "duration";
            }
        }

    }

}
