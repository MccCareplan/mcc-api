package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;

import java.util.List;
import java.util.Map;
@Slf4j
public class PractitionerMapper {
    public static Contact fhir2Contact(Practitioner in, Context ctx) {
        Contact out = new Contact();
        out.setName(Helper.getBestName(in.getName()));
        out.setRelFhirId(Helper.getIdString(in.getIdElement()));

        Address a = Helper.findBestAddress(in.getAddress(), "home");
        if (a != null) {
            out.setAddress(Helper.addressToString(a));
        }
        //in.getContact();   //What to d this this in the future

        //Deal with contact points
        List<ContactPoint> contactPoints = Helper.filterToCurrentContactPoints(in.getTelecom());
        Map<String, List<ContactPoint>> cpBySystem = Helper.organizeContactTypesBySystem(contactPoints);
        ContactPoint bestPhone = Helper.findBestContactByType(cpBySystem, "phone", "work|mobile|home");
        if (bestPhone != null) {
            out.setPhone(bestPhone.getValue());
        }
        ContactPoint bestEmail = Helper.findBestContactByType(cpBySystem, "email", "work|mobile");
        if (bestEmail != null) {
            out.setEmail(bestEmail.getValue());
        }
        try {
            String id = in.getId();
            Bundle bundle = ctx.getClient().search().forResource(PractitionerRole.class).where(PractitionerRole.PRACTITIONER.hasId(id)).returnBundle(Bundle.class).execute();
            StringBuffer orgs = new StringBuffer();
            int orgCount = 0;

            for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
                if (e.getResource().fhirType() == "PractitionerRole") {
                    PractitionerRole pr = (PractitionerRole) e.getResource();
                    boolean isOk = true;
                    if (pr.hasPeriod())
                    {
                         isOk =Helper.isInPeriod(pr.getPeriod(),ctx.getNow());
                    }

                    if (isOk)
                    {
                        Reference ref = pr.getOrganization();
                        if (ref.hasDisplay())
                        {
                            Helper.addStringToBufferWithSep(orgs,ref.getDisplay(),", ");
                        }
                        else
                        {
                            Organization o = ReferenceResolver.findOrganization(ref,ctx);
                            if (o != null)
                            {
                                Helper.addStringToBufferWithSep(orgs,o.getName(),", ");
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
