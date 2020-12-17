package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.CareplanMapper;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class CareplanController {

    private final QueryManager queryManager;

    public CareplanController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @GetMapping("/careplan")
    public MccCarePlan[] getCarePlans(@RequestParam(required = true, name = "subject") String subjectId,  @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccCarePlan> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        String callUrl=queryManager.setupQuery("CarePlan.Query",values,webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType() == "CarePlan") {
                    CarePlan c = (CarePlan) e.getResource();
                    out.add(mapCarePlan(c, client, ctx));
                }
            }
        }

        MccCarePlan[] outA = new MccCarePlan[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/careplan/{id}")
    public MccCarePlan getCareplan(@PathVariable(value = "id") String id,  @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        MccCarePlan c;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        //   "/CarePlan/{id}"
        Map<String,String> values = new HashMap<>();
        values.put("id",id);
        String callUrl=queryManager.setupQuery("CarePlan.Lookup",values,webRequest);

        if (callUrl != null) {
            CarePlan fc = client.fetchResourceFromUrl(CarePlan.class, callUrl);
            //CarePlan fc = client.read().resource(CarePlan.class).withId(id).execute();
            String subjectId = fc.getSubject().getId();
            Context ctx = ContextManager.getManager().findContextForSubject(subjectId, headers);
            c = mapCarePlan(fc, client, ctx);
        }
        else
        {
            //TODO: Return Unavailable carecplac
            c = new MccCarePlan();
            log.warn("Careplan "+id+" disabled");
        }
        return c;
    }

    private MccCarePlan mapCarePlan(CarePlan fc, IGenericClient client, Context ctx) {
        MccCarePlan c;
        c = CareplanMapper.fhir2local(fc,ctx);
        //Now deal with relationships

        //Start with Addresses
        int index = 0;
        StringBuilder addSum = new StringBuilder();
        List<Reference> addresses = fc.getAddresses();
        MccCondition[] mccAddrs = new MccCondition[addresses.size()];
        for(Reference reference: addresses)
        {
         String ref = reference.getReference();
         Condition add = client.read().resource(Condition.class).withUrl(ref).execute();
         if (index>0) addSum.append(", ");
         addSum.append(Helper.getConceptDisplayString(add.getCode()));
         mccAddrs[index] = ConditionMapper.fhir2local(add, ctx);
         index++;
        }
        c.setAddressesSummary(addSum.toString());
        c.setAddresses(mccAddrs);


        return c;
    }
}
