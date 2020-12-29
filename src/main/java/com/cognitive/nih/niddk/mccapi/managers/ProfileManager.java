package com.cognitive.nih.niddk.mccapi.managers;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileManager {
    private static ProfileManager singlton = new ProfileManager();

    public static ProfileManager getProfileManager() {return singlton;}

    private static HashMap<String, String> profileMap = new HashMap<>();

    static {
        profileMap.put("2.16.840.1.113762.1.4.1222.159","CKD");
    }
    public String getProfilesForConcept(CodeableConcept concept)
    {
        //Look for more than one coding.
        StringBuilder out = new StringBuilder();

        List<Coding> cds = concept.getCoding();
        for (Coding cd: cds) {
            ArrayList<String> match = ValueSetManager.getValueSetManager().findCodesValuesSets(cd.getSystem(), cd.getCode());

            if (match != null) {

                for (String vs : match) {
                    if (profileMap.containsKey(vs)) {
                        if (out.length() > 0) {
                            out.append(",");
                        }
                        out.append(profileMap.get(vs));
                    }
                }
                //We have a match so we stop looking
                break;
            }
        }
        if (out.length()==0)
        {
            return null;
        }
        return out.toString();
    }

    public List<String> getProfilesForConceptAsList(CodeableConcept concept)
    {
        //Look for more than one coding.
        ArrayList<String> out = new ArrayList<>();

        List<Coding> cds = concept.getCoding();
        for (Coding cd: cds) {
            ArrayList<String> match = ValueSetManager.getValueSetManager().findCodesValuesSets(cd.getSystem(), cd.getCode());

            if (match != null) {

                for (String vs : match) {
                    if (profileMap.containsKey(vs)) {
                        out.add(profileMap.get(vs));
                    }
                }
                //We have a match so we stop looking
                break;
            }
        }
        return out;
    }
}
