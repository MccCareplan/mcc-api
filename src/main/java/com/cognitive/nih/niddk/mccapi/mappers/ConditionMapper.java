package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import org.hl7.fhir.r4.model.Condition;

public class ConditionMapper {

    public static MccCondition fhir2local(Condition in)
    {
        MccCondition out = new MccCondition();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setCode(CodeableConceptMapper.fhir2local(in.getCode()));
        out.setCategories(CodeableConceptMapper.fhir2local(in.getCategory()));
        out.setSeverity(CodeableConceptMapper.fhir2local(in.getSeverity()));
        out.setClinicalStatus(CodeableConceptMapper.fhir2local(in.getClinicalStatus()));
        return out;
    }
}
