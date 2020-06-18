package com.cognitive.nih.niddk.mccapi.matcher;

import com.cognitive.nih.niddk.mccapi.data.MccCoding;
import org.hl7.fhir.r4.model.Coding;

public class CodingMatcher {

    public static boolean matches(MccCoding a, MccCoding b)
    {
        if (a.getSystem().compareTo(b.getSystem())==0)
        {
            if (a.getCode().compareTo(b.getCode())==0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean matches(MccCoding a, Coding b)
    {
        if (a.getSystem().compareTo(b.getSystem())==0)
        {
            if (a.getCode().compareTo(b.getCode())==0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean matches(Coding a, Coding b)
    {
        if (a.getSystem().compareTo(b.getSystem())==0)
        {
            if (a.getCode().compareTo(b.getCode())==0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean matches(Coding a, MccCoding b)
    {
        return matches(b,a);
    }


}
