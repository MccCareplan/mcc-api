/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.controllers;


import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccQuestionnaireResponse;
import com.cognitive.nih.niddk.mccapi.data.QuestionnaireResponseSummary;
import com.cognitive.nih.niddk.mccapi.data.SimpleQuestionnaireItem;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.services.QuestionnaireResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
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
    private final IR4Mapper mapper;
    private final ObservationController observationController;
    private final ContextManager contextManager;
    private final boolean SERVICE_REQUEST_ENABLED = false;
    @Value("${mcc.patient.reported.data.use.questionnaires:true}")
    private String useQuestionnaires;
    private boolean bUseQuestionnaires = false;
    @Value("${mcc.patient.reported.data.use.observations:true}")
    private String useObservatiions;
    private boolean bUseObservations = false;

    public QuestionnaireResponseController(QueryManager queryManager, QuestionnaireResolver questionnaireResolver, IR4Mapper mapper, ObservationController observationController, ContextManager contextManager) {
        this.queryManager = queryManager;
        this.questionnaireResolver = questionnaireResolver;
        this.mapper = mapper;
        this.observationController = observationController;


        this.contextManager = contextManager;
    }

    @GetMapping("/find/latest/questionnaireresponse")
    public MccQuestionnaireResponse getLatestQuestionnaireResponse(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccQuestionnaireResponse out = null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

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
                                out = mapper.fhir2local(qr, ctx);
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
                            out = mapper.fhir2local(workArray.get(0), ctx);
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

    //TODO: Add General Questionnaire support (
    // including: /find/latest/questionnaire/?code=xxx
    //            /questionnaire/id
    //            /questionnaire?xxxxx

    @GetMapping("/find/latest/questionnaireresponseitem")
    public SimpleQuestionnaireItem getLatestQuestionnaireResponseForItem(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        SimpleQuestionnaireItem out = null;
        QuestionnaireResponse fnd = null;
        String key;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

        if (bUseQuestionnaires) {
            if (queryManager.doesQueryExist("Questionnaire.FindForCode")) {
                // find questionnaires for the code
                String questionnaireIds = questionnaireResolver.findQuestionnairesForCode(code, ctx);
                if (!StringUtils.isBlank(questionnaireIds)) {
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
                                        QuestionnaireResponseCache.put(key, fnd);
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
                                    QuestionnaireResponseCache.put(key, fnd);
                                }
                            } catch (Exception e) {
                                log.warn("Exception during QuestionnaireResponse.Query", e);
                            }
                        }
                    }
                }
            }
        }
        if (bUseObservations) {

            Map<String, String> obsValues = new HashMap<>();
            values.put("code", code);
            String baseQuery = "Observation.QueryLatest";
            ArrayList<Observation> list = observationController.QueryObservationsRaw(baseQuery, "code", client, subjectId, "descending",null,  webRequest, headers, obsValues);
            //OK we found some matching observations
            if (list.size() > 0) {
                Observation obs = list.get(0);

                //We have a questionnaire item that matched
                if (fnd != null) {
                    //OK We need top see if
                    if (fnd.hasAuthored() && obs.hasEffectiveDateTimeType()) {
                        DateTimeType obsDate = obs.getEffectiveDateTimeType();
                        if (FHIRHelper.compare(obsDate,fnd.getAuthoredElement()) >= 0)
                        {
                            out = ctx.getMapper().fhir2SimpleItem(obs, ctx, code);
                        }
                    }
                    else if (obs.hasEffectiveDateTimeType())
                    {
                        //So qe know now that the observation has a date and the questionnaire response does not
                        out = ctx.getMapper().fhir2SimpleItem(obs, ctx, code);
                    }
                    else if(!fnd.hasAuthored())
                    {
                        //If there is no date on either then we use the Observation. Otherwise we should drop thru and the questionnaire response should be used
                        out = ctx.getMapper().fhir2SimpleItem(obs, ctx, code);
                    }

                } else {

                    out = ctx.getMapper().fhir2SimpleItem(obs, ctx, code);
                }

            }
        }
        //TODO: Refactor this logic to make cleaner
        //Observations might se Out, if so any conflicts and mapping are already handled
        if (out == null) {
            if (fnd == null) {
                out = new SimpleQuestionnaireItem();
                out.setFHIRId("notfound");
            } else {
                out = mapper.fhir2SimpleItem(fnd, ctx, "/" + code);
            }
        }
        return out;

    }

    @GetMapping("/find/all/questionnaireresponseitems")
    public SimpleQuestionnaireItem[] getLatestQuestionnaireResponsesForItem(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "count", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "descending") String sortOrder, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<QuestionnaireResponse> workArray = new ArrayList<>();

        String key;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();

        Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

        if (queryManager.doesQueryExist("Questionnaire.FindForCode")) {
            // find questionnaires for the code
            String questionnaireIds = questionnaireResolver.findQuestionnairesForCode(code, ctx);
            if (!questionnaireIds.isEmpty()) {
                key = getReferenceKey(subjectId + "|" + questionnaireIds, ctx);

                values.put("ids", questionnaireIds);
                values.put("count", Integer.toString(maxItems));
                if (queryManager.doesQueryExist("QuestionnaireResponse.QueryWithOptions")) {
                    String callUrl = queryManager.setupQuery("QuestionnaireResponse.Query", values, webRequest);
                    try {
                        Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

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
                            if (sortOrder.compareTo("ascending") == 0) {
                                workArray.sort(comparator);
                            } else {
                                workArray.sort(comparator.reversed());
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception during QuestionnaireResponse.Query", e);
                    }
                }
            }
        }

        SimpleQuestionnaireItem out[] = new SimpleQuestionnaireItem[workArray.size()];
        int i = 0;
        for (QuestionnaireResponse r : workArray) {
            out[i] = mapper.fhir2SimpleItem(r, ctx, "/" + code);
            i++;
        }

        return out;

    }

    MccQuestionnaireResponse getNotFoundQuestionResponse() {
        MccQuestionnaireResponse out = new MccQuestionnaireResponse();
        out.setFHIRId("notfound");
        out.setStatus("notfound");
        return out;
    }

    @GetMapping("/questionnaireresponse/{id}")
    public MccQuestionnaireResponse getQuestionnaireResponse(@PathVariable(value = "id") String id, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccQuestionnaireResponse out = null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        values.put("id", id);

        String callUrl = queryManager.setupQuery("QuestionnaireResponse.Lookup", values, webRequest);

        if (callUrl != null) {
            QuestionnaireResponse qr = client.fetchResourceFromUrl(QuestionnaireResponse.class, callUrl);
            if (qr != null) {
                String subjectId = qr.getSubject().getId();
                Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);
                out = mapper.fhir2local(qr, ctx);
            }
        }

        if (out == null) {
            out = new MccQuestionnaireResponse();
            out.setFHIRId("notfound");
            out.setStatus("notfound");
        }
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
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                    QuestionnaireResponse qr = (QuestionnaireResponse) e.getResource();
                    if (qr.hasStatus()) {
                        String status = qr.getStatus().toCode();
                        Integer s = activeKeys.get(status);
                        if (s != null && s.intValue() != IGNORE) {
                            out.add(mapper.fhir2summary(qr, ctx));
                        }
                    }
                }
            }
        }


        QuestionnaireResponseSummary[] outA = new QuestionnaireResponseSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
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
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("QuestionnaireResponse") == 0) {
                    QuestionnaireResponse qr = (QuestionnaireResponse) e.getResource();
                    if (qr.hasStatus()) {
                        String status = qr.getStatus().toCode();
                        Integer s = activeKeys.get(status);
                        if (s != null && s.intValue() != IGNORE) {
                            out.add(mapper.fhir2local(qr, ctx));
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

    @PostConstruct
    public void init() {
        bUseQuestionnaires = Boolean.parseBoolean(useQuestionnaires);
        bUseObservations = Boolean.parseBoolean(useObservatiions);
        log.info("Config: mcc.patient.reported.data.use.questionnaires = " + useQuestionnaires);
        log.info("Config: mcc.patient.reported.data.use.observations = " + useObservatiions);
    }

}

