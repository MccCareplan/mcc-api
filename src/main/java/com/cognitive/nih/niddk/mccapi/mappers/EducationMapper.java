/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Education;

import com.cognitive.nih.niddk.mccapi.data.EducationSummary;
import com.cognitive.nih.niddk.mccapi.data.primative.FuzzyDate;
import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EducationMapper implements IEducationMapper {

    public  Education fhir2local(Procedure in, Context ctx) {
        Education out = new Education();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasOutcome()) {
            out.setOutcome(mapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(mapper.fhir2local(in.getCode(),ctx));
        if (in.hasPerformer())
        {
            out.setPerformers(mapper.performerToStringArray(in.getPerformer(),ctx));
        }
        if (in.hasPerformed())
        {
            out.setDisplayDate(FHIRHelper.typeToDateString(in.getPerformed()));
            FuzzyDate perfOn =new FuzzyDate(in.getPerformed(), ctx);
            out.setDate(perfOn);
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

    public  EducationSummary fhir2summary(Procedure in, Context ctx) {
        EducationSummary out = new EducationSummary();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        out.setType("Procedure");
        if (in.hasPerformed())
        {
            out.setDisplayDate(FHIRHelper.typeToDateString(in.getPerformed()));
            FuzzyDate perfOn = new FuzzyDate(in.getPerformed(), ctx);
            out.setDate(perfOn);
        }
        if (in.hasOutcome()) {
            out.setOutcome(mapper.fhir2local(in.getOutcome(), ctx));
        }
        out.setTopic(mapper.fhir2local(in.getCode(),ctx));
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


    public  Education fhir2local(ServiceRequest in, Context ctx) {
        Education out = new Education();
        return out;
    }

    public  EducationSummary fhir2summary(ServiceRequest in, Context ctx) {
        EducationSummary out = new EducationSummary();
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
