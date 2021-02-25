package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CareTeamMapper implements ICareTeamMapper {


    public  List<Contact> fhir2Contacts(CareTeam in, Context ctx) {
        ArrayList<Contact> out = new ArrayList<>();
        String teamName = in.getName();
        String teamId = in.getIdElement().getResourceType() + "/" + in.getIdElement().getId();
        Date now = new Date();
        if (in.hasPeriod())
        {
            if (!FHIRHelper.isInPeriod(in.getPeriod(),now))
            {
                //Early Exit
                return out;
            }
        }
        if (in.hasParticipant())
        {
            List<CareTeam.CareTeamParticipantComponent> participants = in.getParticipant();

            for (CareTeam.CareTeamParticipantComponent p: participants)
            {
                if (p.hasPeriod())
                {
                    if (!FHIRHelper.isInPeriod(p.getPeriod(),now))
                    {
                        //Skip if the member is not in the active period
                        continue;
                    }
                }
                Contact m = fhir2Contact(p, ctx);
                if (m != null)
                {
                    m.setTeamId(teamId);
                    m.setTeamName(teamName);
                    out.add(m);
                }

            }
        }
        /*
        out.setName(Helper.getBestName(in.getName()));
        out.setRelFhirId(in.getIdElement().getResourceType() + "/" + in.getIdElement().getId());

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


        out.setType(Contact.TYPE_PERSON);
        out.setRole(Contact.ROLE_PATIENT);
        */

        return out;
    }

    public Contact fhir2Contact(CareTeam.CareTeamParticipantComponent in, Context ctx) {
        Contact out = null;
        String role = "Unknown Role";
        IR4Mapper mapper = ctx.getMapper();

        if (in.hasRole())
        {
            role = FHIRHelper.getConceptsAsDisplayString(in.getRole());
        }
        if (in.hasMember())
        {
            Reference m = in.getMember();
            if (!m.hasReference())
            {
                if (m.hasDisplay())
                {
                    out = new Contact();
                    out.setName(m.getDisplay());
                }
            }
            else
            {
                String type = FHIRHelper.getReferenceType(m);
                if (type.compareTo("Practitioner") == 0)
                {
                    Practitioner p = ReferenceResolver.findPractitioner(m,ctx);
                    if (p != null) {
                        out = mapper.fhir2Contact(p, ctx);
                    }
                }
                else if (type.compareTo("RelatedPerson")==0)
                {
                    RelatedPerson r = ReferenceResolver.findRelatedPerson(m,ctx);
                    if ( r != null) {
                        out = mapper.fhir2Contact(r,ctx);
                    }
                }
                else if (type.compareTo("Organization")==0)
                {
                    Organization o = ReferenceResolver.findOrganization(m, ctx);
                    if (o != null)
                    {
                        out = mapper.fhir2Contact(o,ctx);
                    }
                }

            }

        }

        if (out != null) {
            out.setRole(role);
            if (in.hasOnBehalfOf())
            {
                Reference m = in.getOnBehalfOf();
                out.setOrganizationName(NameResolver.getReferenceName(m,ctx));
            }
        }
        return out;
    }
}
