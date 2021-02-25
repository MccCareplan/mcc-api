package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.data.primative.MccReference;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.cognitive.nih.niddk.mccapi.util.JavaHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GoalMapper implements IGoalMapper {



    public  MccGoal fhir2local(Goal in, Context ctx)
    {
        MccGoal out = new MccGoal();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setId(in.getIdElement().getValue());
        out.setDescription(mapper.fhir2local(in.getDescription(),ctx));
        out.setOutcomeCodes(mapper.fhir2local(in.getOutcomeCode(),ctx));
        if (in.hasAddresses()) {
            out.setAddresses(mapper.fhir2local_referenceArray(in.getAddresses(), ctx));
        }
        out.setCategories(mapper.fhir2local(in.getCategory(),ctx));
        out.setCategorySummary(FHIRHelper.getConceptsAsDisplayString(in.getOutcomeCode()));
        if (in.hasExpressedBy()) {
            out.setExpressedBy(mapper.fhir2local(in.getExpressedBy(), ctx));
        }
        if (in.hasLifecycleStatus()) {
            out.setLifecycleStatus(in.getLifecycleStatus().toCode()); //Weird mapping
        }
        else
        {
            //Catch errors
            log.info("Goal violates US Core - Using a status of unknown");
            out.setLifecycleStatus("unknown");
        }
        out.setPriority(mapper.fhir2local(in.getPriority(),ctx));
        out.setCategories(mapper.fhir2local(in.getCategory(),ctx));

        out.setStatusDate(FHIRHelper.dateTimeToString(in.getStatusDate()));
        out.setStatusReason(in.getStatusReason());
        out.setNotes(FHIRHelper.annotationsToStringList(in.getNote(),ctx));


        if (in.hasStartCodeableConcept())
        {
            out.setUseStartConcept(true);
            out.setStartConcept(mapper.fhir2local(in.getStartCodeableConcept(),ctx));
        }
        else
        {
            out.setUseStartConcept(false);
            out.setStartDateText(FHIRHelper.dateToString(in.getStartDateType().getValue()));
            out.setStartDate(mapper.fhir2local(in.getStartDateType(),ctx));
        }


        List<Goal.GoalTargetComponent> targets = in.getTarget();
        if (targets.size()>0)
        {
            int index=0;
            GoalTarget[] outTargets = new GoalTarget[targets.size()];
            for (Goal.GoalTargetComponent t: targets) {
               outTargets[index] = fhir2local(t,ctx);
               index++;
            }
            out.setTargets(outTargets);
        }

        //http://hl7.org/fhir/StructureDefinition/goal-acceptance
        List<Extension> acc = in.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/goal-acceptance");
        if (acc != null && acc.size()>0) {
            Acceptance[] acceptances = new Acceptance[acc.size()];
            int cnt = 0;
            for (Extension e : acc) {
                Acceptance a = new Acceptance();
                List<Extension> i = e.getExtensionsByUrl("individual");
                Base b = i.get(0).getValue();
                Reference r = b.castToReference(b);
                a.setIndividual(mapper.fhir2local(r,ctx));

                List<Extension> s = e.getExtensionsByUrl("status");
                if (s != null && s.size() > 0)
                {
                    b = s.get(0).getValue();
                    a.setCode(b.castToCode(b).getValue());
                }
                List<Extension> p = e.getExtensionsByUrl("priority");
                if (p!= null && p.size() > 0)
                {
                    b = p.get(0).getValue();
                    a.setPriority(mapper.fhir2local(b.castToCodeableConcept(b),ctx));
                }
                acceptances[cnt]= a;
                cnt++;
            }
            out.setAcceptance(acceptances);
        }
        return out;
    }

    public  GoalSummary fhir2summary(Goal in, Context ctx)
    {
        GoalSummary out = new GoalSummary();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setDescription(in.getDescription().getText());
        out.setLifecycleStatus(in.getLifecycleStatus().toCode());
        Coding pcd = FHIRHelper.getCodingForSystem(in.getPriority(),"http://terminology.hl7.org/CodeSystem/goal-priority");
        out.setPriority(pcd==null?"Undefined":pcd.getCode());
        List<Goal.GoalTargetComponent> targets = in.getTarget();
        MccReference ref = mapper.fhir2local(in.getExpressedBy(),ctx);
        out.setExpressedByType(ref.getType());
        out.setAchievementStatus(mapper.fhir2local(in.getAchievementStatus(),ctx));
        if (in.hasAchievementStatus()) {
            out.setAchievementText(FHIRHelper.getConceptDisplayString(in.getAchievementStatus()));
        }
        if (in.hasStart())
        {
            if (in.hasStartCodeableConcept())
            {
                out.setStartDateText(in.getStartCodeableConcept().getText());
            }
            else if(in.hasStartDateType())
            {
                out.setStartDateText(FHIRHelper.dateToString(in.getStartDateType().getValue()));
            }
        }
        boolean needTargetDate = true;
        if (targets.size()>0)
        {
            int index=0;
            GoalTarget[] outputTargets = new GoalTarget[targets.size()];
            for (Goal.GoalTargetComponent t: targets) {
                if ( needTargetDate && t.hasDue())
                {
                    if (t.hasDueDateType())
                    {
                        out.setTargetDateText(FHIRHelper.dateToString(t.getDueDateType().getValue()));
                    }
                    else if (t.hasDueDuration())
                    {
                        out.setTargetDateText(FHIRHelper.durationToString(t.getDueDuration()));
                    }
                    needTargetDate = false;
                }
                outputTargets[index] = fhir2local(t,ctx);
                index++;
            }
            out.setTargets(outputTargets);
        }
        if (in.hasAddresses())
        {
            out.setAddresses(NameResolver.getReferenceNames(in.getAddresses(),ctx));
        }

        if (in.hasExpressedBy()) {
            out.setExpressedBy(NameResolver.getReferenceName(in.getExpressedBy(), ctx));
        }

        //http://hl7.org/fhir/StructureDefinition/goal-acceptance
        List<Extension> acc = in.getExtensionsByUrl("http://hl7.org/fhir/StructureDefinition/goal-acceptance");
        if (acc != null && acc.size()>0) {
            StringBuilder val = new StringBuilder();
            int cnt = 0;
            for (Extension e : acc) {

                List<Extension> i = e.getExtensionsByUrl("individual");
                Base b = i.get(0).getValue();
                Reference r = b.castToReference(b);
                JavaHelper.addStringToBufferWithSep(val,NameResolver.getReferenceName(r,ctx),",");
                List<Extension> s = e.getExtensionsByUrl("status");
                if (s != null && s.size() > 0)
                {
                    b = s.get(0).getValue();
                    //agree, disagree, pending - Exceptions to agree will be output.
                    String agree = (b.castToCode(b).getValue());
                    if (agree.compareTo("agree")!=0) {
                        val.append(" (");
                        val.append(agree);
                        val.append(")");
                    }
                }
            }
            out.setAcceptance(val.toString());
        }
        return out;
    }

    public  GoalTarget fhir2local(Goal.GoalTargetComponent in, Context ctx)
    {
        GoalTarget out = new GoalTarget();
        IR4Mapper mapper = ctx.getMapper();
        out.setMeasure(mapper.fhir2local(in.getMeasure(),ctx));
        Type x = in.getDue();
        if (x != null)
        {
            out.setDueType(x.fhirType());
        }
        if (in.getDetail()!= null) {
            out.setValue(mapper.fhir2local(in.getDetail(), ctx));
        }
        return out;

    }
}
