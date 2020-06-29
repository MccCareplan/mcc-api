package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import java.util.List;

public class CareplanMapper {
    private static String RACE_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
    private static String ENTHNICITY_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    private static String OMB_CATEGORY = "ombCategory";

    public static MccCarePlan fhir2local(CarePlan in, Context ctx) {
        MccCarePlan out = new MccCarePlan();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setId(in.hasIdentifier() ? in.getIdentifierFirstRep().getValue() : "Unknown");
        out.setTitle(in.getTitle());
        out.setDescription(in.getDescription());
        out.setNotes(AnnotationsToString(in.getNote()));
        out.setStatus(in.getStatus().getDisplay());
        out.setIntent(in.getIntent().getDisplay());
        if (out.getId() == null)
        {
            out.setId("FHIR("+out.getId()+")");
        }
        out.setDateResourceLastUpdated(Helper.dateToString(in.getMeta().getLastUpdated()));
        out.setPeriodStarts(Helper.dateToString(in.getPeriod().getStart()));
        out.setPeriodEnds(Helper.dateToString(in.getPeriod().getEnd()));

        String categories = Helper.getConceptsAsDisplayString(in.getCategory());
        if (categories == null || categories.isBlank())
        {
            categories = "Not provided";
        }
        out.setCategorySummary(categories);
        out.setCategories(CodeableConceptMapper.fhir2local(in.getCategory(),ctx));

        //Reference Elements Handled out side this code
        // Addresses
        // Author
        // CareTeam
        // Activity

        return out;
    }

    public static String AnnotationsToString(List<Annotation> annotations)
    {
        //TODO: Move to an annotation helper?

        StringBuffer out = new StringBuffer();
        boolean bAddLine = false;
        for(Annotation a: annotations)
        {
            if (bAddLine)
            {
                out.append("\n");
            }
            if (a.hasTime())
            {
                out.append(Helper.dateTimeToString(a.getTime()));
                out.append(" ");
            }
            if (a.hasAuthor())
            {
                if (a.hasAuthorStringType())
                {
                    out.append(a.getAuthorStringType().getValue());
                    out.append(" ");
                }
                else
                {
                    //We have an Author Reference
                    //TODO: Call a Reference resolver
                }
            }
            //TOOO: Deal with Markdown
            out.append(a.getText());
            bAddLine = true;
        }
        return out.toString();
    }

    public static String buildName(Patient in)
    {
        String name;
        HumanName bestName = null;
        //Locate the best name
        for (HumanName nm :in.getName())
        {
            if (nm.getUse() == HumanName.NameUse.OFFICIAL)
            {
                bestName = nm;
                break;
            }
        }
        if (bestName == null)
        {
            bestName = in.getNameFirstRep();
        }

        if (bestName.hasText())
        {
            name = bestName.getText();
        }
        else
        {
            name = bestName.getNameAsSingleString();
        }

        return name;
    }

}
