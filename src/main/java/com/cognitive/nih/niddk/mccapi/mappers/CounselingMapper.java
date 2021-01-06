package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;

import java.util.ArrayList;

public class CounselingMapper {

    public static Counseling fhir2local(Procedure in, Context ctx) {
        Counseling out = new Counseling();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasOutcome()) {
            out.setOutcome(CodeableConceptMapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(CodeableConceptMapper.fhir2local(in.getCode(),ctx));

        if (in.hasPerformer())
        {
            out.setPerformer(PerformerMapper.performerToStringArray(in.getPerformer(),ctx));
        }
        if (in.hasReasonCode() )
        {
            out.setReasonsCodes(CodeableConceptMapper.fhir2local(in.getReasonCode(), ctx));
        }
        if (in.hasReasonReference())
        {
            out.setReasons(NameResolver.getReferenceNamesAsArray(in.getReasonReference(),ctx));
        }
        return out;
    }

    public static CounselingSummary fhir2summary(Procedure in, Context ctx) {
        CounselingSummary out = new CounselingSummary();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasOutcome()) {
            out.setOutcome(CodeableConceptMapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(CodeableConceptMapper.fhir2local(in.getCode(),ctx));
        if (in.hasPerformer())
        {
            out.setPerformer(PerformerMapper.performerToString(in.getPerformer(),ctx));
        }

        if (in.hasReasonCode() || in.hasReasonReference())
        {
            StringBuilder reasons = new StringBuilder();
            if (in.hasReasonCode())
            {
                reasons.append(FHIRHelper.getConceptsAsDisplayString(in.getReasonCode()));
            }
            if (in.hasReasonReference())
            {
                String names = NameResolver.getReferenceNames(in.getReasonReference(),ctx);
                if (reasons.length()>0)
                {
                    reasons.append(",");
                }
                reasons.append(names);
            }
            out.setReasons(reasons.toString());

        }

        return out;
    }


    public static Counseling fhir2local(ServiceRequest in, Context ctx) {
        Counseling out = new Counseling();
        return out;
    }

    public static CounselingSummary fhir2summary(ServiceRequest in, Context ctx) {
        CounselingSummary out = new CounselingSummary();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("ServiceRequest");
        out.setTopic(CodeableConceptMapper.fhir2local(in.getCode(),ctx));
        //TODO:  Deal with occurance
        if (in.hasOccurrenceDateTimeType())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.dateTimeToString(in.getOccurrenceDateTimeType().getValue()));
        }
        else if (in.hasOccurrencePeriod())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceDateTimeType(),ctx));
            out.setDisplayDate(FHIRHelper.periodToString(in.getOccurrencePeriod()));
        }
        else if (in.hasOccurrenceTiming())
        {
            out.setDate(GenericTypeMapper.fhir2local((Type) in.getOccurrenceTiming(),ctx));
            out.setDisplayDate(FHIRHelper.translateTiming(in.getOccurrenceTiming()));
        }
        return out;
    }
}
