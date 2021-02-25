package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.GoalLists;
import com.cognitive.nih.niddk.mccapi.data.GoalSummary;
import com.cognitive.nih.niddk.mccapi.data.MccGoal;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Goal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class GoalController {
    private final QueryManager queryManager;
    private final IR4Mapper mapper;

    public GoalController(QueryManager queryManager, IR4Mapper mapper) {
        this.queryManager = queryManager;
        this.mapper = mapper;
    }


    /**
     *
     * @param subjectId
     * @param careplanId
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/summary/goals")
    public GoalLists getGoalSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        return commonGoalSummary(subjectId,careplanId,headers,webRequest);
    }

    /**
     * @Deprecated please use /summary/gooal
     * @param subjectId
     * @param careplanId
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/goalsummary")
    public GoalLists getGoalSummaryOld(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        return commonGoalSummary(subjectId,careplanId,headers,webRequest);
    }
    private GoalLists commonGoalSummary( String subjectId, String careplanId,Map<String, String> headers, WebRequest webRequest)
    {
        GoalLists out = new GoalLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        String callUrl=queryManager.setupQuery("Goal.Query",values,webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Goal")==0){
                    Goal g = (Goal) e.getResource();
                    GoalSummary gs = mapper.fhir2summary(g, ctx);
                    out.addSummary(gs);
                }
            }
        }
        return out;
    }

    @GetMapping("/goal")
    public MccGoal[] getGoals(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccGoal> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        String callUrl=queryManager.setupQuery("Goal.Query",values,webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            //Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //        .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Goal") == 0) {
                    Goal g = (Goal) e.getResource();
                    out.add(mapper.fhir2local(g, ctx));
                }
            }
        }
        MccGoal[] outA = new MccGoal[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/goal/{id}")
    public MccGoal getGoal(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccGoal g;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        values.put("id",id);
        String callUrl=queryManager.setupQuery("Goal.Lookup",values,webRequest);

        if (callUrl != null) {
            Goal fg = client.fetchResourceFromUrl(Goal.class, callUrl);

            //Goal fg = client.read().resource(Goal.class).withId(id).execute();
            String subjectId = fg.getSubject().getId();
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            g = mapper.fhir2local(fg, ctx);
        }
        else
        {
            //TODO: Return unavailable goal
            g = new MccGoal();
            log.warn("Goal Lookup disabled, Goal "+id+" Not found");
        }
        return g;
    }


}
