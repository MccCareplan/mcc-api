/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReferralMapper implements IReferralMapper {


    public Referral fhir2local(ServiceRequest in, Context ctx) {
        Referral out = new Referral();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setPurpose(mapper.fhir2local(in.getCode(),ctx));


        if (in.hasPerformerType())
        {
            out.setPerformerType(mapper.fhir2local(in.getPerformerType(),ctx));
        }


        //TODO:  Deal with occurance
        if (in.hasOccurrenceDateTimeType())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.dateTimeToString(in.getOccurrenceDateTimeType().getValue()));
        }
        else if (in.hasOccurrencePeriod())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.periodToString(in.getOccurrencePeriod()));
        }
        else if (in.hasOccurrenceTiming())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.translateTiming(in.getOccurrenceTiming()));
        }
        return out;
    }

    public ReferralSummary fhir2summary(ServiceRequest in, Context ctx) {
        ReferralSummary out = new ReferralSummary();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        out.setPurpose(mapper.fhir2local(in.getCode(),ctx));

        if (in.hasPerformerType())
        {
            out.setPerformerType(mapper.fhir2local(in.getPerformerType(),ctx));
        }


        if (in.hasOccurrenceDateTimeType())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.dateTimeToString(in.getOccurrenceDateTimeType().getValue()));
        }
        else if (in.hasOccurrencePeriod())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.periodToString(in.getOccurrencePeriod()));
        }
        else if (in.hasOccurrenceTiming())
        {
            out.setDate(mapper.fhir2local((Type) in.getOccurrenceTiming(),ctx));
            out.setDisplayDate(FHIRHelper.translateTiming(in.getOccurrenceTiming()));
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
