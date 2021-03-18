package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.mappers.FHIRNormalizer;
import com.cognitive.nih.niddk.mccapi.mappers.IFHIRNormalizer;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ProfileManager {
    public static ProfileManager getSingleton() {
        return singleton;
    }

    private  static ProfileManager singleton = new ProfileManager();


    private static HashMap<String, String> profileMap = new HashMap<>();
    private static HashMap<String, String> systemMap = new HashMap<>();

    static {
        profileMap.put("2.16.840.1.113762.1.4.1222.159","CKD");
        systemMap.put("urn:oid:2.16.840.1.113883.6.96","http://snomed.info/sct" );
        systemMap.put("urn:oid:2.16.840.1.113883.6.88","http://www.nlm.nih.gov/research/umls/rxnorm");
        systemMap.put("urn:oid:2.16.840.1.113883.6.1","http://loinc.org" );
        systemMap.put("urn:oid:2.16.840.1.113883.6.3","http://hl7.org/fhir/sid/icd-10");
        systemMap.put("urn:oid:2.16.840.1.113883.6.42","http://hl7.org/fhir/sid/icd-9-cm" );
    }


    public String getProfilesForConcept(CodeableConcept concept, Context ctx)
    {
        //Look for more than one coding.
        StringBuilder out = new StringBuilder();
        IFHIRNormalizer fhirNormalizer=ctx.getMapper().getNormalizer();
        List<Coding> cds = concept.getCoding();
        for (Coding cd: cds) {
            //Translate the system if required
            fhirNormalizer.normalizeCoding(cd);
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

    public List<String> getProfilesForConceptAsList(CodeableConcept concept, Context ctx)
    {
        //Look for more than one coding.
        ArrayList<String> out = new ArrayList<>();
        IFHIRNormalizer fhirNormalizer=ctx.getMapper().getNormalizer();
        List<Coding> cds = concept.getCoding();
        for (Coding cd: cds) {
            //Translate the system if required
            fhirNormalizer.normalizeCoding(cd);
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
