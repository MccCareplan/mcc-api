/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.matcher;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCoding;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.List;

public class CodeableConceptMatcher {

    public static boolean matches(MccCodeableConcept a, MccCodeableConcept b)
    {
        //Scan each coding in a and see if it matches any in b;
        MccCoding[] ac = a.getCoding();
        MccCoding[] bc = b.getCoding();
        for (int i=0; i<ac.length; i++)
        {
            for (int j=0; j< bc.length; j++)
            {
                if (CodingMatcher.matches(ac[i], bc[j]))
                {
                    return true;
                }
            }

        }
        return false;
    }
    public static boolean matches(MccCodeableConcept a, CodeableConcept b)
    {
        //Scan each coding in a and see if it matches any in b;
        MccCoding[] ac = a.getCoding();
        List<Coding> bc = b.getCoding();
        for (int i=0; i<ac.length; i++)
        {
            for (int j=0; j< bc.size(); j++)
            {
                if (CodingMatcher.matches(ac[i],bc.get(j)))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean matches(CodeableConcept a, CodeableConcept b)
    {
        //Scan each coding in a and see if it matches any in b;
        List<Coding> ac = a.getCoding();
        List<Coding> bc = b.getCoding();
        for (Coding acd: ac)
        {

            for (Coding bcd: bc)
            {
                if (CodingMatcher.matches(acd, bcd))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean matches(CodeableConcept a, MccCodeableConcept b)
    {
        return matches(b, a);
    }

}
