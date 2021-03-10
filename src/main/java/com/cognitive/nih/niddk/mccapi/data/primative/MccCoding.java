package com.cognitive.nih.niddk.mccapi.data.primative;

import com.cognitive.nih.niddk.mccapi.data.MccType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
public @Data
class MccCoding implements MccType {
    public static final String fhirType = "Coding";
    private String system;
    private String version;
    @NotBlank
    private String code;
    private String display;

    @JsonIgnore public String getKey(String defSystem)
    {
        StringBuilder key = new StringBuilder();
        if (!StringUtils.isEmpty(system))
        {
            key.append(system);
        }
        else
        {
            key.append(defSystem);
        }
        if (!StringUtils.isEmpty(code))
        {
            key.append("|");
            key.append(code);
        }
       return key.toString();
    }
}
