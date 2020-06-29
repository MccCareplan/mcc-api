package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.FHIRServer;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.mappers.CareplanMapper;
import com.cognitive.nih.niddk.mccapi.mappers.ConditionMapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class CareplanController {


    @GetMapping("/careplan")
    public MccCarePlan[] getCarePlans(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "server") String serverId) {
        ArrayList<MccCarePlan> out = new ArrayList<>();
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(CarePlan.class).where(CarePlan.SUBJECT.hasId(subjectId))
                .returnBundle(Bundle.class).execute();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "CarePlan") {
                CarePlan c = (CarePlan) e.getResource();
                out.add(mapCarePlan(c, client, ctx));
            }
        }

        MccCarePlan[] outA = new MccCarePlan[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/careplan/{id}")
    public MccCarePlan getCareplan(@PathVariable(value = "id") String id, @RequestParam(required = false) String serverId) {
        //Create a dummy patient for the mnoment
        MccCarePlan c;
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        CarePlan fc = client.read().resource(CarePlan.class).withId(id).execute();
        String subjectId = fc.getSubject().getId();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        c = mapCarePlan(fc, client, ctx);
        return c;
    }

    private MccCarePlan mapCarePlan(CarePlan fc, IGenericClient client, Context ctx) {
        MccCarePlan c;
        c = CareplanMapper.fhir2local(fc,ctx);
        //Now deal with relationships

        //Start with Addresses
        int index = 0;
        StringBuffer addSum = new StringBuffer();
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
