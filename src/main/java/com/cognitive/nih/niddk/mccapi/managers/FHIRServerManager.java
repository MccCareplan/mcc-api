package com.cognitive.nih.niddk.mccapi.managers;

import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log
@Component
public class FHIRServerManager {

    @Value("${fhir.default.server.address:https://api.logicahealth.org/MCCeCarePlanTest/open}")
    private String defaultFHIRServerAddress;
    @Value("${fhir.secure.server.address:https://api.logicahealth.org/MCCeCarePlanTest/data}")
    private String defaultFHIRSecureAddress;

    @Value("${hapi.logging.enabled:false}")
    private String enableLogging;
    @Value("${hapi.logging.request.summary:true}")
    private String log_request_summary;
    @Value("${hapi.logging.request:body:false}")
    private String log_request_body;
    @Value("${hapi.logging.request.header:false}")
    private String log_request_header;
    @Value("${hapi.logging.response.summary:true}")
    private String log_response_summary;
    @Value("${hapi.logging.response.body:false}")
    private String log_response_body;
    @Value("${hapi.logging.response.header:false}")
    private String log_response_header;

    private FHIRServer defaultFHIRServer;

    private HashMap<String, FHIRServer> serverRegistry;

    public LoggingInterceptor getLoggingInterceptor() {
        return loggingInterceptor;
    }

    public boolean isEnableFHIRLogging() {
        return enableFHIRLogging;
    }

    private boolean enableFHIRLogging;

    private LoggingInterceptor loggingInterceptor;
    private static FHIRServerManager singleton; // = new FHIRServerManager();

    public FHIRServerManager() {
        serverRegistry = new HashMap<>();
        //defineDefaultServers();
    }

    public static FHIRServerManager getManager() {
        return singleton;
    }


    public FHIRServer getDefaultFHIRServer() {
        return defaultFHIRServer;
    }

    public void setDefaultFHIRServer(FHIRServer defaultFHIRServer) {
        this.defaultFHIRServer = defaultFHIRServer;
    }

    @PostConstruct
    private void defineDefaultServers() {
        FHIRServer srv = new FHIRServer();
        String env = System.getenv("DEFAULT_FHIR_SERVER");

        //srv.setBaseURL("https://api.logicahealth.org/MCCeCarePlanTest/open");
        srv.setBaseURL(defaultFHIRServerAddress);
        srv.setName("MMC eCarePlan Test");
        srv.setId("MCCeCarePlanTest");
        defaultFHIRServer = srv;
        log.info("Default FHIR Server = "+defaultFHIRServerAddress);
        addServer(srv);
        srv = new FHIRServer();
        //srv.setBaseURL("https://api.logicahealth.org/MCCeCarePlanTest/data");
        srv.setBaseURL(defaultFHIRSecureAddress);
        log.info("Default FHIR Secure Server = "+defaultFHIRSecureAddress);
        srv.setName("MMC eCarePlan Test (Secure)");
        srv.setId("MCCeCarePlanTestSecure");
        addServer(srv);

        if (singleton == null)
        {
            singleton = this;
        }

        enableFHIRLogging = Boolean.valueOf(enableLogging).booleanValue();

        if (enableFHIRLogging)
        {
            loggingInterceptor = new LoggingInterceptor();
            loggingInterceptor.setLogRequestBody(Boolean.valueOf(log_response_body).booleanValue());
            loggingInterceptor.setLogRequestHeaders(Boolean.valueOf(log_request_header).booleanValue());
            loggingInterceptor.setLogRequestSummary(Boolean.valueOf(log_request_summary).booleanValue());
            loggingInterceptor.setLogResponseSummary(Boolean.valueOf(log_response_summary).booleanValue());
            loggingInterceptor.setLogResponseBody(Boolean.valueOf(log_response_body).booleanValue());
            loggingInterceptor.setLogResponseHeaders(Boolean.valueOf(log_response_header).booleanValue());
        }


    }

    public FHIRServer getServer(String id) {
        return serverRegistry.get(id);
    }

    public FHIRServer getServerWithDefault(String id) {
        FHIRServer srv = null;
        if (id != null )
        {
            srv = serverRegistry.get(id);
        }
        if (srv == null)
        {
            srv = defaultFHIRServer;
        }
        return srv;
    }

    public void addServer(FHIRServer srv) {
        serverRegistry.put(srv.getId(), srv);
    }

    public void removeServer(FHIRServer srv) {
        serverRegistry.remove(srv.getId());
    }

    public List<FHIRServer> getServers() {
        return new ArrayList<FHIRServer>(serverRegistry.values());
    }



}
