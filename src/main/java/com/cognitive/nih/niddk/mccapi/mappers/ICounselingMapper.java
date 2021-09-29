/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Counseling;
import com.cognitive.nih.niddk.mccapi.data.CounselingSummary;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;

public interface ICounselingMapper {
    Counseling fhir2local(Procedure in, Context ctx);
    CounselingSummary fhir2summary(Procedure in, Context ctx);
    Counseling fhir2local(ServiceRequest in, Context ctx);
    CounselingSummary fhir2summary(ServiceRequest in, Context ctx);
}
