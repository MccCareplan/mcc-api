package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.MedicationLists;
import com.cognitive.nih.niddk.mccapi.data.MedicationSummaryList;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.MedicationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MedicationController {
    private final QueryManager queryManager;

    public MedicationController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    private void getClanPlanMedReqIds(String careplanId, HashMap<String, String> carePlanMedicationsRequests, IGenericClient client, Context ctx) {
        if (careplanId != null) {
            String[] cps = careplanId.split(",");
            // Fetch the careplan and grab medications
            for (String cpId : cps) {
                Map<String, String> values = new HashMap<>();
                values.put("id", cpId);
                String callUrl = queryManager.setupQuery("CarePlan.Lookup", values);

                if (callUrl != null) {
                    CarePlan cp = client.fetchResourceFromUrl(CarePlan.class, cpId);
                    if (cp != null) {
                        List<CarePlan.CarePlanActivityComponent> acp = cp.getActivity();
                        for (CarePlan.CarePlanActivityComponent a : acp) {
                            if (a.hasReference()) {
                                Reference ref = a.getReference();
                                if (Helper.isReferenceOfType(ref, "MedicationRequest")) {
                                    String key = ref.getReference();
                                    if (ref != null) {
                                        if (carePlanMedicationsRequests.containsKey(key)) {
                                            String val = carePlanMedicationsRequests.get(key);
                                            carePlanMedicationsRequests.put(key, val + "," + cpId);

                                        } else {
                                            carePlanMedicationsRequests.put(key, cpId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @GetMapping("/medication")
    public MccMedicationRecord getMedication(@RequestParam(required = true, name = "type") String type, @RequestParam(required = true, name = "id") String id, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        MccMedicationRecord out = null;

        Context ctx = ContextManager.getManager().findContextForSubject(null, headers);
        ctx.setClient(client);

        if (type.compareTo("MedicationRequest") == 0) {
            //DIRECT-FHIR-REF
            MedicationRequest mr = client.fetchResourceFromUrl(MedicationRequest.class, id);
            if (mr != null) {
                out = MedicationMapper.fhir2local(mr, ctx);
            }
        } else if (type.compareTo("MedicationStatement") == 0) {
            //DIRECT-FHIR-REF
            MedicationStatement ms = client.fetchResourceFromUrl(MedicationStatement.class, id);
            if (ms != null) {
                out = MedicationMapper.fhir2local(ms, ctx);
            }
        }
        return out;
    }

    @GetMapping("/medicationlists")
    public MedicationLists getMedicationLists(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MedicationLists out = new MedicationLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);

        HashMap<String, String> carePlanMedicationsRequests = new HashMap<>();
        getClanPlanMedReqIds(careplanId, carePlanMedicationsRequests, client, ctx);

        Map<String, String> values = new HashMap<>();
        String callUrl = queryManager.setupQuery("MedicationRequest.Query", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            // Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
            //    .returnBundle(Bundle.class).execute();
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("MedicationRequest") == 0) {
                    MedicationRequest mr = (MedicationRequest) e.getResource();
                    out.addMedicationRequest(mr, carePlanMedicationsRequests, ctx);
                }
            }
        }

        callUrl = queryManager.setupQuery("MedicationStatement.Query", values, webRequest);
        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            //results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
            //        .returnBundle(Bundle.class).execute();

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("MedicationStatement") == 0) {
                    MedicationStatement ms = (MedicationStatement) e.getResource();
                    out.addMedicationStatement(ms, ctx);
                }
            }
        }
        return out;
    }

    @GetMapping("/medicationsummary")
    public MedicationSummaryList getMedicationSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MedicationSummaryList out = new MedicationSummaryList();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);
        HashMap<String, String> carePlanMedicationsRequests = new HashMap<>();
        getClanPlanMedReqIds(careplanId, carePlanMedicationsRequests, client, ctx);

        Map<String, String> values = new HashMap<>();
        String callUrl = queryManager.setupQuery("MedicationRequest.Query", values, webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            //Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
            //   .returnBundle(Bundle.class).execute();
            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("MedicationRequest") == 0) {
                    MedicationRequest mr = (MedicationRequest) e.getResource();
                    out.addMedicationRequest(mr, carePlanMedicationsRequests, ctx);
                }
            }
        }

        callUrl = queryManager.setupQuery("MedicationStatement.Query", values, webRequest);
        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
            //results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
            //        .returnBundle(Bundle.class).execute();

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType().compareTo("MedicationStatement") == 0) {
                    MedicationStatement ms = (MedicationStatement) e.getResource();
                    out.addMedicationStatement(ms, ctx);
                }
            }
        }
        return out;
    }
}
