package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.MccType;
import org.hl7.fhir.r4.model.Base;

public class MccTypeMapper {


    public MccType fhir2local(Base in)
    {
        //TODO: Implement a generic mapper
        String typeName = in.getClass().getTypeName();
        return null;
    }
}
