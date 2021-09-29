/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.util;

import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

public class MCC2FHIRHelper {

    public static boolean conceptInValueSet(CodeableConcept cc, MccValueSet valueSet)
    {
        if (cc == null)
            return false;

        List<Coding> codes = cc.getCoding();
        for (Coding coding: codes)
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
