package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.ArrayList;
import java.util.List;

public class CodeableConceptMapper {

    public static MccCodeableConcept fhir2local(CodeableConcept in, Context ctx)
    {
        MccCodeableConcept out = new MccCodeableConcept();
        MccCoding[] codes = new MccCoding[in.getCoding().size()];
        String text = null;

        if (in.hasText()) {
            text = in.getText();
        }
        int i=0;
        for (Coding c: in.getCoding())
        {
            codes[i]=CodingMapper.fhir2local(c, ctx);
            if (text == null)
            {
                if (c.hasDisplay())
                {
                    text = c.getDisplay();
                }
            }
            i++;
        }
        if (text == null)
        {
            Coding firstCode = in.getCodingFirstRep();
            if (firstCode != null)
            {
                text = "Undescribed code "+firstCode.getCode();
            }
            else
            {
                text = "Concept with no Code";
            }
        }
        out.setCoding(codes);
        out.setText(text);
        return out;
    }

    public static MccCodeableConcept[] fhir2local(List<CodeableConcept> in, Context ctx)
    {
        MccCodeableConcept[] o = new MccCodeableConcept[in.size()];
        int i=0;
        for( CodeableConcept concept: in)
        {
            o[i]=fhir2local(concept, ctx);
            i++;
        }
        return o;
    }

    public static MccCodeableConcept conceptFromCode(String code, String text)
    {
        MccCodeableConcept out = new MccCodeableConcept();
        out.setText(text);
        MccCoding[] codes = new MccCoding[1];
        MccCoding coding = new MccCoding();
        if (code.contains("|"))
        {
            //We have a system
            String[] parts = code.split("|");
            if (parts.length>1)
            {
                coding.setSystem(parts[0]);
                coding.setCode(parts[1]);
            }
            else
            {
                coding.setCode(code);
            }
        }
        else
        {
            coding.setCode(code);
        }
        coding.setDisplay(text);
        codes[0]=coding;
        out.setCoding(codes);
        return out;
    }
}
