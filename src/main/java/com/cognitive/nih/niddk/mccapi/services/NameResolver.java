package com.cognitive.nih.niddk.mccapi.services;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.NonNull;
import org.hl7.fhir.r4.model.*;

import java.util.List;

public class NameResolver {

    public static String getReferenceName(Reference ref, Context ctx)
    {

        if (ref.hasDisplay())
        {
            return ref.getDisplay();
        }
        String out =null;
        String type = Helper.getReferenceType(ref);

        if (type.compareTo("Practitioner")==0)
        {
            Practitioner x = ReferenceResolver.findPractitioner(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("Organization")==0)
        {
            Organization x = ReferenceResolver.findOrganization(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("Medication")==0)
        {
            Medication x = ReferenceResolver.findMedication(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;

        }
        else if (type.compareTo("PractitionerRole")==0)
        {
            PractitionerRole x = ReferenceResolver.findPractitionerRole(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("Condition")==0)
        {
            PractitionerRole x = ReferenceResolver.findPractitionerRole(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("MedicationStatement ")==0)
        {
            MedicationStatement x = ReferenceResolver.findMedicationStatement(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("ServiceRequest")==0)
        {
            ServiceRequest x = ReferenceResolver.findServiceRequest(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("RiskAssessment")==0)
        {
            RiskAssessment x = ReferenceResolver.findRiskAssessment(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("NutritionOrder")==0)
        {
            NutritionOrder x = ReferenceResolver.findNutritionOrder(ref, ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }
        else if (type.compareTo("Patient")==0)
        {
            Patient x = ReferenceResolver.findPatient(ref,ctx);
            out = x!= null ? getName(x,ctx) : "Missing "+type;
        }

        if (out == null)
        {
            out = type + " Not Yet Supported";
        }

        return out;
    }


    public static String getReferenceNames(List<Reference> refs, Context ctx)
    {
        StringBuffer out = new StringBuffer();
        for (Reference ref: refs)
        {
            Helper.addStringToBufferWithSep(out,getReferenceName(ref,ctx),",");
        }
        return out.toString();
    }

    public static String getName(@NonNull Practitioner p, Context ctx)
    {
        return Helper.getBestName(p.getName());
    }
    public static String getName(@NonNull Patient p, Context ctx)
    {
        return Helper.getBestName(p.getName());
    }
    public static String getName(@NonNull Medication x, Context ctx)
    {
        return x.getCode().getText();
    }

    public static String getName(@NonNull Organization o, Context ctx)
    {
        return o.getName();
    }

    public static String getName(@NonNull RelatedPerson o, Context ctx)
    {
        return Helper.getBestName(o.getName());
    }

    public static String getName(@NonNull PractitionerRole o,Context ctx)
    {
        return o.getCodeFirstRep().getText();
    }

    public static String getName(@NonNull NutritionOrder o, Context ctx) {
        if (o.hasIdentifier())
        {
            return "NutritionOrder "+o.getIdentifierFirstRep().getValue();
        }
        else
        {
            return "NutritionOrder, "+o.getIdElement().toString();
        }
    }

    public static String getName(@NonNull ServiceRequest s, Context ctx)
    {
        StringBuffer out = new StringBuffer();
        out.append(s.getIntent().getDisplay());
        if (s.hasCode())
        {
            out.append(" for ");
            out.append(Helper.getConceptDisplayString(s.getCode()));
        }
        else
        {
            out.append(" ");
            if (s.hasIdentifier())
            {
                return "RiskAssessment "+s.getIdentifierFirstRep().getValue();
            }
            else
            {
                return "RiskAssessment, "+s.getIdElement().toString();
            }
        }
        return out.toString();
    }

    public static String getName(@NonNull RiskAssessment r, Context ctx)
    {
        if (r.hasCode())
        {
            return Helper.getConceptDisplayString(r.getCode());
        }
        if (r.hasCondition())
        {
            return getReferenceName(r.getCondition(),ctx);
        }
        if (r.hasIdentifier())
        {
            return "RiskAssessment "+r.getIdentifierFirstRep().getValue();
        }
        else
        {
            return "RiskAssessment, "+r.getIdElement().toString();
        }
    }

    public static String getName(@NonNull MedicationStatement in, Context ctx)
    {
        String out = "Unresolvable Medication Statement";
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out = in.getMedicationCodeableConcept().getText();
            }
            if (in.hasMedicationReference()) {
                out = getReferenceName(in.getMedicationReference(),ctx);
            }
        }
        return out;
    }
}
