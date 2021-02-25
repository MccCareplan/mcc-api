package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import org.hl7.fhir.r4.model.CodeableConcept;

import java.util.List;

public interface ICodeableConceptMapper {
    MccCodeableConcept fhir2local(CodeableConcept in, Context ctx);
    MccCodeableConcept[] fhir2local(List<CodeableConcept> in, Context ctx);
    MccCodeableConcept conceptFromCode(String code, String text);
}
