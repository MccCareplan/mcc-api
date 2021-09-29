/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Referral;
import com.cognitive.nih.niddk.mccapi.data.ReferralSummary;
import org.hl7.fhir.r4.model.ServiceRequest;

public interface IReferralMapper {
    Referral fhir2local(ServiceRequest in, Context ctx);
    ReferralSummary fhir2summary(ServiceRequest in, Context ctx);
}
