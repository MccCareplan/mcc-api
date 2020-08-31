package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.ObservationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@CrossOrigin

public class ObservationController {

        @GetMapping("/find/latest/observation")
        public MccObservation getLatestObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(required = false, name = "server") String serverId) {

            MccObservation out = null;

            FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
            FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
            IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
            //Eqv query: {{Server}}/Observation/?_sort=-date&_count=1&subject={{subjevct}}&code={{code}}
            Bundle results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.CODE.exactly().code(code)).sort().descending("date").count(1)
                    .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Observation")==0) {
                    Observation o = (Observation) e.getResource();
                    out = ObservationMapper.fhir2local(o,ctx);
                }
            }
            if (out == null)
            {
                throw new ItemNotFoundException(code);
            }
            return out;
        }

}
