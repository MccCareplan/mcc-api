package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

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
        out.setNotes(Helper.annotationsToString(in.getNote(),ctx));
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
