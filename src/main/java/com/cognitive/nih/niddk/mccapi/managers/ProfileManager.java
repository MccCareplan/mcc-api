package com.cognitive.nih.niddk.mccapi.managers;

import org.hl7.fhir.r4.model.CodeableConcept;

public class ProfileManager {
    private static ProfileManager singlton = new ProfileManager();

    public static ProfileManager getProfileManager() {return singlton;}

    public String getProfileForConcept(CodeableConcept concept)
    {
        String out = null;
        //TODO: Value Set Test against each profile.
        //      First match wins

        if (concept.hasCoding("http://snomed.info/sct","709044004"))
        {
            out = "CKD";
        }
        return out;
    }
}
