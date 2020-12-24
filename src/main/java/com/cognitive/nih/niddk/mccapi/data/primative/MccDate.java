package com.cognitive.nih.niddk.mccapi.data.primative;

import lombok.Data;

import java.util.Date;

public @Data
class MccDate implements Comparable
{
    public static final String fhirType = "Date";
    private Date rawDate;
    private String date;

    @Override
    public int compareTo(Object o) {
        if ( o instanceof  MccDate)
        {
            MccDate in = (MccDate) o;
            return this.rawDate.compareTo(in.rawDate);
        }
        if ( o instanceof Date)
        {
            return this.rawDate.compareTo((Date)o);
        }
        return 0;
    }
}
