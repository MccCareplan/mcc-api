/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FHIRServices {

    private final FhirContext stu4Context;

    private static final FHIRServices singleon = new FHIRServices();

    public static FHIRServices getFhirServices() {
        return singleon;
    }

    public FHIRServices() {

        stu4Context = FhirContext.forR4();
        FHIRServerManager srvMgr = FHIRServerManager.getManager();
        stu4Context.getRestfulClientFactory().setConnectTimeout(srvMgr.connTimeout);
        stu4Context.getRestfulClientFactory().setSocketTimeout(srvMgr.reqTimeout);
    }

    public FhirContext getR4Context() {
        return stu4Context;
    }


    public IGenericClient getClient(Map<String, String> headers) {
        IGenericClient client;
        FHIRServerManager srvMgr = FHIRServerManager.getManager();
        //FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();

        if (headers.containsKey("mcc-fhir-server")) {
            String server = headers.get("mcc-fhir-server");
            log.info("Server is " + server);
            client = stu4Context.newRestfulGenericClient(server);
            if (headers.containsKey("mcc-token")) {
                BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(headers.get("mcc-token"));
                client.registerInterceptor(authInterceptor);
            }
        } else {
            log.warn("No Server provided - using default");
            FHIRServer srv = srvMgr.getDefaultFHIRServer();
            client = stu4Context.newRestfulGenericClient(srv.getBaseURL());
        }
        if (srvMgr.isEnableFHIRLogging()) {
            client.registerInterceptor(srvMgr.getLoggingInterceptor());
        }
        return client;
    }
}
