package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.Context;


import java.util.HashMap;
import java.util.Map;

public class ContextManager {
    private static ContextManager singleton= new ContextManager();
    public static ContextManager getManager() {return singleton;}

    //TODO: Replace with a cache
    HashMap<String, Context> subjectMap;

    public ContextManager(){
     subjectMap = new HashMap<>();
    }

    public Context findContextForSubject(String subjectId, Map<String, String> headers)
    {
        Context out = null;
        Context fnd = null;
        //Deal with calls with no subject - Which get a unique context

        if (subjectId != null) {
            fnd = subjectMap.get(subjectId);

            if (fnd == null) {
                out = new Context();
                out.setSubjectId(subjectId);
                subjectMap.put(subjectId, out);
            }
            else
            {
                out = new Context(fnd);
                out.setHeaders(copyHeaders(headers));
            }
        }

        if (out == null )
        {
            out = new Context();
            out.setHeaders(copyHeaders(headers));
        }



        return out;
    }

    private static HashMap<String, String> copyHeaders(Map<String,String> in)
    {
        HashMap<String, String> out = new HashMap<String, String>();
        copyHeader("mcc-fhir-server", in, out);
        copyHeader("mcc-fhir-token", in, out);
        return out;
    }
    private static void copyHeader(String header, Map<String,String> in, Map<String ,String> out)
    {
        if (in.containsKey(header))
        {
            out.put(header,in.get(header));
        }
    }
    public void removeContext(Context ctx)
    {
        subjectMap.remove(ctx.getSubjectId());
    }
}
