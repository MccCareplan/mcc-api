/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
public class Demographics {
    Date dob;
    String gender;
    private String Race;
    private String Ethnicity;
    private String Id;

    public int calculateAge(Date when)
    {
        int years =0;
        if (dob != null) {
            LocalDate birth = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate chkWhen = when.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            years = java.time.Period.between(birth, chkWhen).getYears();
        }
        return years;
    }

}
