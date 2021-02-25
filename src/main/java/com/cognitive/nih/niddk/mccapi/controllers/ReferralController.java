package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.ReferralSummary;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")

public class ReferralController {
    private final QueryManager queryManager;
    private final IR4Mapper mapper;

    private static final HashSet<String> supportedCategories = new HashSet<>();

    static {
        // List of categories: 440379008,3457005,409073007,409063005
        supportedCategories.add("440379008"); // “Referral to”
        supportedCategories.add("3457005"); // “Patient Referral"
        supportedCategories.add("409073007"); // “Education"
        supportedCategories.add("409063005"); // “Counseling”

    }

    public ReferralController(QueryManager queryManager, IR4Mapper mapper) {
        this.queryManager = queryManager;
        this.mapper = mapper;
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
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("ServiceRequest")==0) {
                    ServiceRequest p = (ServiceRequest) e.getResource();
                    //TODO: Add filter by status  active, completed

                    //Filter out lab, surgery
                    if (p.hasCategory()) {
                        List<CodeableConcept> categories = p.getCategory();

                        //Add PerformerType
                        if (p.hasRequester()) {
                            //TODO: Filter by intent
                            if (p.hasPerformer()) {
                                if (isPerformerProvider(p.getPerformer())) {
                                    ReferralSummary cs = mapper.fhir2summary(p, ctx);
                                    out.add(cs);
                                }
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
    private boolean isCategorySupported(List<CodeableConcept> categories)
    {
        boolean out = false;
        Search:
        for (CodeableConcept concept: categories)
        {
            List<Coding> codes = concept.getCoding();
            for (Coding code: codes)
            {
                if (supportedCategories.contains(code.getCode()))
                {
                    out = true;
                    break Search;
                }
            }
        }
        return out;
    }

    private boolean isPerformerProvider(List<Reference> performers)
    {
        boolean ok = true;

        search:
        for(Reference ref: performers)
        {
            String type = FHIRHelper.getReferenceType(ref);
            switch(type)
            {
                case "Patient":
                case "Device":
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

