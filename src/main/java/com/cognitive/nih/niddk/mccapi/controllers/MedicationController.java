package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MedicationLists;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MedicationController {

    @GetMapping("/medicationsummary")
    public MedicationLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestParam(required = false, name = "server") String serverId) {
        MedicationLists out = new MedicationLists();

        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());


        Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "MedicationRequest") {
                MedicationRequest mr = (MedicationRequest) e.getResource();
                out.addMedicationRequest(mr,ctx);
            }
        }

        results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "MedicationStatement") {
                MedicationStatement ms = (MedicationStatement) e.getResource();
                out.addMedicationStatement(ms,ctx);
            }
        }

        return out;
    }
}
