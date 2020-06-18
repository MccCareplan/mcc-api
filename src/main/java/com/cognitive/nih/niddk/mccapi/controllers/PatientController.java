package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.PatientMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import org.hl7.fhir.r4.model.Patient;

@RestController
@CrossOrigin
public class PatientController {




    @GetMapping("/patient/{id}")
    public MccPatient getPatient(@PathVariable(value="id") String id, @RequestParam(required=false) String serverId)
    {
        //Create a dummy patient for the mnoment
        MccPatient p;
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(id);
        FhirContext ctx = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = ctx.newRestfulGenericClient(srv.getBaseURL());
        Patient fp = client.read().resource(Patient.class).withId(id).execute();
        p = PatientMapper.fhir2local(fp);
        return p;
    }
}
