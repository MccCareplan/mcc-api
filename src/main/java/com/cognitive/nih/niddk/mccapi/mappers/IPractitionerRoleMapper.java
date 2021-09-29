/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;

public interface IPractitionerRoleMapper {
    Contact fhir2Contact(PractitionerRole in, Context ctx);
    void updateContact(PractitionerRole in, Contact contact, Context ctx);
}
