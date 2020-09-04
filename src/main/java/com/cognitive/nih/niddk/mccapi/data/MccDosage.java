package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.*;

public class MccDosage {
    private int sequence;
    private String text;
    private String patientInstructions;
    private MccCodeableConcept[] additionInstructions;
    private MccTiming timing;
    private boolean asNeededBoolean;
    private MccCodeableConcept asNeededCodableConcept;
    private MccCodeableConcept site;
    private MccCodeableConcept route;
    private MccCodeableConcept method;
    private DoseAndRate[] doseAndRate;
    private MccRatio maxDosePerPeriod;
    private MccSimpleQuantity maxDosePerAdministration;
    private MccSimpleQuantity maxDosePerLifetime;

    public DoseAndRate[] createDoseAndRateArray(int size)
    {
        doseAndRate = new DoseAndRate[size];
        for (int i = 0; i< size; i++)
        {
            doseAndRate[i] = new DoseAndRate();
        }
        return doseAndRate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPatientInstructions() {
        return patientInstructions;
    }

    public void setPatientInstructions(String patientInstructions) {
        this.patientInstructions = patientInstructions;
    }

    public MccCodeableConcept[] getAdditionInstructions() {
        return additionInstructions;
    }

    public void setAdditionInstructions(MccCodeableConcept[] additionInstructions) {
        this.additionInstructions = additionInstructions;
    }

    public MccTiming getTiming() {
        return timing;
    }

    public void setTiming(MccTiming timing) {
        this.timing = timing;
    }

    public boolean isAsNeededBoolean() {
        return asNeededBoolean;
    }

    public void setAsNeededBoolean(boolean asNeededBoolean) {
        this.asNeededBoolean = asNeededBoolean;
    }

    public MccCodeableConcept getAsNeededCodableConcept() {
        return asNeededCodableConcept;
    }

    public void setAsNeededCodableConcept(MccCodeableConcept asNeededCodableConcept) {
        this.asNeededCodableConcept = asNeededCodableConcept;
    }

    public MccCodeableConcept getSite() {
        return site;
    }

    public void setSite(MccCodeableConcept site) {
        this.site = site;
    }

    public MccCodeableConcept getRoute() {
        return route;
    }

    public void setRoute(MccCodeableConcept route) {
        this.route = route;
    }

    public MccCodeableConcept getMethod() {
        return method;
    }

    public void setMethod(MccCodeableConcept method) {
        this.method = method;
    }

    public DoseAndRate[] getDoseAndRate() {
        return doseAndRate;
    }

    public void setDoseAndRate(DoseAndRate[] doseAndRate) {
        this.doseAndRate = doseAndRate;
    }

    public MccRatio getMaxDosePerPeriod() {
        return maxDosePerPeriod;
    }

    public void setMaxDosePerPeriod(MccRatio maxDosePerPeriod) {
        this.maxDosePerPeriod = maxDosePerPeriod;
    }

    public MccSimpleQuantity getMaxDosePerAdministration() {
        return maxDosePerAdministration;
    }

    public void setMaxDosePerAdministration(MccSimpleQuantity maxDosePerAdministration) {
        this.maxDosePerAdministration = maxDosePerAdministration;
    }

    public MccSimpleQuantity getMaxDosePerLifetime() {
        return maxDosePerLifetime;
    }

    public void setMaxDosePerLifetime(MccSimpleQuantity maxDosePerLifetime) {
        this.maxDosePerLifetime = maxDosePerLifetime;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public class DoseAndRate
    {
        private MccCodeableConcept type;
        private MccRange doseRange;
        private MccQuantity doseQuantity;
        private MccRatio rateRatio;
        private MccRange rateRange;
        private MccSimpleQuantity rateQuantity;

        public MccCodeableConcept getType() {
            return type;
        }

        public void setType(MccCodeableConcept type) {
            this.type = type;
        }

        public MccRange getDoseRange() {
            return doseRange;
        }

        public void setDoseRange(MccRange doseRange) {
            this.doseRange = doseRange;
        }

        public MccQuantity getDoseQuantity() {
            return doseQuantity;
        }

        public void setDoseQuantity(MccQuantity doseQuantity) {
            this.doseQuantity = doseQuantity;
        }

        public MccRatio getRateRatio() {
            return rateRatio;
        }

        public void setRateRatio(MccRatio rateRatio) {
            this.rateRatio = rateRatio;
        }

        public MccRange getRateRange() {
            return rateRange;
        }

        public void setRateRange(MccRange rateRange) {
            this.rateRange = rateRange;
        }

        public MccSimpleQuantity getRateQuantity() {
            return rateQuantity;
        }

        public void setRateQuantity(MccSimpleQuantity rateQuantity) {
            this.rateQuantity = rateQuantity;
        }
    }


}
