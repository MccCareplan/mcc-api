package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.cognitive.nih.niddk.mccapi.data.primative.GenericType;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.mappers.ObservationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ObservationController {
    private final QueryManager queryManager;

    public ObservationController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    private ArrayList<MccObservation> QueryObservations(String baseQuery, String mode, IGenericClient client, String subjectId, String sortOrder, WebRequest webRequest, Map<String, String> headers, Map<String, String> values) {
        ArrayList<MccObservation> out = new ArrayList<>();
        List<String> calls = getQueryStrings(baseQuery, mode);

        if (calls.size() > 0) {
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            ctx.setClient(client);

            for (String key : calls) {
                String callUrl = queryManager.setupQuery(key, values, webRequest);
                try {
                    Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                    //In general the we expect the return value to be in descending date order

                    for (Bundle.BundleEntryComponent e : results.getEntry()) {
                        if (e.getResource().fhirType().compareTo("Observation") == 0) {
                            Observation o = (Observation) e.getResource();
                            out.add(ObservationMapper.fhir2local(o, ctx));
                        }
                    }
                }
                catch(Exception e)
                {

                }
            }
            //Now we need possibly to sort the output
            if (sortOrder.compareTo("ascending") == 0) {
                //We need ascending order
                out.sort((MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective()));
            } else if (calls.size() > 1) {
                Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());

                //We need to merge multiples into one and sort descending
                out.sort(comparator.reversed());
            }

        } else {
            //TODO: Deal with suppressed query return
            log.info(baseQuery + " suppressed by override");
        }
        return out;
    }

    /**
     * Creates a compound query key
     *
     * @param query the Query Name
     * @param mode  the Mode (code, combo, panel, component)
     * @return A compound key
     */
    private String getKey(String query, String mode) {
        return query + "." + mode;
    }

    @GetMapping("/find/latest/observation")
    public MccObservation getLatestObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        ArrayList<MccObservation> list = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        String baseQuery = "Observation.QueryLatest";
        list = this.QueryObservations(baseQuery, mode, client, subjectId, "descending", webRequest, headers, values);

        if (list.size() == 0) {
            return getEmptyObservation(code,baseQuery);
        }

        return list.get(0);


    }

    @GetMapping("/find/latest/observationbyvalueset")
    public MccObservation getLatestObservationByValueSet(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "valueset") String valueset, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        ArrayList<MccObservation> list = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        String baseQuery = "Observation.QueryLatestValueSetExpanded";
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(valueset);
        if (valueSetObj != null) {

            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count", Integer.toString(1));


            list = this.QueryObservations(baseQuery, mode, client, subjectId, "descending", webRequest, headers, values);

        } else {
            log.warn("Search for non-existent values set: "+valueset);
        }


        if (list.size() == 0) {
            return getEmptyObservation(valueset,baseQuery);
        }

        return list.get(0);


    }
    @GetMapping("/observations")
    public MccObservation[] getObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "count", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestHeader Map<String, String> headers, WebRequest webRequest) {


        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Map<String, String> values = new HashMap<>();
        values.put("count", Integer.toString(maxItems));
        String baseQuery = "Observation.Query";
        List<MccObservation> out = this.QueryObservations(baseQuery, mode, client, subjectId, sortOrder, webRequest, headers, values);

        if (out.size()>maxItems)
        {
            out = out.subList(0,maxItems-1);
        }
        MccObservation[] outA = new MccObservation[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/observationsbyvalueset")
    public MccObservation[] getObservationsByValueSet(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "valueset") String valueset, @RequestParam(name = "max", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        List<MccObservation> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);


        //Right now this search is using the local expanded version of the value set
        //If the server supports code:in then it would be better to use that feature
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(valueset);
        if (valueSetObj != null) {

            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count", Integer.toString(maxItems));
            String baseQuery = "Observation.QueryValueSetExpanded";
            out = this.QueryObservations(baseQuery, mode, client, subjectId, sortOrder, webRequest, headers, values);

        } else {

            log.warn("Search for non-existent values set: "+valueset);
        }

        if (out.size()>maxItems)
        {
            out = out.subList(0,maxItems-1);
        }
        MccObservation[] outA = new MccObservation[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    /**
     * Finds the queries that will match the request, for most modes this will be a single item
     *
     * @param query the Query Name
     * @param mode  the Mode (code, combo, panel, component)
     * @return a list of query keys
     */
    private List<String> getQueryStrings(String query, String mode) {
        //Normalize the mode (Combo, Code, Panel, Component)
        mode = mode.toLowerCase();
        //Prep Output
        ArrayList<String> out = new ArrayList<>();
        String key;
        //Search for matches
        if (mode.equals("combo")) {
            key = getKey(query, "combo");
            if (queryManager.doesQueryExist(key) == false) {
                key = getKey(query, "code");
                if (queryManager.doesQueryExist(key)) {
                    out.add(key);
                }
                key = getKey(query, "component");
                if (queryManager.doesQueryExist(key)) {
                    out.add(key);
                }
            } else {
                out.add(key);
            }

        } else {
            key = getKey(query, mode);
            if (queryManager.doesQueryExist(key)) {
                out.add(key);
            }
        }
        return out;
    }

    MccObservation getEmptyObservation(String codes, String context)
    {
        MccObservation out = new MccObservation();

        out.setValue(GenericType.fromString("No Data"));
        out.setStatus("notfound");
        out.setFHIRId("");
        return out;
    }
}
