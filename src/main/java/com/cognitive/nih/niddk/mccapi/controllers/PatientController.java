package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.PatientMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PatientController {




    @GetMapping("/patient/{id}")
    public MccPatient getPatient(@PathVariable(value="id") String id, @RequestParam(required=false) String serverId)
    {
        //Create a dummy patient for the mnoment
        MccPatient p;
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(id);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Patient fp = client.read().resource(Patient.class).withId(id).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(id);
        p = PatientMapper.fhir2local(fp,ctx);
        return p;
    }
}
