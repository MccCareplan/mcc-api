package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import org.hl7.fhir.r4.model.RelatedPerson;

public interface IRelatedPersonMapper {
    Contact fhir2Contact(RelatedPerson in, Context ctx);
}
