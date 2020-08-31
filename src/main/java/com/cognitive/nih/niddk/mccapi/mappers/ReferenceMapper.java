package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;

public class ReferenceMapper {

    public static MccReference fhir2local(Reference in, Context ctx) {
        MccReference out  = new MccReference();
        String ref = in.getReference();
        out.setReference(ref);
        String[] parts = ref.split("/");
        if (parts.length>1)
        {
            out.setType(parts[parts.length-2]);
        }
        else
        {
            out.setType("Undefined");
        }
        out.setDisplay(in.getDisplay());
        return out;
    }

    public static MccReference[] fhir2local(List<Reference> in, Context ctx)
    {
        MccReference[] o = new MccReference[in.size()];
        int i=0;
        for( Reference concept: in)
        {
            o[i]=fhir2local(concept, ctx);
            i++;
        }
        return o;
    }
}
