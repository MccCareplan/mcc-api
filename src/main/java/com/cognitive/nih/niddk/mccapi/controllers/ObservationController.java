package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.mappers.ObservationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ObservationController {
    private final QueryManager queryManager;

    public ObservationController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @GetMapping("/find/latest/observation")
    public MccObservation getLatestObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        MccObservation out = null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        String callUrl = queryManager.setupQuery("Observation.QueryLatest", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            //Eqv query: {{Server}}/Observation/?_sort=-date&_count=1&subject={{subject}}&combo-code={{code}}
            //Bundle results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.COMBO_CODE.exactly().code(code)).sort().descending("date").count(1)
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);


            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("Observation") == 0) {
                    Observation o = (Observation) e.getResource();
                    out = ObservationMapper.fhir2local(o, ctx);
                }
            }
        }
        if (out == null) {
            throw new ItemNotFoundException(code);
        }
        return out;
    }

    @GetMapping("/observations")
    public MccObservation[] getObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "count", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        MccObservation[] out = null;

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Map<String, String> values = new HashMap<>();
        values.put("count",Integer.toString(maxItems));
        String callUrl = queryManager.setupQuery("Observation.Query", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            //Eqv query: {{Server}}/Observation/?_sort=-date&_count=1&subject={{subject}}&code={{code}}
            // Bundle results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.COMBO_CODE.exactly().code(code)).sort().descending("date").count(maxItems)
            //         .returnBundle(Bundle.class).execute();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);

            int fnd = results.getTotal();

            if (fnd != 0) {
                out = new MccObservation[fnd];
                if (sortOrder.compareTo("ascending") == 0) {
                    fnd--;
                    for (Bundle.BundleEntryComponent e : results.getEntry()) {
                        if (e.getResource().fhirType().compareTo("Observation") == 0) {
                            Observation o = (Observation) e.getResource();
                            //We resort
                            out[fnd] = ObservationMapper.fhir2local(o, ctx);
                            fnd--;
                        }
                    }
                } else {
                    int c = 0;
                    for (Bundle.BundleEntryComponent e : results.getEntry()) {
                        if (e.getResource().fhirType().compareTo("Observation") == 0) {
                            Observation o = (Observation) e.getResource();
                            //We resort
                            out[c] = ObservationMapper.fhir2local(o, ctx);
                            c++;
                        }
                    }

                }
            } else {
                throw new ItemNotFoundException(code);
            }
        } else {
            //TODO: Deal with suppressed query return
            log.info("Observation Query suppressed by override");
        }
        return out;
    }

    @GetMapping("/observationsbyvalueset")
    public MccObservation[] getObservationsByValueSet(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "valueset") String valueset, @RequestParam(name = "max", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        MccObservation[] out = new MccObservation[0];

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);


        //Right now this search is using the local exapnded version of the value set
        //If the server supports code:in then it would be better to use that feature
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(valueset);
        if (valueSetObj != null) {

            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count",Integer.toString(maxItems));
            String callUrl = queryManager.setupQuery("Observation.QueryValueSetExpanded", values, webRequest);

            if (callUrl != null) {
                Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //String codes = valueSetObj.asQueryString();
                //String url = "/Observation/?_sort=-date&_count=" + Integer.toString(maxItems) + "&subject=" + subjectId + "&combo-code=" + codes;
                //Eqv query: {{Server}}/Observation/?_sort=-date&_count=1&subject={{subject}}&code={{code}}
                //Bundle results = client.fetchResourceFromUrl(Bundle.class, url);
                //Bundle results = client.search().forResource(Observation.class).where(Observation.SUBJECT.hasId(subjectId)).and(Observation.CODE.exactly().code(codes)).sort().descending("date").count(maxItems)
                //        .returnBundle(Bundle.class).execute();
                Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
                ctx.setClient(client);


                int fnd = results.getTotal();
                if (fnd != 0) {
                    out = new MccObservation[fnd];
                    if (sortOrder.compareTo("ascending") == 0) {
                        fnd--;
                        for (Bundle.BundleEntryComponent e : results.getEntry()) {
                            if (e.getResource().fhirType().compareTo("Observation") == 0) {
                                Observation o = (Observation) e.getResource();
                                //We resort
                                out[fnd] = ObservationMapper.fhir2local(o, ctx);
                                fnd--;
                            }
                        }
                    } else {
                        int c = 0;
                        for (Bundle.BundleEntryComponent e : results.getEntry()) {
                            if (e.getResource().fhirType().compareTo("Observation") == 0) {
                                Observation o = (Observation) e.getResource();
                                //We resort
                                out[c] = ObservationMapper.fhir2local(o, ctx);
                                c++;
                            }
                        }

                    }
                }
            } else {
                log.info("Observation.QueryValueSetExpanded suppressed");
            }
        } else {
            throw new ItemNotFoundException(valueset);

        }
        return out;
    }

}
