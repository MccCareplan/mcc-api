package com.cognitive.nih.niddk.mccapi.util;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper {
    private static String dateFormat="dd/MM/yyyy";
    private static SimpleDateFormat fmtDate = new SimpleDateFormat(dateFormat);
    private static String dateTimeFormat="dd/MM/yyyy hh:mm";
    private static SimpleDateFormat fmtDateTime = new SimpleDateFormat(dateFormat);

    public static String dateToString(Date d)
    {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone for UTC - fmtDate.setTimeZone(TimeZone.getTimeZone("UTC"))
        return fmtDate.format(d);
    }

    public static String dateTimeToString(Date d)
    {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone
        return fmtDateTime.format(d);
    }


    public static String getCodingDisplayExtensionAsString(DomainResource r, String extURL, String field, String defValue)
    {
        Extension extb = r.getExtensionByUrl(extURL);
        if (extb != null)
        {

            Extension ext = extb.getExtensionByUrl(field);
            if (ext != null) {
                Coding cd = ext.castToCoding(ext.getValue());
                if (cd.hasDisplay()) return cd.getDisplay();
                if (cd.hasCode()) {
                    //TODO: - Add code lookup and log conformance warning
                    return cd.getCode();
                }
                return defValue;
            }
            return defValue;
        }
        else
        {
            //TODO: Log as missing extension
            return defValue;
        }
    }

    public static String getConceptsAsDisplayString(List<CodeableConcept> concepts)
    {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for(CodeableConcept c: concepts)
        {
            if (extra)
            {
                out.append(",");
            }
            out.append(getConceptDisplayString(c));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptDisplayString(CodeableConcept concept)
    {
        if (!concept.getText().isBlank())
        {
            return concept.getText();
        }
        String out = null;
        //Search for any coding text
        for (Coding cd: concept.getCoding())
        {
            if (cd.hasDisplay())
            {
                out = cd.getDisplay();
                break;
            }
        }
        if (out == null)
        {
            //No coding text was found (grrr)
            //No we have a few choices -
            //  1) Grab and try to look up a description for each code
            //  2) Build a System, Code representtion
            //  3) return a default value
            //  4) return null
            //
            // For Diagnositic purposes we are going to
            Coding cd = concept.getCodingFirstRep();
            out = String.format("%s {%s}",cd.getCode(),cd.getSystem());
        }
        return out;
    }
}
