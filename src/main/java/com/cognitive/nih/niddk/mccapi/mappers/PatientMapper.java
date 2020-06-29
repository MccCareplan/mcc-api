package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.*;

public class PatientMapper {
    private static String RACE_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
    private static String ENTHNICITY_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    private static String OMB_CATEGORY = "ombCategory";
    public static MccPatient fhir2local(Patient in,  Context ctx)
    {
        MccPatient out = new MccPatient();
        out.setDateOfBirth(Helper.dateToString(in.getBirthDate()));
        out.setGender(in.getGender().getDisplay());
        out.setName(buildName(in));
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setRace(Helper.getCodingDisplayExtensionAsString(in,RACE_KEY,OMB_CATEGORY,"Undefined"));
        out.setEthnicity(Helper.getCodingDisplayExtensionAsString(in,ENTHNICITY_KEY,OMB_CATEGORY,"Undefined"));
        out.setId(in.hasIdentifier()?in.getIdentifierFirstRep().getValue():"Unknown");
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
