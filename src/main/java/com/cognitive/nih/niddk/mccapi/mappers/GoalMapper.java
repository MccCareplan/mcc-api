package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.GoalTarget;
import com.cognitive.nih.niddk.mccapi.data.MccGoal;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Goal;

import java.util.List;

public class GoalMapper {

    public static MccGoal fhir2local(Goal in, Context ctx)
    {
        MccGoal out = new MccGoal();
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setId(in.getIdElement().getValue());
        out.setDescription(CodeableConceptMapper.fhir2local(in.getDescription(),ctx));
        out.setOutcomeCodes(CodeableConceptMapper.fhir2local(in.getOutcomeCode(),ctx));
        out.setAddresses(ReferenceMapper.fhir2local(in.getAddresses(),ctx));
        out.setCategories(CodeableConceptMapper.fhir2local(in.getCategory(),ctx));
        out.setCategorySummary(Helper.getConceptsAsDisplayString(in.getOutcomeCode()));
        out.setExpressedBy(ReferenceMapper.fhir2local(in.getExpressedBy(),ctx));
        out.setLifecycleStatus(in.getLifecycleStatus().toCode()); //Weird mapping
        out.setPriority(CodeableConceptMapper.fhir2local(in.getPriority(),ctx));
        out.setCategories(CodeableConceptMapper.fhir2local(in.getCategory(),ctx));
        out.setStatusDate(Helper.dateTimeToString(in.getStatusDate()));
        out.setStatusReason(in.getStatusReason());
        out.setNotes(Helper.AnnotationsToStringList(in.getNote()));


        if (in.hasStartCodeableConcept())
        {
            out.setUseStartConcept(true);
            out.setStartConcept(CodeableConceptMapper.fhir2local(in.getStartCodeableConcept(),ctx));
        }
        else
        {
            out.setUseStartConcept(false);
            out.setStartText(Helper.dateToString(in.getStartDateType().getValue()));
            //TODO: Add Actual date when supported
        }
        List<Goal.GoalTargetComponent> targets = in.getTarget();
        if (targets.size()>0)
        {
            int index=0;
            GoalTarget[] otargets = new GoalTarget[targets.size()];
            for (Goal.GoalTargetComponent t: targets) {
               otargets[index] = fhir2local(t,ctx);
               index++;
            }
            out.setTargets(otargets);
        }

        return out;
    }

    public static GoalTarget fhir2local(Goal.GoalTargetComponent in, Context ctx)
    {
        GoalTarget out = new GoalTarget();
        out.setMeasure(CodeableConceptMapper.fhir2local(in.getMeasure(),ctx));
        out.setDueType(in.getDue().fhirType());
        return out;

    }
}
