package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.mappers.ObservationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ObservationController {

        @GetMapping("/find/latest/observation")
        public MccObservation getLatestObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers) {

            MccObservation out = null;

            FHIRServices fhirSrv = FHIRServices.getFhirServices();
            IGenericClient client = fhirSrv.getClient(headers);

            //Eqv query: {{Server}}/Observation/?_sort=-date&_count=1&subject={{subject}}&code={{code}}
            Bundle results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.CODE.exactly().code(code)).sort().descending("date").count(1)
                    .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
            //Check if we did not find anything if not then see if maybe it is in an observation.component
            if (results.getTotal() == 0)
            {
                results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.COMPONENT_CODE.exactly().code(code)).sort().descending("date").count(1)
                        .returnBundle(Bundle.class).execute();
            }


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
