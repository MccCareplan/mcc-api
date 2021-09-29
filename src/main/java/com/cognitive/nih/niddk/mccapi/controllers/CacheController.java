/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.controllers;

import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.services.QuestionnaireResolver;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class CacheController {

    private final QuestionnaireResolver questionnaireResolver;
    private final QueryManager queryManager;

    public CacheController(QuestionnaireResolver questionnaireResolver, QueryManager queryManager) {
        this.questionnaireResolver = questionnaireResolver;
        this.queryManager = queryManager;
    }


    @DeleteMapping("/cache")
    public void clearAllCaches() {
        clearCachedQuestionnaires();
        clearCachedReferences();
        clearCachedValueSets();
        clearQueryMappings();
    }

    @DeleteMapping("/cache/questionnaires")
    public void clearCachedQuestionnaires() {
        questionnaireResolver.clearCache();
        ReferenceResolver.clearQuestionnaireCache();
    }

    @DeleteMapping("/cache/references")
    public void clearCachedReferences() {
        ReferenceResolver.clearAllCaches();
    }

    @DeleteMapping("/cache/valuesets")
    public void clearCachedValueSets() {
        ValueSetManager.getValueSetManager().reloadAllValuesSets();
    }

    @DeleteMapping("/cache/querymappings")
    public void clearQueryMappings() {
        queryManager.reload();
    }
}
