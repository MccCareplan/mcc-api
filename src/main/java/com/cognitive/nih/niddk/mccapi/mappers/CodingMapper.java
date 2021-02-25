package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CodingMapper implements ICodingMapper {

    public MccCoding fhir2local(Coding in, Context ctx)
    {
        //TODO: Consider a cache
        MccCoding out = new MccCoding();
        out.setCode(in.getCode());
        out.setDisplay(in.getDisplay());
        out.setSystem(in.getSystem());
        out.setVersion(in.getVersion());
        return out;
    }
}
