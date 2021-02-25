package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import org.hl7.fhir.r4.model.Coding;

public interface ICodingMapper {
    MccCoding fhir2local(Coding in, Context ctx);
}
