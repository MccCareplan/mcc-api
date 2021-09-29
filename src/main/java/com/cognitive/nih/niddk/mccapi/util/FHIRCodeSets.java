/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.util;

import java.util.HashSet;
import java.util.Set;

public class FHIRCodeSets {
    public static final String CONDITION_CATEGORY_US_CORE =  "http://hl7.org/fhir/us/core/CodeSystem/condition-category";
    public static final String CONDITION_CATEGORY = "http://terminology.hl7.org/CodeSystem/condition-category";

    public static Set<String> US_CORE_CONDITION_CATEGORY_SET;

    static
    {
        US_CORE_CONDITION_CATEGORY_SET = new HashSet<String>();
        US_CORE_CONDITION_CATEGORY_SET.add(CONDITION_CATEGORY_US_CORE);
        US_CORE_CONDITION_CATEGORY_SET.add(CONDITION_CATEGORY);
    }
}
