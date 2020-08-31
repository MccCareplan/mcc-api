package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
@JsonInclude(JsonInclude.Include. NON_NULL)
public class MccDateTime {
    public static final String fhirType = "DateTime";

    private Date rawDate;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getRawDate() {
        return rawDate;
    }

    public void setRawDate(Date rawDate) {
        this.rawDate = rawDate;
    }
}
