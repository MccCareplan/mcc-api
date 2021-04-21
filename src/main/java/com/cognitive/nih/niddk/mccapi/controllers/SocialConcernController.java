package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.MCC2HFHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class SocialConcernController {
    private final QueryManager queryManager;
    private final IR4Mapper mapper;
    @Value("${mcc.social_concern.use_category:true}")
    private String useCategopry;
    private boolean bUseCategory = true;
    @Value("${mcc.social_concern.use_valueset:true}")
    private String useValueSet;
    private boolean bUseValueSet = true;

    public static String valueSetId = "SocialConcerns";

    public SocialConcernController(QueryManager queryManager, IR4Mapper mapper)
    {
        this.queryManager = queryManager;
        this.mapper = mapper;
    }

    @PostConstruct
    public void config() {

        bUseCategory = Boolean.parseBoolean(useCategopry);
        log.info("Config: mcc.social_concern.require_category = " + useCategopry);
        bUseValueSet = Boolean.parseBoolean(useValueSet);
        log.info("Config: mcc.social_concern.use_valueset = " + useValueSet);
    }


    @GetMapping("/socialconcernsummary")
    public ConditionLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ConditionLists out = new ConditionLists();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        MccValueSet valueSet = ValueSetManager.getValueSetManager().findValueSet(valueSetId);

        String callUrl = null;
        if (bUseCategory)
        {
            callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns.use_category", values, webRequest);
        }
        else if (bUseValueSet)
        {
            callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns.use_valueset", values, webRequest);
        }


        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            //Bundle results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId))
            //        .and(Condition.CATEGORY.exactly().code("health-concern")).returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Condition")==0) {
                    if (bUseValueSet)
                    {
                        //Verify the conditoin is in the value set
                    }
                    Condition c = (Condition) e.getResource();
                    addCondtionToConditionList(out, c, ctx);
                }
            }
        }
        out.categorizeConditions(bUseCategory, bUseValueSet, valueSet);
        return out;
    }


    private void addCondtionToConditionList(ConditionLists list, Condition c, Context ctx)
    {
        list.addCondition(c, ctx);
    }

    @GetMapping("/socialconcerns")
    public SocialConcern[] getCarePlans(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<SocialConcern> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
        //TODO: Query for concerns
        //Bundle results = client.search().forResource(CarePlan.class).where(CarePlan.SUBJECT.hasId(subjectId))
        //        .returnBundle(Bundle.class).execute();
        //for (Bundle.BundleEntryComponent e : results.getEntry()) {
        //    if (e.getResource().fhirType() == "CarePlan") {
        //        CarePlan c = (CarePlan) e.getResource();
        //        out.add(mapCarePlan(c, client, ctx));
        //    }
        //}
        /*
        out.add(new SocialConcern("Food Security"));
        out.add(new SocialConcern("Transportation Access"));
        out.add(new SocialConcern("Housing Stability"));
        out.add(new SocialConcern("Primary Language"));
        out.add(new SocialConcern("Health Insurance Status/Type"));
        out.add(new SocialConcern("History of Abuse"));
        out.add(new SocialConcern("Computer/Phone Access"));
        out.add(new SocialConcern("Alcohol Abuse"));
        out.add(new SocialConcern("Substance Abuse"));
        out.add(new SocialConcern("Caregiver Characteristics"));
        out.add(new SocialConcern("Characteristic of Home Environment"));
        out.add(new SocialConcern("Employment Status"));
        out.add(new SocialConcern("Education Level"));
        out.add(new SocialConcern("Environmental Conditions"));
`       */
        MccValueSet valueSet = ValueSetManager.getValueSetManager().findValueSet(valueSetId);
        ConditionLists concerns = new ConditionLists();

        Map<String, String> values = new HashMap<>();
        String callUrl = null;
        if (bUseCategory)
        {
            callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns.ByCategory", values, webRequest);
        }
        else if (bUseValueSet)
        {
            callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns.ForValueSet", values, webRequest);
        }

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            ///Bundle results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId))
            //    .and(Condition.CATEGORY.exactly().code("health-concern")).returnBundle(Bundle.class).execute();

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Condition")==0) {
                    Condition c = (Condition) e.getResource();
                    if (bUseValueSet)
                    {
                        //Verify the condition is in the value set
                        if (c.hasCode()) {
                            if (!MCC2HFHIRHelper.conceptInValueSet(c.getCode(),valueSet))
                            {
                                //Skip - this concept is not one we consider to be a referral
                                continue;
                            }
                        }
                    }
                    addCondtionToConditionList(concerns, c, ctx);
                }
            }
        }
        concerns.categorizeConditions(bUseCategory, bUseValueSet, valueSet);
        SocialConcern[] outA = new SocialConcern[out.size()];
        for (ConditionSummary s : concerns.getActiveConcerns())
        {
            SocialConcern crn = new SocialConcern();
            crn.setName(s.getCode().getText());
            crn.setDate(s.getFirstOnset());
            crn.setData(s.getClinicalStatus());
            out.add(crn);
        }
        for (ConditionSummary s : concerns.getInactiveConcerns())
        {
            SocialConcern crn = new SocialConcern();
            crn.setName(s.getCode().getText());
            crn.setDate(s.getFirstOnset());
            crn.setData(s.getClinicalStatus());
            out.add(crn);
        }
        outA = out.toArray(outA);
        return outA;
    }

}
