/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class FHIRNormalizer implements IFHIRNormalizer {

    private static HashMap<String, String> systemMap = new HashMap<>();

    static {
        systemMap.put("urn:oid:2.16.840.1.113883.6.96","http://snomed.info/sct" );
        systemMap.put("urn:oid:2.16.840.1.113883.6.88","http://www.nlm.nih.gov/research/umls/rxnorm");
        systemMap.put("urn:oid:2.16.840.1.113883.6.1","http://loinc.org" );
        systemMap.put("urn:oid:2.16.840.1.113883.6.3","http://hl7.org/fhir/sid/icd-10");
        systemMap.put("urn:oid:2.16.840.1.113883.6.42","http://hl7.org/fhir/sid/icd-9-cm" );
    }

    public boolean requiresNormalization(Coding cd)
    {
        return systemMap.containsKey(cd.getSystem());
    }

    public Coding copyCodingNormalized(Coding cd)
    {
        Coding out = cd.copy();
        String system = out.getSystem();
        if (system != null && systemMap.containsKey(system))
        {
            out.setSystem(systemMap.get(system));
        }
        return out;
    }
    public void normalizeCoding(Coding cd)
    {
        String system = cd.getSystem();
        if (system != null && systemMap.containsKey(system))
        {
            cd.setSystem(systemMap.get(system));
        }
    }
}
