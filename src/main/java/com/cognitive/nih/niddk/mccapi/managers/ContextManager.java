package com.cognitive.nih.niddk.mccapi.managers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.exception.FHIRServerHeaderMissingException;
import com.cognitive.nih.niddk.mccapi.exception.FHIRTokenHeaderMissingException;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ContextManager {
    //private static ContextManager singleton= new ContextManager();
    //public static ContextManager getManager() {return singleton;}

    //TODO: Replace with a cache
    HashMap<String, Context> subjectMap;
    @Value("${mcc.request.header.require.server:false}")
    private String headerRequireServer;
    @Value("${mcc.request.header.require.token:false}")
    private String headerRequireToken;
    private boolean reqToken = false;
    private boolean reqServer = false;

    private static String headerServer = "mcc-fhir-server";
    private static String headerToken = "mcc-token";

    public ContextManager(){
     subjectMap = new HashMap<>();
    }

    @PostConstruct
    public void init()
    {
        reqToken = Boolean.parseBoolean(headerRequireToken);
        log.info("Config: mcc.request.header.require.token = "+reqToken);
        reqServer = Boolean.parseBoolean(headerRequireServer);
        log.info("Config: mcc.request.header.require.server = "+reqServer);
    }

    public Context setupContext(String subjectId, IGenericClient client, IR4Mapper mapper, Map<String, String> headers)
    {
        Context out = null;
        Context fnd = null;

        //Enfor
        if (reqServer && !headers.containsKey(headerServer))
        {
            throw new FHIRServerHeaderMissingException();
        }
        if (reqToken && !headers.containsKey(headerToken))
        {
            throw new FHIRTokenHeaderMissingException();
        }

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

        out.setClient(client);
        out.setMapper(mapper);


        return out;
    }

    private static HashMap<String, String> copyHeaders(Map<String,String> in)
    {
        HashMap<String, String> out = new HashMap<String, String>();
        copyHeader("mcc-fhir-server", in, out);
        copyHeader("mcc-token", in, out);
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
