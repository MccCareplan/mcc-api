package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.cognitive.nih.niddk.mccapi.util.JavaHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class PractitionerMapper implements IPractitionerMapper {

    public Contact fhir2Contact(Practitioner in, Context ctx) {
        Contact out = new Contact();
        out.setName(FHIRHelper.getBestName(in.getName()));
        out.setRelFhirId(FHIRHelper.getIdString(in.getIdElement()));

        Address a = FHIRHelper.findBestAddress(in.getAddress(), "work");
        if (a != null) {
            out.setAddress(FHIRHelper.addressToString(a));
        }
        //in.getContact();   //What to d this this in the future
        if (in.hasPhoto())
        {
            out.setHasImage(true);
        }
        //Deal with contact points
        List<ContactPoint> contactPoints = FHIRHelper.filterToCurrentContactPoints(in.getTelecom());
        Map<String, List<ContactPoint>> cpBySystem = FHIRHelper.organizeContactTypesBySystem(contactPoints);
        ContactPoint bestPhone = FHIRHelper.findBestContactByType(cpBySystem, "phone", "work|mobile|home");
        if (bestPhone != null) {
            out.setPhone(bestPhone.getValue());
        }
        ContactPoint bestEmail = FHIRHelper.findBestContactByType(cpBySystem, "email", "work|mobile");
        if (bestEmail != null) {
            out.setEmail(bestEmail.getValue());
        }
        try {
            String id = in.getId();
            Bundle bundle = ctx.getClient().search().forResource(PractitionerRole.class).where(PractitionerRole.PRACTITIONER.hasId(id)).returnBundle(Bundle.class).execute();
            StringBuilder orgs = new StringBuilder();
            int orgCount = 0;

            for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
                if (e.getResource().fhirType() == "PractitionerRole") {
                    PractitionerRole pr = (PractitionerRole) e.getResource();
                    boolean isOk = true;
                    if (pr.hasPeriod())
                    {
                         isOk = FHIRHelper.isInPeriod(pr.getPeriod(),ctx.getNow());
                    }

                    if (isOk)
                    {
                        Reference ref = pr.getOrganization();
                        if (ref.hasDisplay())
                        {
                            JavaHelper.addStringToBufferWithSep(orgs,ref.getDisplay(),", ");
                        }
                        else
                        {
                            Organization o = ReferenceResolver.findOrganization(ref,ctx);
                            if (o != null)
                            {
                                JavaHelper.addStringToBufferWithSep(orgs,o.getName(),", ");
                            }
                        }
                    }
                }
            }
            out.setOrganizationName(orgs.toString());

        }
        catch(Exception e)
        {
            log.warn("Error fetching organization name",e);
        }
        out.setType(Contact.TYPE_PERSON);
        out.setRole(Contact.ROLE_PROVIDER);
        return out;
    }
}
