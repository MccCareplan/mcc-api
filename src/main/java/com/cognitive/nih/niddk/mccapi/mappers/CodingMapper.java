package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import org.hl7.fhir.r4.model.Coding;

public class CodingMapper {
    public static MccCoding fhir2local(Coding in, Context ctx)
    {
        //TODO: Consider a cache
        MccCoding out = new MccCoding();
        out.setCode(in.getCode());
        out.setDisplay(in.getCode());
        out.setSystem(in.getSystem());
        out.setVersion(in.getVersion());
        return out;
    }
}
