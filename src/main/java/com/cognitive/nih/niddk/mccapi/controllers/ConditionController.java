package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.ConditionLists;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ConditionController {

    @GetMapping("/conditionsummary")
    public ConditionLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId,  @RequestHeader Map<String, String> headers) {
        ConditionLists out = new ConditionLists();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Bundle results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        ctx.setClient(client);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Condition") {
                Condition c = (Condition) e.getResource();
                addCondtionToConditionList(out,c, ctx);
            }
        }
        out.categorizeConditions();
        return out;
    }

    private void addCondtionToConditionList(ConditionLists list, Condition c, Context ctx)
    {
        list.addCondition(c, ctx);
    }

    @GetMapping("/condition")
    public MccCondition[] getConditions(@RequestParam(required = true, name = "subject") String subjectId,  @RequestHeader Map<String, String> headers) {
        ArrayList<MccCondition> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Bundle results = client.search().forResource(Condition.class).where(CarePlan.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        ctx.setClient(client);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Condition") {
                Condition c = (Condition) e.getResource();
                out.add(mapCondition(c, client, ctx));
            }
        }

        MccCondition[] outA = new MccCondition[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/condition/{id}")
    public MccCondition getCodition(@PathVariable(value = "id") String id,  @RequestHeader Map<String, String> headers) {
        MccCondition c;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Condition fc = client.read().resource(Condition.class).withId(id).execute();
        if (fc == null)
        {
            throw new ItemNotFoundException(id);
        }
        String subjectId = fc.getSubject().getId();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId,headers);
        ctx.setClient(client);
        c = mapCondition(fc, client, ctx);
        return c;
    }

    private MccCondition mapCondition(Condition fc, IGenericClient client, Context ctx) {
        MccCondition c;
        c = ConditionMapper.fhir2local(fc, ctx);
        //Now deal with relationships

        /*
        //Start with Addresses
        int index = 0;
        StringBuilder addSum = new StringBuilder();
        List<Reference> addresses = fc.getAddresses();
        MccCondition[] mccAddrs = new MccCondition[addresses.size()];
        for(Reference reference: addresses)
        {
            String ref = reference.getReference();
            Condition add = client.read().resource(Condition.class).withUrl(ref).execute();
            if (index>0) addSum.append(",");
            addSum.append(Helper.getConceptDisplayString(add.getCode()));
            mccAddrs[index] = ConditionMapper.fhir2local(add);
        }
        c.setAddressesSummary(addSum.toString());
        c.setAddresses(mccAddrs);
        */

        return c;
    }
}
