package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.EducationMapper;
import com.cognitive.nih.niddk.mccapi.mappers.GoalMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")

public class EducationController {
    private final QueryManager queryManager;

    public EducationController(QueryManager queryManager) {
        this.queryManager = queryManager;

    }

    @GetMapping("/summary/educations")
    public EducationSummary[] getEducationSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<EducationSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();

        String callUrl=queryManager.setupQuery("Education.Procedure.Query",values,webRequest);


        //Possible Sources
        ///    Procedures - Education category = 409073007, Counseling 409063005
        //     ServiceRequests - Same

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Procedure")==0){
                    Procedure p = (Procedure) e.getResource();
                    //TODO: Add filter by status  active, completed
                    //TODO: Filter by intent
                   EducationSummary es = EducationMapper.fhir2summary(p, ctx);
                    out.add(es);
                }
            }
        }

        callUrl=queryManager.setupQuery("Education.ServiceRequest.Query",values,webRequest);


        //Possible Sources
        ///    Procedures - Education category = 409073007, Counseling 409063005
        //     ServiceRequests - Same

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("ServiceRequest")==0){
                    ServiceRequest p = (ServiceRequest) e.getResource();
                    //TODO: Add filter by status  active, completed
                    //TODO: Filter by intent
                    EducationSummary es = EducationMapper.fhir2summary(p, ctx);
                    out.add(es);
                }
            }
        }

        EducationSummary[] outA = new EducationSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

}

