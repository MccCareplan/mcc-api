package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Education;
import com.cognitive.nih.niddk.mccapi.data.EducationSummary;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;

public interface IEducationMapper {
    Education fhir2local(Procedure in, Context ctx);
    EducationSummary fhir2summary(Procedure in, Context ctx);
    Education fhir2local(ServiceRequest in, Context ctx);
    EducationSummary fhir2summary(ServiceRequest in, Context ctx);
}
