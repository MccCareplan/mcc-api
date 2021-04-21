package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.cognitive.nih.niddk.mccapi.data.ReferralSummary;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.cognitive.nih.niddk.mccapi.util.MCC2HFHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")

public class ReferralController {
    private static final HashSet<String> supportedCategories = new HashSet<>();
    private static final int ACTIVE_LIST = 0;
    private static final int IGNORE = 1;
    private static HashMap<String, Integer> activeStatus = new HashMap<>();
    private static HashMap<String, Integer> activeIntent = new HashMap<>();

    private static String valueSetId = "Referrals";

    static {
        // List of categories: 440379008,3457005,409073007,409063005
        supportedCategories.add("440379008"); // “Referral to”
        supportedCategories.add("3457005"); // “Patient Referral"
        supportedCategories.add("409073007"); // “Education"
        supportedCategories.add("409063005"); // “Counseling”


        // Status
        //	draft, active, on-hold, revoked, completed, entered-in-error, unknown
        //
        // Intent
        //  proposal, plan, directive, order, original-order, reflex-order, filler-order, instance-order, option
        //
        // Category in handle in logic problem-list-item | encounter-diagnosis | health-concern

        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);
        //	active | recurrence | relapse
        activeStatus.put("active", active);
        activeStatus.put("draft", active);
        activeStatus.put("on-hold", active);
        activeStatus.put("revoked", ignore);
        activeStatus.put("completed", active);
        activeStatus.put("entered-in-error", ignore);
        activeStatus.put("unknown", ignore);

        activeIntent.put("proposal", active);
        activeIntent.put("plan", active);
        activeIntent.put("directive", ignore);
        activeIntent.put("order", active);
        activeIntent.put("original-order", active);
        activeIntent.put("reflex-order", active);
        activeIntent.put("filler-order", active);
        activeIntent.put("instance-order", active);
        activeIntent.put("option", ignore);
        activeIntent.put("unknown", ignore);

    }

    private final QueryManager queryManager;
    private final IR4Mapper mapper;
    @Value("${mcc.referral.require_performer:true}")
    private String requirePerformer;
    private boolean bRequirePerformer = true;
    @Value("${mcc.referral.require_category:true}")
    private String requireCategory;
    private boolean bRequireCategory = true;
    @Value("${mcc.referral.use_valueset:true}")
    private String useValueSet;
    private boolean bUseValueSet = true;

    public ReferralController(QueryManager queryManager, IR4Mapper mapper) {
        this.queryManager = queryManager;
        this.mapper = mapper;
    }

    @PostConstruct
    public void config() {
        bRequirePerformer = Boolean.parseBoolean(requirePerformer);
        log.info("Config: mcc.referral.require_performer = " + requirePerformer);
        bRequireCategory = Boolean.parseBoolean(requireCategory);
        log.info("Config: mcc.referral.require_category = " + requireCategory);
        bUseValueSet = Boolean.parseBoolean(useValueSet);
        log.info("Config: mcc.referral.use_valueset = " + useValueSet);
    }

    @GetMapping("/summary/referrals")
    public ReferralSummary[] getReferralSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<ReferralSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        MccValueSet valueSet = null;
        if (bUseValueSet)
        {
            valueSet= ValueSetManager.getValueSetManager().findValueSet(valueSetId);
            if (valueSet == null)
            {
                log.warn("Referrals based on valueset enabled, but value set "+valueSetId+" is not present");
            }
        }

        Map<String, String> values = new HashMap<>();

        String callUrl = queryManager.setupQuery("Referral.ServiceRequest.Query", values, webRequest);


        //Possible Sources
        //     ServiceRequests - Education category = 409073007, Counseling 409063005

        if ((callUrl != null) && (bUseValueSet?(valueSet!=null):true) ) {


            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();

            out.ensureCapacity(results.getTotal());
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("ServiceRequest") == 0) {
                    ServiceRequest p = (ServiceRequest) e.getResource();

                    if (p.hasStatus()) {
                        String status = p.getStatus().toCode();
                        Integer v = activeStatus.get(status);
                        if (v == null) {
                            log.warn("Unknown ServiceRequest Status Type (" + status + ") used: " + p.getId().toString());
                            continue;
                        }
                        if (v.intValue() == IGNORE) {
                            // Ignore this status
                            continue;
                        }
                    }
                    if (p.hasIntent()) {
                        String intent = p.getIntent().toCode();
                        Integer v = activeIntent.get(intent);
                        if (v == null) {
                            log.warn("Unknown ServiceRequest Intent Type (" + intent + ") used: " + p.getId().toString());
                            continue;
                        }
                        if (v.intValue() == IGNORE) {
                            // Ignore this status
                            continue;
                        }

                    }
                    if (bRequireCategory) {
                        if (p.hasCategory()) {
                            List<CodeableConcept> categories = p.getCategory();

                            if (!isCategorySupported(p.getCategory())) {
                                //skip this entry uses an unsupported category
                                continue;
                            }
                        } else {
                            //Skip - Category is required.
                            continue;
                        }
                    }

                    if (bRequirePerformer) {

                        if (p.hasPerformer()) {
                            if (!isPerformerProvider(p.getPerformer())) {
                                //Skip - performer is not a provider
                                continue;
                            }
                        } else {
                            //Skip - Performer is required but not present
                            continue;
                        }
                    }

                    //Must has a requestor
                    if (!p.hasRequester()) {
                        continue;
                    }
                    if (bUseValueSet)
                    {
                        if (p.hasCode()) {
                            if (!MCC2HFHIRHelper.conceptInValueSet(p.getCode(),valueSet))
                            {
                                //Skip - this concept is not one we consider to be a referral
                                continue;
                            }
                        }
                    }
                    //Ok we have pass the filer checks
                    ReferralSummary cs = mapper.fhir2summary(p, ctx);
                    out.add(cs);

                }
            }
        }

        ReferralSummary[] outA = new ReferralSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }


    private boolean isCategorySupported(List<CodeableConcept> categories) {
        boolean out = false;
        Search:
        for (CodeableConcept concept : categories) {
            List<Coding> codes = concept.getCoding();
            for (Coding code : codes) {
                if (supportedCategories.contains(code.getCode())) {
                    out = true;
                    break Search;
                }
            }
        }
        return out;
    }

    private boolean isPerformerProvider(List<Reference> performers) {
        boolean ok = true;

        search:
        for (Reference ref : performers) {
            String type = FHIRHelper.getReferenceType(ref);
            switch (type) {
                case "Patient":
                case "Device":
                case "RelatedPerson": {
                    ok = false;
                    break search;
                }
                default: {
                    break;
                }
            }
        }
        return ok;
    }
}

