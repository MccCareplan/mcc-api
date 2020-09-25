package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.mappers.PatientMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class PatientController {


    @GetMapping("/patient")
    public MccPatient[] getPatients(@RequestParam(required=true) String name, @RequestHeader Map<String, String> headers)
    {
        ArrayList<MccPatient> out = new ArrayList<>();
        //Create a dummy patient for the mnoment
        MccPatient p;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Bundle response = client.search().forResource(Patient.class).where(Patient.NAME.matches().value(name)).returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : response.getEntry()) {
            if (e.getResource().fhirType() == "Patient") {
                Patient bp = (Patient) e.getResource();
                Context ctx = ContextManager.getManager().findContextForSubject(bp.hasIdentifier()?bp.getIdentifierFirstRep().getValue():"Unknown",headers);
                ctx.setClient(client);
                p = PatientMapper.fhir2local(bp,ctx);
                out.add(p);
            }
        }

        MccPatient[] outA = new MccPatient[out.size()];
        outA = out.toArray(outA);
        return outA;

    }

    @GetMapping("/patient/{id}")
    public MccPatient getPatient(@PathVariable(value="id") String id, @RequestHeader Map<String, String> headers)
    {
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        MccPatient p;


        Patient fp = client.read().resource(Patient.class).withId(id).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(id,headers);
        ctx.setClient(client);
        p = PatientMapper.fhir2local(fp,ctx);
        return p;
    }


}
