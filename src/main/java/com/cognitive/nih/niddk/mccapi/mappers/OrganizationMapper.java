package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Organization;

import java.util.List;
import java.util.Map;

public class OrganizationMapper {

    public static Contact fhir2Contact(Organization in, Context ctx) {
        Contact out = new Contact();
        out.setName(in.getName());
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


        out.setType(Contact.TYPE_ORGANIZATION);
        return out;
    }
}

