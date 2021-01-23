package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccQuestionnaireResponse;
import com.cognitive.nih.niddk.mccapi.data.QuestionnaireResponseSummary;
import com.cognitive.nih.niddk.mccapi.data.SimpleQuestionnaireItem;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.QuestionnaireResponseMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.services.QuestionnaireResolver;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class QuestionnaireResponseController {
    private static final int ACTIVE_LIST = 0;
    private static final int INACTIVE_LIST = 1;
    private static final int IGNORE = 2;
    private static final HashMap<String, Integer> activeKeys = new HashMap<>();

    private static Cache<String, QuestionnaireResponse> QuestionnaireResponseCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    static {
        //Hash as verified
        Integer active = Integer.valueOf(ACTIVE_LIST);
        Integer inactive = Integer.valueOf(INACTIVE_LIST);
        Integer ignore = Integer.valueOf(IGNORE);
        //	active | recurrence | relapse
        activeKeys.put("in-progress", active);
        activeKeys.put("amended", active);
        activeKeys.put("completed", active);

        activeKeys.put("stopped", inactive);
        activeKeys.put("entered-in-error", ignore);
        activeKeys.put("unknown", ignore);

    }

    private final QueryManager queryManager;
    private final QuestionnaireResolver questionnaireResolver;
    private final boolean SERVICE_REQUEST_ENABLED = false;


    public QuestionnaireResponseController(QueryManager queryManager, QuestionnaireResolver questionnaireResolver) {
        this.queryManager = queryManager;

        this.questionnaireResolver = questionnaireResolver;
    }

    //TODO: Add General Questionnaire support (
    // including: /find/latest/questionnaire/?code=xxx
    //            /questionnaire/id
    //            /questionnaire?xxxxx


    @GetMapping("/find/latest/questionnaireresponse")
    public MccQuestionnaireResponse getLatestQuestionnaireResponse(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccQuestionnaireResponse out = null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);

        if (queryManager.doesQueryExist("Questionnaire.FindForCode")) {
            // find questionnaires for the code
            String questionnaireIds = questionnaireResolver.findQuestionnairesForCode(code, ctx);
            if (!questionnaireIds.isEmpty()) {

                // Search for QuestionnaireResponses for this subject
                values.put("ids", questionnaireIds);
                if (queryManager.doesQueryExist("QuestionnaireResponse.QueryLatest")) {
                    //We have a query that will get of the latest completed or amended
                    String callUrl = queryManager.setupQuery("QuestionnaireResponse.QueryLatest", values, webRequest);
                    try {
                        Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                        if ((results.getEntry().size() > 0)) {
                            Bundle.BundleEntryComponent e = results.getEntryFirstRep();
                            if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                                QuestionnaireResponse qr = (QuestionnaireResponse) e.getResource();
                                out = QuestionnaireResponseMapper.fhir2local(qr, ctx);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception during QuestionnaireResponse.QueryLatest", e);
                    }
                } else if (queryManager.doesQueryExist("QuestionnaireResponse.Query")) {
                    String callUrl = queryManager.setupQuery("QuestionnaireResponse.Query", values, webRequest);
                    try {
                        Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                        ArrayList<QuestionnaireResponse> workArray = new ArrayList<>();

                        //Filter
                        for (Bundle.BundleEntryComponent e : results.getEntry()) {
                            if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                                QuestionnaireResponse o = (QuestionnaireResponse) e.getResource();
                                if (activeKeys.get(o.getStatus().toCode()) == ACTIVE_LIST) {
                                    //We require an authored date
                                    if (o.hasAuthored()) {
                                        workArray.add(o);
                                    }
                                }
                            }
                        }
                        //Sort the results
                        if (workArray.size() > 1) {
                            Comparator<QuestionnaireResponse> comparator = (QuestionnaireResponse o1, QuestionnaireResponse o2) -> o1.getAuthored().compareTo(o2.getAuthored());
                            workArray.sort(comparator.reversed());
                        }
                        if (workArray.size() > 0) {
                            out = QuestionnaireResponseMapper.fhir2local(workArray.get(0), ctx);
                        }
                    } catch (Exception e) {
                        log.warn("Exception during QuestionnaireResponse.Query", e);
                    }
                }
            }
        }

        if (out == null) {
            out = getNotFoundQuestionResponse();
        }

        return out;

    }

    @GetMapping("/find/latest/questionnaireresponseitem")
    public SimpleQuestionnaireItem getLatestQuestionnaireResponseForItem(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        SimpleQuestionnaireItem out = null;
        QuestionnaireResponse fnd = null;
        String key;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);

        if (queryManager.doesQueryExist("Questionnaire.FindForCode")) {
            // find questionnaires for the code
            String questionnaireIds = questionnaireResolver.findQuestionnairesForCode(code, ctx);
            if (!questionnaireIds.isEmpty()) {
                key = getReferenceKey(subjectId + "|" + questionnaireIds, ctx);

                fnd = QuestionnaireResponseCache.getIfPresent(key);

                if (fnd == null) {
                    // Search for QuestionnaireResponses for this subject
                    values.put("ids", questionnaireIds);
                    if (queryManager.doesQueryExist("QuestionnaireResponse.QueryLatest")) {
                        //We have a query that will get of the latest completed or amended
                        String callUrl = queryManager.setupQuery("QuestionnaireResponse.QueryLatest", values, webRequest);
                        try {
                            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                            if ((results.getEntry().size() > 0)) {
                                Bundle.BundleEntryComponent e = results.getEntryFirstRep();
                                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                                    fnd = (QuestionnaireResponse) e.getResource();
                                    QuestionnaireResponseCache.put(key,fnd);
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Exception during QuestionnaireResponse.QueryLatest", e);
                        }
                    } else if (queryManager.doesQueryExist("QuestionnaireResponse.Query")) {
                        String callUrl = queryManager.setupQuery("QuestionnaireResponse.Query", values, webRequest);
                        try {
                            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                            ArrayList<QuestionnaireResponse> workArray = new ArrayList<>();

                            //Filter
                            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                                    QuestionnaireResponse o = (QuestionnaireResponse) e.getResource();
                                    if (activeKeys.get(o.getStatus().toCode()) == ACTIVE_LIST) {
                                        //We require an authored date
                                        if (o.hasAuthored()) {
                                            workArray.add(o);
                                        }
                                    }
                                }
                            }
                            //Sort the results
                            if (workArray.size() > 1) {
                                Comparator<QuestionnaireResponse> comparator = (QuestionnaireResponse o1, QuestionnaireResponse o2) -> o1.getAuthored().compareTo(o2.getAuthored());
                                workArray.sort(comparator.reversed());
                            }
                            if (workArray.size() > 0) {
                                fnd = workArray.get(0);
                                QuestionnaireResponseCache.put(key,fnd);
                            }
                        } catch (Exception e) {
                            log.warn("Exception during QuestionnaireResponse.Query", e);
                        }
                    }
                }
            }
        }

        if (fnd == null) {
            out = new SimpleQuestionnaireItem();
            out.setFHIRId("notfound");
        } else {
            out = QuestionnaireResponseMapper.fhir2SimpleItem(fnd, ctx, "/" + code);
        }

        return out;

    }

    MccQuestionnaireResponse getNotFoundQuestionResponse() {
        MccQuestionnaireResponse out = new MccQuestionnaireResponse();
        out.setFHIRId("notfound");
        out.setStatus("notfound");
        return out;
    }

    @GetMapping("/summary/questionnaireresponses")
    public QuestionnaireResponseSummary[] getQuestionnaireResponseSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<QuestionnaireResponseSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        String callUrl = queryManager.setupQuery("QuestionnaireResponse.Summary.Query", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                    QuestionnaireResponse qr = (QuestionnaireResponse) e.getResource();
                    if (qr.hasStatus()) {
                        String status = qr.getStatus().toCode();
                        Integer s = activeKeys.get(status);
                        if (s != null && s.intValue() != IGNORE) {
                            out.add(QuestionnaireResponseMapper.fhir2summary(qr, ctx));
                        }
                    }
                }
            }
        }



        QuestionnaireResponseSummary[] outA = new QuestionnaireResponseSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }
    @GetMapping("/questionnaireresponse/{id}")
    public MccQuestionnaireResponse getQuestionnaireResponse(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccQuestionnaireResponse out=null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        values.put("id",id);

        String callUrl = queryManager.setupQuery("QuestionnaireResponse.Lookup", values, webRequest);

        if (callUrl != null) {
            QuestionnaireResponse qr = client.fetchResourceFromUrl(QuestionnaireResponse.class, callUrl);
            if (qr != null)
            {
                String subjectId = qr.getSubject().getId();
                Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
                out= QuestionnaireResponseMapper.fhir2local(qr, ctx);
            }
        }

        if (out == null)
        {
            out = new MccQuestionnaireResponse();
            out.setFHIRId("notfound");
            out.setStatus("notfound");
        }
        return out;
    }
    @GetMapping("/questionnaireresponse")
    public MccQuestionnaireResponse[] getQuestionnaireResponses(@RequestParam(required = true, name = "subject") String subjectId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccQuestionnaireResponse> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        String callUrl = queryManager.setupQuery("QuestionnaireResponse.OpenQuery", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(Goal.class).where(Goal.SUBJECT.hasId(subjectId))
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                    QuestionnaireResponse qr = (QuestionnaireResponse) e.getResource();
                    if (qr.hasStatus()) {
                        String status = qr.getStatus().toCode();
                        Integer s = activeKeys.get(status);
                        if (s != null && s.intValue() != IGNORE) {
                            out.add(QuestionnaireResponseMapper.fhir2local(qr, ctx));
                        }
                    }
                }
            }
        }



        MccQuestionnaireResponse[] outA = new MccQuestionnaireResponse[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    public String getReferenceKey(String ref, Context ctx) {
        StringBuilder key = new StringBuilder();
        key.append(ctx.getClient().getServerBase());
        key.append(":");
        key.append(ref);
        return key.toString();
    }

}

