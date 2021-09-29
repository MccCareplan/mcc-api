/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import org.hl7.fhir.r4.model.Practitioner;

public interface IPractitionerMapper {
    Contact fhir2Contact(Practitioner in, Context ctx);
}
