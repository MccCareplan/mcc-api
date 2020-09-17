package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.MedicationLists;
import com.cognitive.nih.niddk.mccapi.data.MedicationSummaryList;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.mappers.MedicationMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MedicationController {

    @GetMapping("/medicationlists")
    public MedicationLists getMedicationLists(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers) {
        MedicationLists out = new MedicationLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);

        HashMap<String, String> carePlanMedicationsRequests = new HashMap<>();
        getClanPlanMedReqIds(careplanId, carePlanMedicationsRequests, client, ctx);

        Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType().compareTo("MedicationRequest") == 0) {
                MedicationRequest mr = (MedicationRequest) e.getResource();
                out.addMedicationRequest(mr, carePlanMedicationsRequests, ctx);
            }
        }

        results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType().compareTo("MedicationStatement") == 0) {
                MedicationStatement ms = (MedicationStatement) e.getResource();
                out.addMedicationStatement(ms, ctx);
            }
        }

        //results = client.fetchResourceFromUrl(Bundle.class,"/MedicationRequest?subject=cc-pat-pnoelle");
        return out;
    }

    private void getClanPlanMedReqIds(String careplanId, HashMap<String, String> carePlanMedicationsRequests, IGenericClient client, Context ctx) {
        if (careplanId != null) {
            String[] cps = careplanId.split(",");
            // Fetch the careplan and grab medications
            for (String cpId : cps) {
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

    @GetMapping("/medicationsummary")
    public MedicationSummaryList getMedicationSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestHeader Map<String, String> headers) {
        MedicationSummaryList out = new MedicationSummaryList();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
        ctx.setClient(client);
        HashMap<String, String> carePlanMedicationsRequests = new HashMap<>();
        getClanPlanMedReqIds(careplanId, carePlanMedicationsRequests, client, ctx);

        Bundle results = client.search().forResource(MedicationRequest.class).where(MedicationRequest.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType().compareTo("MedicationRequest") == 0) {
                MedicationRequest mr = (MedicationRequest) e.getResource();
                out.addMedicationRequest(mr,carePlanMedicationsRequests, ctx);
            }
        }

        results = client.search().forResource(MedicationStatement.class).where(MedicationStatement.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType().compareTo("MedicationStatement") == 0) {
                MedicationStatement ms = (MedicationStatement) e.getResource();
                out.addMedicationStatement(ms, ctx);
            }
        }

        return out;
    }

    @GetMapping("/medication")
    public MccMedicationRecord getMedication(@RequestParam(required = true, name = "type") String type, @RequestParam(required = true, name = "id") String id, @RequestHeader Map<String, String> headers) {

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        MccMedicationRecord out = null;

        Context ctx = ContextManager.getManager().findContextForSubject(null, headers);
        ctx.setClient(client);

        if (type.compareTo("MedicationRequest") == 0) {
            MedicationRequest mr = client.fetchResourceFromUrl(MedicationRequest.class, id);
            if (mr != null) {
                out = MedicationMapper.fhir2local(mr, ctx);
            }
        } else if (type.compareTo("MedicationStatement") == 0) {
            MedicationStatement ms = client.fetchResourceFromUrl(MedicationStatement.class, id);
            if (ms != null) {
                out = MedicationMapper.fhir2local(ms, ctx);
            }
        }
        return out;
    }
}
