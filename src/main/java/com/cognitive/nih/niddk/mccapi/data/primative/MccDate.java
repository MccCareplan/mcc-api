package com.cognitive.nih.niddk.mccapi.data.primative;

import java.util.Date;

public class MccDate {
    public static final String fhirType = "Date";
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
