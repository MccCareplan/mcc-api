package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MedicationLists;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MedicationController {

    @GetMapping("/medicationsummary")
    public MedicationLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers) {
        MedicationLists out = new MedicationLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);


        Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "MedicationRequest") {
                MedicationRequest mr = (MedicationRequest) e.getResource();
                out.addMedicationRequest(mr, ctx);
            }
        }

        results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "MedicationStatement") {
                MedicationStatement ms = (MedicationStatement) e.getResource();
                out.addMedicationStatement(ms, ctx);
            }
        }

        //results = client.fetchResourceFromUrl(Bundle.class,"/MedicationRequest?subject=cc-pat-pnoelle");
        return out;
    }
}
