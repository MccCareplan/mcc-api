/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccDosage;
import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import org.hl7.fhir.r4.model.*;

import java.util.Date;
import java.util.List;

public interface IGenericTypeMapper {
    GenericType fhir2local(Type in, Context ctx);
    MccDate fhir2local(DateType in, Context ctx);
    MccId fhir2local(IdType in, Context ctx);

    MccDate fhir2local(Date in, Context ctx);
    MccCoding fhir2local(Coding in, Context ctx);
    MccIdentifer[] fhir2local_identifierArray(List<Identifier> in, Context ctx);
    MccDateTime[] fhir2local(List<DateTimeType> in, Context ctx);
    MccDateTime fhir2local(DateTimeType in, Context ctx);
    MccTime fhir2local(TimeType in, Context ctx);
    MccPeriod fhir2local(Period in, Context ctx);
    MccSampledData fhir2local(SampledData in, Context ctx);
    MccSimpleQuantity fhir2local(SimpleQuantity in, Context ctx);
    MccIdentifer fhir2local(Identifier in, Context ctx);
    MccReference fhir2local(Reference in, Context ctx);
    MccCodeableConcept fhir2local(CodeableConcept in, Context ctx);
    MccQuantity fhir2local(Quantity in, Context ctx);
    MccRatio fhir2local(Ratio in, Context ctx);
    MccRange fhir2local(Range in, Context ctx);
    MccDuration fhir2local(Duration in, Context ctx);
    MccInstant fhir2local(InstantType in, Context ctx);
    MccTiming fhir2local(Timing in, Context ctx);
    MccDosage[] fhir2local_dosageList(List<Dosage> in, Context ctx);
    MccDosage fhir2local(Dosage in, Context ctx);

}
