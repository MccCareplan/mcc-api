package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.MccCoding;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

public class CodeableConceptMapper {

    public static MccCodeableConcept fhir2local(CodeableConcept in)
    {
        MccCodeableConcept out = new MccCodeableConcept();
        out.setText(in.getText());
        MccCoding[] codes = new MccCoding[in.getCoding().size()];
        int i=0;
        for (Coding c: in.getCoding())
        {
            codes[i]=CodingMapper.fhir2local(c);
            i++;
        }
        out.setCoding(codes);
        return out;
    }

    public static MccCodeableConcept[] fhir2local(List<CodeableConcept> in)
    {
        MccCodeableConcept[] o = new MccCodeableConcept[in.size()];
        int i=0;
        for( CodeableConcept concept: in)
        {
            o[i]=fhir2local(concept);
            i++;
        }
        return o;
    }
}
