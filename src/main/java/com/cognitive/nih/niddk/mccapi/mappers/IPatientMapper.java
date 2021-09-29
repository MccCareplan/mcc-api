/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Demographics;
import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import org.hl7.fhir.r4.model.*;
import java.util.List;

public interface IPatientMapper {

    List<Patient.ContactComponent> getActiveContactOfType(Patient in, String type);

    Contact fhir2Contact(Patient.ContactComponent in, Context ctx);
    Contact fhir2Contact(Patient in, Context ctx);

    MccPatient fhir2local(Patient in, Context ctx);
    Demographics fhir2Demographics(Patient in, Context ctx);
}

