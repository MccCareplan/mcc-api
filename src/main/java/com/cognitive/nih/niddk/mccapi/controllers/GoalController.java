package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.GoalMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
public class GoalController {

    @GetMapping("/goalsummary")
    public GoalLists getGoalSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestParam(required = false, name = "server") String serverId) {
        GoalLists out = new GoalLists();

        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(Condition.class).where(Goal.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Goal") {
                Goal c = (Goal) e.getResource();
                addGoalToGoalList(out,c, ctx);
            }
        }
        //out.categorizeConditions();
        return out;
    }

    private void addGoalToGoalList(GoalLists list, Goal g, Context ctx)
    {

    }

    @GetMapping("/goal")
    public MccGoal[] getGoals(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "server") String serverId) {
        ArrayList<MccGoal> out = new ArrayList<>();
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(Goal.class).where(CarePlan.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Goal") {
                Goal g = (Goal) e.getResource();
                out.add(GoalMapper.fhir2local(g,ctx));
            }
        }

        MccGoal[] outA = new MccGoal[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/goal/{id}")
    public MccGoal getGoal(@PathVariable(value = "id") String id, @RequestParam(required = false) String serverId) {
        MccGoal g;
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Goal fg = client.read().resource(Goal.class).withId(id).execute();
        String subjectId = fg.getSubject().getId();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        g = GoalMapper.fhir2local(fg,ctx);
        return g;
    }


}
