package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccDate implements Comparable
{
    public static final String fhirType = "date";
    @NotBlank
    private Date rawDate;
    @NotBlank
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
