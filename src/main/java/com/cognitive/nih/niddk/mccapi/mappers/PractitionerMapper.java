package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.cognitive.nih.niddk.mccapi.util.JavaHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class PractitionerMapper implements IPractitionerMapper {
    @Value("${mcc.provider.pullbyrole}")
    private String usePullByRole;
    private boolean isUsePullByRole;

    private final IPractitionerRoleMapper practitionerRoleMapper;
    private final QueryManager queryManager;

    public PractitionerMapper(IPractitionerRoleMapper practitionerRoleMapper, QueryManager queryManager) {
        this.practitionerRoleMapper = practitionerRoleMapper;
        this.queryManager = queryManager;
    }

    @PostConstruct
    public void config()
    {
        isUsePullByRole = Boolean.parseBoolean(usePullByRole);
        log.info("Config: mcc.provider.pullbyrole = "+usePullByRole);
    }

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

            Map<String, String> values = new HashMap<>();
            values.put("id", in.getId());

            String callUrl = queryManager.setupQuery("PractitionerRole.Query", values);


            String id = in.getId();
            Bundle bundle = ctx.getClient().fetchResourceFromUrl(Bundle.class,callUrl);
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
                                if (StringUtils.isBlank(out.getAddress())&& o.hasAddress())
                                {
                                    a = FHIRHelper.findBestAddress(o.getAddress(), "work");
                                    if (a != null) {
                                        out.setAddress(FHIRHelper.addressToString(a));
                                    }
                                }
                            }
                        }
                        if (isUsePullByRole)
                        {
                            //Update Contact data as needed
                            practitionerRoleMapper.updateContact(pr,out,ctx);
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
