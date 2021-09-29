/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlanSummary;
import org.hl7.fhir.r4.model.CarePlan;

import java.util.Set;

public interface ICareplanMapper {

    MccCarePlan fhir2local(CarePlan in, Context ctx);
    MccCarePlanSummary fhir2Summary(CarePlan in, Set<String> profiles, Context ctx);
}
