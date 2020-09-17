package com.cognitive.nih.niddk.mccapi.data.primative;

import lombok.Data;

import java.util.Date;

public @Data
class MccDate {
    public static final String fhirType = "Date";
    private Date rawDate;
    private String date;

}
