package com.cognitive.nih.niddk.mccapi.services;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ReferenceResolver {

    private static Cache<String, Practitioner> practitionerCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private static Cache<String, Organization> organizationCache= Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private static Cache<String, Questionnaire> questionnaireCache= Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private static Cache<String, Medication> medicationCache = Caffeine.newBuilder()
            .expireAfterWrite(8, TimeUnit.HOURS)
            .maximumSize(200)
            .build();

    public static Practitioner findPractitioner(Reference ref, Context ctx) {
        Practitioner out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Practitioner")) {
            String key = getReferenceKey(ref, ctx);
            out = practitionerCache.getIfPresent(key);
            if (out == null)
            {
                try {
                    //DIRECT-FHIR-REF
                    out = ctx.getClient().fetchResourceFromUrl(Practitioner.class, ref.getReference());
                    if (checkOutput(out, ref)) practitionerCache.put(key,out);
                } catch (Exception e) {
                    logReferenceException(ref, e);
                }
            }
        }
        return out;
    }

    public static Questionnaire findQuestionnaire(Reference ref, Context ctx) {
        Questionnaire out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Questionnaire")) {
            String key = getReferenceKey(ref, ctx);
            out = questionnaireCache.getIfPresent(key);
            if (out == null)
            {
                try {
                    //DIRECT-FHIR-REF
                    out = ctx.getClient().fetchResourceFromUrl(Questionnaire.class, ref.getReference());
                    if (checkOutput(out, ref)) questionnaireCache.put(key,out);
                } catch (Exception e) {
                    logReferenceException(ref, e);
                }
            }
        }
        return out;
    }

    public static CareTeam findCareTeam(Reference ref, Context ctx) {
        CareTeam out = null;
        if (FHIRHelper.isReferenceOfType(ref, "CareTeam")) {
                try {
                    //DIRECT-FHIR-REF
                    out = ctx.getClient().fetchResourceFromUrl(CareTeam.class, ref.getReference());
                 } catch (Exception e) {
                    logReferenceException(ref, e);
                }
        }
        return out;
    }

    public static Device findDevice(Reference ref, Context ctx) {
        Device out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Device")) {
            try {
                //DIRECT-FHIR-REF
                out = ctx.getClient().fetchResourceFromUrl(Device.class, ref.getReference());
            } catch (Exception e) {
                logReferenceException(ref, e);
            }
        }
        return out;
    }

    public static HealthcareService findHealthcareService(Reference ref, Context ctx) {
        HealthcareService out = null;
        if (FHIRHelper.isReferenceOfType(ref, "HealthcareService")) {
            try {
                //DIRECT-FHIR-REF
                out = ctx.getClient().fetchResourceFromUrl(HealthcareService.class, ref.getReference());
            } catch (Exception e) {
                logReferenceException(ref, e);
            }
        }
        return out;
    }


    public static Patient findPatient(Reference ref, Context ctx) {
        Patient out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Patient")) {
            try {
                log.info("PATIENT_ID_CHECK 2"+ref.getReference());
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "RelatedPerson")) {
            try {
                //DIRECT-FHIR-REF
                out = ctx.getClient().fetchResourceFromUrl(RelatedPerson.class, ref.getReference());
                if (checkOutput(out,ref))
                {

                }
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static MedicationRequest findMedicationRequest(Reference ref, Context ctx) {
        MedicationRequest out = null;
        if (FHIRHelper.isReferenceOfType(ref, "MedicationRequest"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "DetectedIssue"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "Observation"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "PractitionerRole"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "RiskAssessment"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "ServiceRequest"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "MedicationStatement"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "NutritionOrder"))
        {
            try {
                //DIRECT-FHIR-REF
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
        if (FHIRHelper.isReferenceOfType(ref, "Condition"))
        {
            try {
                //DIRECT-FHIR-REF
                out = ctx.getClient().fetchResourceFromUrl(Condition.class, ref.getReference());
                checkOutput(out,ref);
            } catch (Exception e) {
                logReferenceException(ref,e);
            }
        }
        return out;
    }

    public static Medication findMedication(Reference ref, Context ctx) {
        Medication out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Medication"))
        {
            String key = getReferenceKey(ref, ctx);
            out = medicationCache.getIfPresent(key);
            if (out == null) {
                try {
                    //DIRECT-FHIR-REF
                    out = ctx.getClient().fetchResourceFromUrl(Medication.class, ref.getReference());
                    if (checkOutput(out, ref)) medicationCache.put(key,out);
                } catch (Exception e) {
                    logReferenceException(ref, e);
                }
            }
        }
        return out;
    }

    public static Organization findOrganization(Reference ref, Context ctx) {
        Organization out = null;
        if (FHIRHelper.isReferenceOfType(ref, "Organization"))
        {
            String key = getReferenceKey(ref, ctx);
            out = organizationCache.getIfPresent(key);
            if (out == null) {
                try {
                    //DIRECT-FHIR-REF
                    out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref.getReference());
                    if (checkOutput(out, ref)) organizationCache.put(key,out);
                } catch (Exception e) {
                    logReferenceException(ref, e);
                }
            }
        }
        return out;
    }

    public static Organization findOrganization(String ref, Context ctx) {
        Organization out = null;
        String key = getReferenceKey(ref, ctx);
        out = organizationCache.getIfPresent(key);
        if (out == null)
        {
        try {
            //DIRECT-FHIR-REF
            out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref);
            if (checkOutput(out,ref)) organizationCache.put(key,out);
        } catch (Exception e) {
            logReferenceException(ref,e);
        }}
        return out;
    }

    public static boolean checkOutput(Object o, String ref)
    {
        if (o == null)
        {
            log.warn("Missing reference (" + ref + ")");
            return false;
        }
        return true;
    }

    public static void logReferenceException (String ref, Exception e)
    {
        log.warn("Error retrieving reference (" + ref + ")", e);
    }
    public static boolean checkOutput(Object o, Reference ref)
    {
        if (o == null)
        {
            log.warn("Missing reference (" + ref.toString() + ")");
            return false;
        }
        return true;
    }

    public static void logReferenceException (Reference ref, Exception e)
    {
        log.warn("Error retrieving reference (" + ref.toString() + ")", e);
    }

    public static String getReferenceKey(Reference ref, Context ctx)
    {
        StringBuilder key = new StringBuilder();
        key.append(ctx.getClient().getServerBase());
        key.append(":");
        key.append(ref.getReference());
        return key.toString();
    }

    public static String getReferenceKey(String ref, Context ctx)
    {
        StringBuilder key = new StringBuilder();
        key.append(ctx.getClient().getServerBase());
        key.append(":");
        key.append(ref);
        return key.toString();
    }

    public static void clearAllCaches()
    {
       practitionerCache.invalidateAll();
       practitionerCache.cleanUp();
       medicationCache.invalidateAll();
       medicationCache.cleanUp();
       organizationCache.invalidateAll();
       organizationCache.cleanUp();
       clearQuestionnaireCache();
    }

    public static void clearQuestionnaireCache()
    {
        questionnaireCache.invalidateAll();
        questionnaireCache.cleanUp();
    }
}
