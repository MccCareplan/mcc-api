package com.cognitive.nih.niddk.mccapi.controllers;

import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@RestController
public class FHIRServerController {

    @GetMapping("/fhirserver/{id}")
    public FHIRServer getServer(@PathVariable(value="id") String id)
    {
        FHIRServer srv = FHIRServerManager.getManager().getServer(id);
        return srv;
    }

    @GetMapping("/fhirserver")
    public List<FHIRServer> getServers()
    {
        return FHIRServerManager.getManager().getServers();
    }

}
