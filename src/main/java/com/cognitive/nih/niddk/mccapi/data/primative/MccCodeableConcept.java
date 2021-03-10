package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data class MccCodeableConcept implements MccType {
    public static final String fhirType = "CodeableConcept";
    private MccCoding[] coding;
    @NotBlank
    private String text;

    @JsonIgnore
    public String getKey(String defSystem)
    {
        String out = "Unknown";
        MccCoding fndCode=null;
        if (!StringUtils.isEmpty(defSystem))
        {
            //Search the codings
            for (MccCoding cd: coding)
            {
                if (cd.getSystem().compareTo(defSystem)==0)
                {
                    fndCode = cd;
                    break;
                }
            }
        }
        if (fndCode == null)
        {
            //Default to first code
            fndCode=coding[0];

        }

        if (fndCode != null) {
            out = fndCode.getKey(defSystem);
        }
        return out;
    }

    @JsonIgnore
    public MccCoding getCode(String system)
    {
        MccCoding fndCode=null;
        if (!StringUtils.isEmpty(system))
        {
            //Search the codings
            for (MccCoding cd: coding)
            {
                if (cd.getSystem().compareTo(system)==0)
                {
                    fndCode = cd;
                    break;
                }
            }
        }
        return fndCode;

    }
}
