package com.cognitive.nih.niddk.mccapi.data;

import java.util.Date;

public class MccDate {
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
