package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;

public interface IReferenceMapper {
    MccReference fhir2local(Reference in, Context ctx);
    MccReference[] fhir2local(List<Reference> in, Context ctx);
}
