/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ReferenceMapper implements IReferenceMapper {

    public MccReference fhir2local(Reference in, Context ctx) {
        MccReference out  = new MccReference();
        if (in.hasReference()) {
            String ref = in.getReference();
            out.setReference(ref);
            String[] parts = ref.split("/");
            if (parts.length > 1) {
                out.setType(parts[parts.length - 2]);
            } else {
                out.setType("Undefined");
            }
        }
        if (in.hasDisplay()) {
            out.setDisplay(in.getDisplay());
        }
        return out;
    }

    public MccReference[] fhir2local(List<Reference> in, Context ctx)
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
