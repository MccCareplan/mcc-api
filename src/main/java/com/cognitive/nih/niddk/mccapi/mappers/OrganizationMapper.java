/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrganizationMapper implements IOrganizationMapper {

    public Contact fhir2Contact(Organization in, Context ctx) {
        Contact out = new Contact();
        out.setName(NameResolver.getName(in,ctx));
        out.setRelFhirId(FHIRHelper.getIdString(in.getIdElement()));

        Address a = FHIRHelper.findBestAddress(in.getAddress(), "work");
        if (a != null) {
            out.setAddress(FHIRHelper.addressToString(a));
        }
        //in.getContact();   //What to d this this in the future

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


        out.setType(Contact.TYPE_ORGANIZATION);
        return out;
    }
}

