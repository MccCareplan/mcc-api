package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.ConditionLists;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
public class ConditionController {

    @GetMapping("/conditionsummary")
    public ConditionLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestParam(required = false, name = "server") String serverId) {
        ConditionLists out = new ConditionLists();

        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(Condition.class).where(CarePlan.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
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
        list.addCondtion(c, ctx);
    }

    @GetMapping("/condition")
    public MccCondition[] getConditions(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "server") String serverId) {
        ArrayList<MccCondition> out = new ArrayList<>();
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(Condition.class).where(CarePlan.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
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
    public MccCondition getCodition(@PathVariable(value = "id") String id, @RequestParam(required = false) String serverId) {
        MccCondition c;
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Condition fc = client.read().resource(Condition.class).withId(id).execute();
        String subjectId = fc.getSubject().getId();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
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
        StringBuffer addSum = new StringBuffer();
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
