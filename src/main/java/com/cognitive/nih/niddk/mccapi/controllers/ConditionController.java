package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.ConditionLists;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ConditionController {

    private final QueryManager queryManager;

    public ConditionController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    private void addConditionToConditionList(ConditionLists list, Condition c, Context ctx) {
        list.addCondition(c, ctx);
    }

    @GetMapping("/condition/{id}")
    public MccCondition getCondition(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccCondition c;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        values.put("id",id);
        String callUrl = queryManager.setupQuery("Condition.Lookup", values, webRequest);

        if (callUrl != null) {

            Condition fc = client.fetchResourceFromUrl(Condition.class, callUrl);

            //Condition fc = client.read().resource(Condition.class).withId(id).execute();
            if (fc == null) {
                throw new ItemNotFoundException(id);
            }
            String subjectId = fc.getSubject().getId();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            c = mapCondition(fc, client, ctx);
        } else {
            c = new MccCondition();
        }
        return c;
    }

    /**
     * @Deprecated Please use /summary/conditions
     * @param subjectId
     * @param careplanId
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/conditionsummary")
    public ConditionLists getConditionSummaryOld(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        return commonConditionSummary(subjectId,careplanId, headers, webRequest);
    }

    /**
     *
     * @param subjectId
     * @param careplanId
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/summary/conditions")
    public ConditionLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        return commonConditionSummary(subjectId,careplanId, headers, webRequest);
    }

    private ConditionLists commonConditionSummary(String subjectId, String careplanId,Map<String, String> headers, WebRequest webRequest) {
            ConditionLists out = new ConditionLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);

        log.info("Fetching condition summary");
        Map<String, String> values = new HashMap<>();
        //First we get the problem list items
        Bundle results;
        try {

            String callUrl = queryManager.setupQuery("Condition.QueryProblemList", values, webRequest);

            if (callUrl != null) {
                results = client.fetchResourceFromUrl(Bundle.class, callUrl);

                //results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId)).where(Condition.CATEGORY.exactly().systemAndValues("http://terminology.hl7.org/CodeSystem/condition-category", "problem-list-item"))
                //    .returnBundle(Bundle.class).execute();
                ctx.setClient(client);
                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Condition")==0) {
                        Condition c = (Condition) e.getResource();
                        addConditionToConditionList(out, c, ctx);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error fetching problem list items fetch condition summary");
        }
        //Now we try for Health concerns
        try {
            String callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns", values);

            if (callUrl != null) {
                results = client.fetchResourceFromUrl(Bundle.class, callUrl);

                //results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId)).where(Condition.CATEGORY.exactly().systemAndValues("http://hl7.org/fhir/us/core/CodeSystem/condition-category", "health-concern"))
                //   .returnBundle(Bundle.class).execute();
                ctx.setClient(client);
                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Condition")==0) {
                        Condition c = (Condition) e.getResource();
                        addConditionToConditionList(out, c, ctx);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error fetching Health Concerns during fetch of condition summary");
        }


        out.categorizeConditions();
        return out;
    }

    @GetMapping("/condition")
    public MccCondition[] getConditions(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccCondition> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Bundle results;
        Map<String, String> values = new HashMap<>();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);

        log.info("Fetching conditions");

        try {
            String callUrl = queryManager.setupQuery("Condition.QueryProblemList", values, webRequest);

            if (callUrl != null) {
                results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId)).where(Condition.CATEGORY.exactly().systemAndValues("http://terminology.hl7.org/CodeSystem/condition-category", "problem-list-item"))
                //        .returnBundle(Bundle.class).execute();
                ctx.setClient(client);
                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Condition") ==0) {
                        Condition c = (Condition) e.getResource();
                        out.add(mapCondition(c, client, ctx));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error fetching problem list items while fetching conditions");
        }

        ///Now try for health concerns
        try {
            String callUrl = queryManager.setupQuery("Condition.QueryHealthConcerns", values);

            if (callUrl != null) {
                results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //       results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId)).where(Condition.CATEGORY.exactly().systemAndValues("http://hl7.org/fhir/us/core/CodeSystem/condition-category", "health-concern"))
                //    .returnBundle(Bundle.class).execute();
                ctx.setClient(client);
                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Condition")==0) {
                        Condition c = (Condition) e.getResource();
                        out.add(mapCondition(c, client, ctx));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error fetching health concerns while fetching Conditions");
        }

        MccCondition[] outA = new MccCondition[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    private MccCondition mapCondition(Condition fc, IGenericClient client, Context ctx) {
        MccCondition c;
        c = ConditionMapper.fhir2local(fc, ctx);
        //Now deal with relationships

        /*
        //Start with Addresses
        int index = 0;
        StringBuilder addSum = new StringBuilder();
        List<Reference> addresses = fc.getAddresses();
        MccCondition[] mccAddrs = new MccCondition[addresses.size()];
        for(Reference reference: addresses)
        {
            String ref = reference.getReference();
            Condition add = client.read().resource(Condition.class).withUrl(ref).execute();
            if (index>0) addSum.append(",");
            addSum.append(Helper.getConceptDisplayString(add.getCode()));
            mccAddrs[index] = ConditionMapper.fhir2local(add);
        }
        c.setAddressesSummary(addSum.toString());
        c.setAddresses(mccAddrs);
        */

        return c;
    }
}
