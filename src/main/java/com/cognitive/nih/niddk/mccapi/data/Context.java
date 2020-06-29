package com.cognitive.nih.niddk.mccapi.data;

import java.util.Date;

public class Context {
    private String subjectId;
    private Date dob;

    public static final Context NULL_CONTEXT = new Context();

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
