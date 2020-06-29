package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.Context;


import java.util.HashMap;

public class ContextManager {
    private static ContextManager singleton= new ContextManager();
    public static ContextManager getManager() {return singleton;}

    //TODO: Replace with a cache
    HashMap<String, Context> subjectMap;

    public ContextManager(){
     subjectMap = new HashMap<>();
    }

    public Context findContextForSubject(String subjectId)
    {
        Context out;
        out = subjectMap.get(subjectId);
        if (out == null)
        {
            out = new Context();
            out.setSubjectId(subjectId);
            subjectMap.put(subjectId,out);
        }
        return out;
    }

    public void removeContext(Context ctx)
    {
        subjectMap.remove(ctx.getSubjectId());
    }
}
