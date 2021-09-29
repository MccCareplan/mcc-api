/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.util;

import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

public class MccHelper {

    public static MccCodeableConcept conceptFromCode(String code, String text)
    {
        MccCodeableConcept out = new MccCodeableConcept();
        out.setText(text);
        MccCoding[] codes = new MccCoding[1];
        MccCoding coding = new MccCoding();
        if (code.contains("|"))
        {
            //We have a system
            String[] parts = code.split("\\|");
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

    public static GenericType genericFromString(String in)
    {
        GenericType out = new GenericType();
        out.setValueType("string");
        out.setStringValue(in);
        return out;
    }

    public static boolean conceptInValueSet(MccCodeableConcept cc, MccValueSet valueSet)
    {
        if (cc == null)
            return false;

        MccCoding[] codes = cc.getCoding();
        for (MccCoding coding: codes)
        {
            String cd = coding.getCode();
            String system = coding.getSystem();
            if (valueSet.hasCode(system,cd))
            {
                return true;
            }
        }
        return false;
    }
}
