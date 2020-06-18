package com.cognitive.nih.niddk.mccapi.managers;

import com.cognitive.nih.niddk.mccapi.data.FHIRServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FHIRServerManager {
    private FHIRServer defaultFHIRServer;
    private HashMap<String, FHIRServer> serverRegistry;

    private static FHIRServerManager singleton = new FHIRServerManager();

    public FHIRServerManager() {
        serverRegistry = new HashMap<>();
        defineDefaultServers();
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

    private void defineDefaultServers() {
        FHIRServer srv = new FHIRServer();
        srv.setBaseURL("https://api.logicahealth.org/MCCeCarePlanTest/open");
        srv.setName("MMC eCarePlan Test");
        srv.setId("MCCeCarePlanTest");
        defaultFHIRServer = srv;
        addServer(srv);
        srv = new FHIRServer();
        srv.setBaseURL("https://api.logicahealth.org/MCCeCarePlanTest/data");
        srv.setName("MMC eCarePlan Test (Secure)");
        srv.setId("MCCeCarePlanTestSecure");
        addServer(srv);
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
