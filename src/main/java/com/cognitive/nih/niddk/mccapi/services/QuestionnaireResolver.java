package com.cognitive.nih.niddk.mccapi.services;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.util.JavaHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class QuestionnaireResolver {

    private static Cache<String, String> questionnairesForCode = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private static Cache<String, Questionnaire> QuestionnaireCache= Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private final QueryManager queryManager;

    public QuestionnaireResolver(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    //Set of statuses that we use to search for results for.
    private static HashSet<String> activeQuestionnaireStatus = new HashSet<>();

    static {
        activeQuestionnaireStatus.add("active");
        activeQuestionnaireStatus.add("draft");
        activeQuestionnaireStatus.add("retired");
    }
    /**
     *
     * @param code
     * @param ctx
     * @return a String of the questionnaires that use the requested coce
     */
    public String findQuestionnairesForCode(String code, Context ctx)
    {
        String out;
        String key = getReferenceKey(code, ctx);
        out = questionnairesForCode.getIfPresent(key);
        if (out == null)
        {
            try {
                Map<String, String> values = new HashMap<>();
                values.put("code",code);
                String callUrl = queryManager.setupQuery("Questionnaire.FindForCode",values);
                Bundle results = ctx.getClient().fetchResourceFromUrl(Bundle.class, callUrl);
                StringBuilder ids = new StringBuilder();
                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Questionnaire") == 0) {
                        Questionnaire q = (Questionnaire) e.getResource();
                        String status = q.getStatus().toCode();
                        if (activeQuestionnaireStatus.contains(status)) {
                            String id = q.getIdElement().getIdPart();
                            JavaHelper.addStringToBufferWithSep(ids,id,",");
                        }
                    }
                }
                out = ids.toString();
                //We will cache an empty type
                if (out == null) out ="";
                questionnairesForCode.put(key,out);
            } catch (Exception e) {
                logException(code, e);
            }
        }
        return out;
    }

    public static void logException (String code, Exception e)
    {
        log.warn("Error retrieving questionnaires for code (" + code.toString() + ")", e);
    }


    public static String getReferenceKey(Reference ref, Context ctx)
    {
        StringBuilder key = new StringBuilder();
        key.append(ctx.getClient().getServerBase());
        key.append(":");
        key.append(ref.getReference());
        return key.toString();
    }

    public static String getReferenceKey(String ref, Context ctx)
    {
        StringBuilder key = new StringBuilder();
        key.append(ctx.getClient().getServerBase());
        key.append(":");
        key.append(ref);
        return key.toString();
    }

    public void clearCache()
    {
        questionnairesForCode.invalidateAll();
        questionnairesForCode.cleanUp();
    }
}
