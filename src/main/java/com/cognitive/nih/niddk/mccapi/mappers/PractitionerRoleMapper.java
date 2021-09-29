/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PractitionerRoleMapper implements IPractitionerRoleMapper {
    @Override
    public Contact fhir2Contact(PractitionerRole in, Context ctx) {
        Contact out = new Contact();
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
        return out;
    }

    @Override
    public void updateContact(PractitionerRole in, Contact out, Context ctx) {
        //Deal with contact points
        List<ContactPoint> contactPoints = FHIRHelper.filterToCurrentContactPoints(in.getTelecom());
        Map<String, List<ContactPoint>> cpBySystem = FHIRHelper.organizeContactTypesBySystem(contactPoints);
        if (StringUtils.isBlank(out.getPhone())) {
            ContactPoint bestPhone = FHIRHelper.findBestContactByType(cpBySystem, "phone", "work|mobile|home");
            if (bestPhone != null) {
                out.setPhone(bestPhone.getValue());
            }
        }
        if (StringUtils.isBlank((out.getEmail()))) {
            ContactPoint bestEmail = FHIRHelper.findBestContactByType(cpBySystem, "email", "work|mobile");
            if (bestEmail != null) {
                out.setEmail(bestEmail.getValue());
            }
        }

    }
}
