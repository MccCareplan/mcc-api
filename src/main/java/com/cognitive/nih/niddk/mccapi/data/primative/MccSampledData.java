package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccSampledData {

    public static final String fhirType = "SampledData";

    private MccSimpleQuantity origin;
    private String period; //decimal
    private String factor; //decimal
    private String lowerlimit; //decimal
    private String upperlimit; //decimal
    private int dimensions;
    private String data;

    public MccSimpleQuantity getOrigin() {
        return origin;
    }

    public void setOrigin(MccSimpleQuantity origin) {
        this.origin = origin;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getLowerlimit() {
        return lowerlimit;
    }

    public void setLowerlimit(String lowerlimit) {
        this.lowerlimit = lowerlimit;
    }

    public String getUpperlimit() {
        return upperlimit;
    }

    public void setUpperlimit(String upperlimit) {
        this.upperlimit = upperlimit;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
