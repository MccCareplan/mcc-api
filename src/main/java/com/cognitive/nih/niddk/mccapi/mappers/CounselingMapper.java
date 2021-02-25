package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.data.primative.FuzzyDate;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CounselingMapper implements ICounselingMapper {

    public  Counseling fhir2local(Procedure in, Context ctx) {
        Counseling out = new Counseling();
        IR4Mapper mapper = ctx.getMapper();

        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasOutcome()) {
            out.setOutcome(mapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(mapper.fhir2local(in.getCode(),ctx));
        if (in.hasPerformed())
        {
            out.setDisplayDate(FHIRHelper.typeToDateString(in.getPerformed()));
            FuzzyDate perfOn =new FuzzyDate(in.getPerformed(), ctx);
            out.setDate(perfOn);
        }
        if (in.hasPerformer())
        {
            out.setPerformer(mapper.performerToStringArray(in.getPerformer(),ctx));
        }
        if (in.hasReasonCode() )
        {
            out.setReasonsCodes(mapper.fhir2local(in.getReasonCode(), ctx));
        }
        if (in.hasReasonReference())
        {
            out.setReasons(NameResolver.getReferenceNamesAsArray(in.getReasonReference(),ctx));
        }
        return out;
    }

    public  CounselingSummary fhir2summary(Procedure in, Context ctx) {
        CounselingSummary out = new CounselingSummary();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasOutcome()) {
            out.setOutcome(mapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(mapper.fhir2local(in.getCode(),ctx));
        if (in.hasPerformed())
        {
            out.setDisplayDate(FHIRHelper.typeToDateString(in.getPerformed()));
            FuzzyDate perfOn =new FuzzyDate(in.getPerformed(), ctx);
            out.setDate(perfOn);
        }
        if (in.hasPerformer())
        {
            out.setPerformer(mapper.performerToString(in.getPerformer(),ctx));
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


    public  Counseling fhir2local(ServiceRequest in, Context ctx) {
        Counseling out = new Counseling();
        return out;
    }

    public  CounselingSummary fhir2summary(ServiceRequest in, Context ctx) {
        CounselingSummary out = new CounselingSummary();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("ServiceRequest");
        out.setTopic(mapper.fhir2local(in.getCode(),ctx));
        if (in.hasOccurrence())
        {
            out.setDisplayDate(FHIRHelper.typeToDateString(in.getOccurrence()));
            FuzzyDate perfOn =new FuzzyDate(in.getOccurrence(), ctx);
            out.setDate(perfOn);
        }

        return out;
    }
}
