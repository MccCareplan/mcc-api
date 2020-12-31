package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.EducationSummary;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.EducationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
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
    private static final int ACTIVE_LIST = 0;
    private static final int INACTIVE_LIST = 1;
    private static final int IGNORE = 2;
    private static final HashMap<String, Integer> activeKeys = new HashMap<>();

    static {
        // Procedure Status
        //		preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error | unknown
        //

        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer inactive = Integer.valueOf(INACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);
        //	active | recurrence | relapse
        activeKeys.put("preparation", active);
        activeKeys.put("in-progress", active);
        activeKeys.put("not-done", active);
        activeKeys.put("on-hold", active);
        activeKeys.put("stopped", inactive);
        activeKeys.put("completed", inactive);
        activeKeys.put("entered-in-error", ignore);
        activeKeys.put("unknown", ignore);

    }

    private final QueryManager queryManager;
    private final boolean SERVICE_REQUEST_ENABLED = false;


    public EducationController(QueryManager queryManager) {
        this.queryManager = queryManager;

    }

    @GetMapping("/summary/educations")
    public EducationSummary[] getEducationSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<EducationSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        String callUrl = queryManager.setupQuery("Education.Procedure.Query", values, webRequest);


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
                if (e.getResource().fhirType().compareTo("Procedure") == 0) {
                    Procedure p = (Procedure) e.getResource();
                    if (p.hasStatus()) {
                        String status = p.getStatus().toCode();
                        Integer s = activeKeys.get(status);
                        if (s != null && s.intValue() != IGNORE) {
                            //TODO: Filter by intent
                            EducationSummary es = EducationMapper.fhir2summary(p, ctx);
                            out.add(es);
                        }
                    }
                }
            }
        }

        callUrl = queryManager.setupQuery("Education.ServiceRequest.Query", values, webRequest);


        //Possible Sources
        ///    Procedures - Education category = 409073007, Counseling 409063005
        //     ServiceRequests - Same

        if (callUrl != null && SERVICE_REQUEST_ENABLED) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("ServiceRequest") == 0) {
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

