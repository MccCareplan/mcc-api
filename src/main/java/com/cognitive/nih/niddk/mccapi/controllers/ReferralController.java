package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.CounselingSummary;
import com.cognitive.nih.niddk.mccapi.data.ReferralSummary;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.CounselingMapper;
import com.cognitive.nih.niddk.mccapi.mappers.ReferralMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")

public class ReferralController {
    private final QueryManager queryManager;

    public ReferralController(QueryManager queryManager) {
        this.queryManager = queryManager;

    }

    @GetMapping("/summary/referrals")
    public ReferralSummary[] getCounselingSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<ReferralSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();

        String callUrl = queryManager.setupQuery("Referral.ServiceRequest.Query",values,webRequest);


        //Possible Sources
        //     ServiceRequests - Education category = 409073007, Counseling 409063005

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

                    if (p.hasRequester()) {
                        //TODO: Filter by intent
                        if (p.hasPerformer() ) {
                            if (isPerformerProvider(p.getPerformer())) {
                                ReferralSummary cs = ReferralMapper.fhir2summary(p, ctx);
                                out.add(cs);
                            }
                        }
                    }
                }
            }
        }

        ReferralSummary[] outA = new ReferralSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    private boolean isPerformerProvider(List<Reference> performers)
    {
        boolean ok = true;

        search:
        for(Reference ref: performers)
        {
            String type = Helper.getReferenceType(ref);
            switch(type)
            {
                case "Patient":
                case "RelatedPerson":
                {
                    ok = false;
                    break search;
                }
                default:
                {
                    break;
                }
            }
        }
        return ok;
    }
}

