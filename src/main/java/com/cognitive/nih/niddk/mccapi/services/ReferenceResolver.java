package com.cognitive.nih.niddk.mccapi.services;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedPerson;
@Slf4j
public class ReferenceResolver {



    public static Practitioner findPractitioner(Reference ref, Context ctx) {
        Practitioner out = null;
        if (Helper.isReferenceOfType(ref, "Practitioner")) {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Practitioner.class, ref.getReference());
            } catch (Exception e) {
                log.warn("Error retieving reference (" + ref.toString() + ")", e);
            }
        }
        return out;
    }

    public static RelatedPerson findRelatedPerson(Reference ref, Context ctx) {
        RelatedPerson out = null;
        if (Helper.isReferenceOfType(ref, "RelatedPerson")) {
            try {
                out = ctx.getClient().fetchResourceFromUrl(RelatedPerson.class, ref.getReference());
            } catch (Exception e) {
                log.warn("Error retieving reference (" + ref.toString() + ")", e);
            }
        }
        return out;
    }

    public static Organization findOrganization(Reference ref, Context ctx) {
        Organization out = null;
        String type = ref.getType();
        if (Helper.isReferenceOfType(ref, "Organization"))
        {
            try {
                out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref.getReference());
            } catch (Exception e) {
                log.warn("Error retieving reference (" + ref.toString() + ")", e);
            }
        }
        return out;
    }
    public static Organization findOrganization(String ref,Context ctx) {
        Organization out = null;
        try {
            out = ctx.getClient().fetchResourceFromUrl(Organization.class, ref);
        } catch (Exception e) {
            log.warn("Error retieving reference (" + ref.toString() + ")", e);
        }
        return out;
    }
}
