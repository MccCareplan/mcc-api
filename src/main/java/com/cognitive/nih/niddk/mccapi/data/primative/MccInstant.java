package com.cognitive.nih.niddk.mccapi.data.primative;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.InstantType;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Slf4j
public @Data
class MccInstant implements Comparable {
    public static final String fhirType = "Instant";

    private String value;
    private InstantType $instant;

    @Override
    public int compareTo(Object o) {
        if (o instanceof MccInstant)
        {
            return compareTo((MccInstant) o);
        }
        if (o instanceof Date)
        {
            return $instant.getValue().compareTo((Date)o);
        }
        if (o instanceof InstantType)
        {
            return $instant.getValue().compareTo(((InstantType) o).getValue());
        }
        return 0;
    }

    public int compareTo(MccInstant in)
    {
        return $instant.getValue().compareTo($instant.getValue());
    }

    public void setValue(String v)
    {
        value = v;
        $instant = new InstantType(value);
    }

    public Date toDate()
    {
        if (value == null)
            return null;
        //Let HAPI handle it
        return $instant.getValue();
    }
}
