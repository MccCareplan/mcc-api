package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.GoalLists;
import com.cognitive.nih.niddk.mccapi.data.GoalSummary;
import com.cognitive.nih.niddk.mccapi.data.MccGoal;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.mappers.GoalMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Goal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class GoalController {

    @GetMapping("/goalsummary")
    public GoalLists getGoalSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers) {
        GoalLists out = new GoalLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Goal") {
                Goal g = (Goal) e.getResource();
                GoalSummary gs = GoalMapper.summaryfhir2local(g,ctx);
                out.addSummary(gs);
            }
        }
        //out.categorizeConditions();
        return out;
    }

    @GetMapping("/goal")
    public MccGoal[] getGoals(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers) {
        ArrayList<MccGoal> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
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
    public MccGoal getGoal(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers) {
        MccGoal g;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Goal fg = client.read().resource(Goal.class).withId(id).execute();
        String subjectId = fg.getSubject().getId();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        g = GoalMapper.fhir2local(fg,ctx);
        return g;
    }


}
