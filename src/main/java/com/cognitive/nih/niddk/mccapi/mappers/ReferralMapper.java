package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;

import java.util.List;

public class ReferralMapper {



    public static Referral fhir2local(ServiceRequest in, Context ctx) {
        Referral out = new Referral();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setPurpose(CodeableConceptMapper.fhir2local(in.getCode(),ctx));

        //TODO:  Deal with occurance
        if (in.hasOccurrenceDateTimeType())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(Helper.dateTimeToString(in.getOccurrenceDateTimeType().getValue()));
        }
        else if (in.hasOccurrencePeriod())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(Helper.periodToString(in.getOccurrencePeriod()));
        }
        else if (in.hasOccurrenceTiming())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(Helper.translateTiming(in.getOccurrenceTiming()));
        }
        return out;
    }

    public static ReferralSummary fhir2summary(ServiceRequest in, Context ctx) {
        ReferralSummary out = new ReferralSummary();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        out.setPurpose(CodeableConceptMapper.fhir2local(in.getCode(),ctx));

        if (in.hasOccurrenceDateTimeType())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(Helper.dateTimeToString(in.getOccurrenceDateTimeType().getValue()));
        }
        else if (in.hasOccurrencePeriod())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(Helper.periodToString(in.getOccurrencePeriod()));
        }
        else if (in.hasOccurrenceTiming())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceTiming(),ctx));
            out.setDisplayDate(Helper.translateTiming(in.getOccurrenceTiming()));
        }

        if (in.hasPerformer())
        {
            out.setReceiver(NameResolver.getReferenceNames(in.getPerformer(),ctx));
        }
        if (in.hasRequester())
        {
            out.setReferrer(NameResolver.getReferenceName(in.getRequester(),ctx));
        }
        return out;
    }
}
