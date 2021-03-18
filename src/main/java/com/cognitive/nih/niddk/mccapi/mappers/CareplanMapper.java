package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlanSummary;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class CareplanMapper implements ICareplanMapper {

    private static String RACE_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
    private static String ENTHNICITY_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    private static String OMB_CATEGORY = "ombCategory";


    public MccCarePlan fhir2local(CarePlan in, Context ctx) {
        MccCarePlan out = new MccCarePlan();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setId(in.hasIdentifier() ? in.getIdentifierFirstRep().getValue() : "Unknown");
        out.setTitle(in.hasTitle()?in.getTitle():"Untitled Careplan");
        out.setDescription(in.hasDescription()?in.getDescription():"No desciption available");
        out.setNotes(FHIRHelper.annotationsToString(in.getNote(),ctx));
        out.setStatus(in.getStatus().getDisplay());
        out.setIntent(in.getIntent().getDisplay());
        if (out.getId() == null)
        {
            out.setId("FHIR("+out.getId()+")");
        }
        out.setDateResourceLastUpdated(FHIRHelper.dateToString(in.getMeta().getLastUpdated()));
        if (in.hasPeriod()) {
            out.setPeriodStarts(FHIRHelper.dateToString(in.getPeriod().getStart()));
            out.setPeriodEnds(FHIRHelper.dateToString(in.getPeriod().getEnd()));
        }

        String categories = FHIRHelper.getConceptsAsDisplayString(in.getCategory());
        if (categories == null || categories.isBlank())
        {
            categories = "Not provided";
        }
        out.setCategorySummary(categories);
        out.setCategories(ctx.getMapper().fhir2local(in.getCategory(),ctx));

        //Reference Elements Handled out side this code
        // Addresses
        // Author
        // CareTeam
        // Activity

        return out;
    }

    public MccCarePlanSummary fhir2Summary(CarePlan in, Set<String> profiles, Context ctx)
    {
        MccCarePlanSummary out = new MccCarePlanSummary();
        out.setProfiles(profiles);
        out.setFHIRId(in.getIdElement().getIdPart());
        if (in.hasCreated()) {
            out.setCreated(in.getCreated());
        }
        if (in.hasMeta() && in.getMeta().hasLastUpdated())
        {
            out.setLastUpdated(in.getMeta().getLastUpdated());
        }
        if (in.hasPeriod())
        {
            out.setPeriod(ctx.getMapper().fhir2local(in.getPeriod(),ctx));
        }
        return out;
    }

    public String buildName(Patient in)
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
