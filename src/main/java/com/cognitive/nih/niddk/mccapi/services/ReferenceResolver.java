package com.cognitive.nih.niddk.mccapi.services;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;

@Slf4j
public class ReferenceResolver {


    //TODO Add Cache
    public static Practitioner findPractitioner(Reference ref, Context ctx) {
        Practitioner out = null;
        if (Helper.isReferenceOfType(ref, "Practitioner")) {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Practitioner.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static Patient findPatient(Reference ref, Context ctx) {
        Patient out = null;
        if (Helper.isReferenceOfType(ref, "Patient")) {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Patient.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static RelatedPerson findRelatedPerson(Reference ref, Context ctx) {
        RelatedPerson out = null;
        if (Helper.isReferenceOfType(ref, "RelatedPerson")) {
            try {
                out = ctx.getClient().fetchResourceFromUrl(RelatedPerson.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static MedicationRequest findMedicationRequest(Reference ref, Context ctx) {
        MedicationRequest out = null;
        if (Helper.isReferenceOfType(ref, "MedicationRequest"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(MedicationRequest.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static DetectedIssue findDetectedIssues(Reference ref, Context ctx) {
        DetectedIssue out = null;
        if (Helper.isReferenceOfType(ref, "DetectedIssue"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(DetectedIssue.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static Observation findObservation(Reference ref, Context ctx) {
        Observation out = null;
        if (Helper.isReferenceOfType(ref, "Observation"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Observation.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }


    public static PractitionerRole findPractitionerRole(Reference ref, Context ctx) {
        PractitionerRole out = null;
        if (Helper.isReferenceOfType(ref, "Condition"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl( PractitionerRole.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static RiskAssessment findRiskAssessment(Reference ref, Context ctx) {
        RiskAssessment out = null;
        if (Helper.isReferenceOfType(ref, "RiskAssessment"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(RiskAssessment.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static ServiceRequest findServiceRequest(Reference ref, Context ctx) {
        ServiceRequest out = null;
        if (Helper.isReferenceOfType(ref, "ServiceRequest"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(ServiceRequest.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static MedicationStatement findMedicationStatement(Reference ref, Context ctx) {
        MedicationStatement out = null;
        if (Helper.isReferenceOfType(ref, "MedicationStatement"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(MedicationStatement.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static NutritionOrder findNutritionOrder(Reference ref, Context ctx) {
        NutritionOrder out = null;
        if (Helper.isReferenceOfType(ref, "NutritionOrder"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(NutritionOrder.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }


    public static Condition findCondition(Reference ref, Context ctx) {
        Condition out = null;
        if (Helper.isReferenceOfType(ref, "Condition"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Condition.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    //TODO: Add to cache
    public static Medication findMedication(Reference ref, Context ctx) {
        Medication out = null;
        if (Helper.isReferenceOfType(ref, "Medication"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Medication.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }
    //TODO: Add cache
    public static Organization findOrganization(Reference ref, Context ctx) {
        Organization out = null;
        if (Helper.isReferenceOfType(ref, "Organization"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static Organization findOrganization(String ref, Context ctx) {
        Organization out = null;
        try {
            out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref);
            checkOutput(out,ref);
        } catch (Exception e) {
            logReferenceException(ref,e);
        }
        return out;
    }

    public static void checkOutput(Object o, String ref)
    {
        if (o == null)
        {
            log.warn("Missing reference (" + ref + ")");
        }

    }

    public static void logReferenceException (String ref, Exception e)
    {
        log.warn("Error retrieving reference (" + ref + ")", e);
    }
    public static void checkOutput(Object o, Reference ref)
    {
        if (o == null)
        {
            log.warn("Missing reference (" + ref.toString() + ")");
        }

    }

    public static void logReferenceException (Reference ref, Exception e)
    {
        log.warn("Error retrieving reference (" + ref.toString() + ")", e);
    }
}
