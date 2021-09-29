/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import org.hl7.fhir.r4.model.CareTeam;

import java.util.List;

public interface ICareTeamMapper {
    List<Contact> fhir2Contacts(CareTeam in, Context ctx);
    Contact fhir2Contact(CareTeam.CareTeamParticipantComponent in, Context ctx);
}
