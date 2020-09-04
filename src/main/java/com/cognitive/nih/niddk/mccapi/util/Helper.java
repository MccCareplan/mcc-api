package com.cognitive.nih.niddk.mccapi.util;

import org.hl7.fhir.r4.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helper {
    private static String dateFormat = "MM/dd/yyyy";
    private static SimpleDateFormat fmtDate = new SimpleDateFormat(dateFormat);
    private static String dateTimeFormat = "MM/dd/yyyy hh:mm";
    private static SimpleDateFormat fmtDateTime = new SimpleDateFormat(dateFormat);

    public static Coding getCodingForSystem(CodeableConcept concept, String system) {
        Coding out = null;
        List<Coding> codings = concept.getCoding();
        for (Coding code : codings) {
            String cs = code.getSystem();
            if (cs != null) {
                if (cs.compareTo(system) == 0) {
                    out = code;
                    break;
                }
            }
        }
        return out;
    }

    public static String dateToString(Date d) {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone for UTC - fmtDate.setTimeZone(TimeZone.getTimeZone("UTC"))
        return fmtDate.format(d);
    }

    public static String dateTimeToString(Date d) {
        if (d == null) {
            return null;
        }
        //TODO: Deal with TimeZone
        return fmtDateTime.format(d);
    }


    public static String getCodingDisplayExtensionAsString(DomainResource r, String extURL, String field, String defValue) {
        Extension extb = r.getExtensionByUrl(extURL);
        if (extb != null) {

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
        } else {
            //TODO: Log as missing extension
            return defValue;
        }
    }

    public static String getConceptsAsDisplayString(List<CodeableConcept> concepts) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptDisplayString(c));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptsAsDisplayString(CodeableConcept[] concepts) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptDisplayString(c));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptDisplayString(CodeableConcept concept) {
        if (!concept.getText().isBlank()) {
            return concept.getText();
        }
        String out = null;
        //Search for any coding text
        for (Coding cd : concept.getCoding()) {
            if (cd.hasDisplay()) {
                out = cd.getDisplay();
                break;
            }
        }
        if (out == null) {
            //No coding text was found (grrr)
            //No we have a few choices -
            //  1) Grab and try to look up a description for each code
            //  2) Build a System, Code representtion
            //  3) return a default value
            //  4) return null
            //
            // For Diagnositic purposes we are going to
            Coding cd = concept.getCodingFirstRep();
            out = String.format("%s {%s}", cd.getCode(), cd.getSystem());
        }
        return out;
    }


    public static String getConceptCodes(CodeableConcept[] concepts, String system) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptCode(c, system));
            extra = true;
        }
        return out.toString();
    }

    public static String getConceptCodes(CodeableConcept[] concepts, Set<String> system) {
        StringBuffer out = new StringBuffer();
        boolean extra = false;
        for (CodeableConcept c : concepts) {
            if (extra) {
                out.append(",");
            }
            out.append(getConceptCode(c, system));
            extra = true;
        }
        return out.toString();
    }

    public static HashSet<String> getConceptSet(CodeableConcept[] concepts, String system) {
        HashSet<String> out = new HashSet<>();
        for (CodeableConcept c : concepts) {
            out.add(getConceptCode(c, system));
        }
        return out;
    }

    public static String getConceptCode(CodeableConcept concept, String system) {
        for (Coding cd : concept.getCoding()) {
            if (cd.getSystem().compareTo(system) == 0) {
                return cd.getCode();
            }
        }
        return null;
    }


    public static String getConceptCode(CodeableConcept concept, Set<String> system) {
        for (Coding cd : concept.getCoding()) {
            if (system.contains(cd.getSystem())) {
                return cd.getCode();
            }
        }
        return null;
    }


    public static String AnnotationsToString(List<Annotation> annotations) {

        StringBuffer out = new StringBuffer();
        boolean bAddLine = false;
        for (Annotation a : annotations) {
            if (bAddLine) {
                out.append("\n");
            }
            //TOOO: Deal with Markdown
            out.append(AnnotationToString(a));
            bAddLine = true;
        }
        return out.toString();
    }

    public static String AnnotationToString(Annotation a) {

        StringBuffer out = new StringBuffer();
        boolean bAddLine = false;
        if (a.hasTime()) {
            out.append(Helper.dateTimeToString(a.getTime()));
            out.append(" ");
        }
        if (a.hasAuthor()) {
            if (a.hasAuthorStringType()) {
                out.append(a.getAuthorStringType().getValue());
                out.append(" ");
            } else {
                //We have an Author Reference
                //TODO: Call a Reference resolver
            }
        }
        //TOOO: Deal with Markdown
        out.append(a.getText());
        return out.toString();
    }

    public static String[] AnnotationsToStringList(List<Annotation> annotations) {
        String[] out = new String[annotations.size()];
        int index = 0;
        for (Annotation a : annotations) {
            out[index] = AnnotationToString(a);
            index++;
        }
        return out;
    }

    public static String DurationToString(Duration duration)
    {
        if (duration.hasDisplay())
        {
            return duration.getDisplay();
        }
        StringBuffer out = new StringBuffer();

        if (duration.hasComparator())
        {
            out.append(duration.getComparator().getDisplay());
        }
        if (duration.hasValue())
        {
            out.append(duration.getValue().toPlainString());
        }
        if (duration.hasUnit()) {
            out.append(duration.getUnit());
        }
        return out.toString();
    }
}
