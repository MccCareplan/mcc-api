/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.MedicationSummary;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;

public interface IMedicationMapper {
    MccMedicationRecord fhir2local(MedicationRequest in, Context ctx);
    MccMedicationRecord fhir2local(MedicationStatement in, Context ctx);
    MedicationSummary fhir2summary(MedicationRequest in, Context ctx);
    MedicationSummary fhir2summary(MedicationStatement in, Context ctx);
}
