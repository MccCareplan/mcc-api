package com.cognitive.nih.niddk.mccapi.data;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;

import java.util.Date;
import java.util.HashMap;


public class Context {
    private String subjectId;
    private Date dob;
    private HashMap<String, String> headers;
    private Context parent;
    private IGenericClient client;

    public static final Context NULL_CONTEXT = new Context();

    public Context()
    {

    }
    public Context(Context parent)
    {
        this.parent = parent;
    }

    public String getSubjectId() {
        if (parent == null)
        {
            return parent.subjectId;
        }
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        if (parent == null)
        {
            this.subjectId = subjectId;
        }
        else
        {
            parent.setSubjectId(subjectId);
        }
    }

    public Date getDob() {
        if (parent == null)
        {
            return dob;
        }
        else
        {
            return parent.dob;
        }
    }

    public void setDob(Date dob) {
        if (parent == null)
        {
            this.dob = dob;
        }
        else
        {
            parent.dob = dob;
        }

    }
    //Right now all local
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public IGenericClient getClient() {
        if (this.client == null)
        {
            FHIRServices fhirSrv = FHIRServices.getFhirServices();
            this.client = fhirSrv.getClient(this.headers);
        }
        return client;
    }

    public void setClient(IGenericClient client) {
        this.client = client;
    }
}
