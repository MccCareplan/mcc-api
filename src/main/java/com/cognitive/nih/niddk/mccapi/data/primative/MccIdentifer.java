package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccIdentifer {
    public static final String fhirType = "Identifier";

    private String use;
    private MccCodeableConcept type;
    private String system;
    private String value;
    private MccPeriod period;
    private MccReference assigner;

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public MccCodeableConcept getType() {
        return type;
    }

    public void setType(MccCodeableConcept type) {
        this.type = type;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MccPeriod getPeriod() {
        return period;
    }

    public void setPeriod(MccPeriod period) {
        this.period = period;
    }

    public MccReference getAssigner() {
        return assigner;
    }

    public void setAssigner(MccReference assigner) {
        this.assigner = assigner;
    }
}
