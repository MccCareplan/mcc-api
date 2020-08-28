package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.FHIRServerManager;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@CrossOrigin
public class SocialConcernController {
    @GetMapping("/socialconcernsummary")
    public ConditionLists getConditionSummary(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String careplanId, @RequestParam(required = false, name = "server") String serverId) {
        ConditionLists out = new ConditionLists();

        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        Bundle results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId))
                .and(Condition.CATEGORY.exactly().code("health-concern")).returnBundle(Bundle.class).execute();
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

    @GetMapping("/socialconcerns")
    public SocialConcern[] getCarePlans(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "server") String serverId) {
        ArrayList<SocialConcern> out = new ArrayList<>();
        FHIRServer srv = FHIRServerManager.getManager().getServerWithDefault(serverId);
        FhirContext fhirContext = FHIRServices.getFhirServices().getR4Context();
        Context ctx = ContextManager.getManager().findContextForSubject(subjectId);
        IGenericClient client = fhirContext.newRestfulGenericClient(srv.getBaseURL());
        //TODO: Query for concerns
        //Bundle results = client.search().forResource(CarePlan.class).where(CarePlan.SUBJECT.hasId(subjectId))
        //        .returnBundle(Bundle.class).execute();
        //for (Bundle.BundleEntryComponent e : results.getEntry()) {
        //    if (e.getResource().fhirType() == "CarePlan") {
        //        CarePlan c = (CarePlan) e.getResource();
        //        out.add(mapCarePlan(c, client, ctx));
        //    }
        //}
        /*
        out.add(new SocialConcern("Food Security"));
        out.add(new SocialConcern("Transportation Access"));
        out.add(new SocialConcern("Housing Stability"));
        out.add(new SocialConcern("Primary Language"));
        out.add(new SocialConcern("Health Insurance Status/Type"));
        out.add(new SocialConcern("History of Abuse"));
        out.add(new SocialConcern("Computer/Phone Access"));
        out.add(new SocialConcern("Alcohol Abuse"));
        out.add(new SocialConcern("Substance Abuse"));
        out.add(new SocialConcern("Caregiver Characteristics"));
        out.add(new SocialConcern("Characteristic of Home Environment"));
        out.add(new SocialConcern("Employment Status"));
        out.add(new SocialConcern("Education Level"));
        out.add(new SocialConcern("Environmental Conditions"));
`       */

        ConditionLists concerns = new ConditionLists();

        Bundle results = client.search().forResource(Condition.class).where(Condition.SUBJECT.hasId(subjectId))
                .and(Condition.CATEGORY.exactly().code("health-concern")).returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent e : results.getEntry()) {
            if (e.getResource().fhirType() == "Condition") {
                Condition c = (Condition) e.getResource();
                addCondtionToConditionList(concerns,c, ctx);
            }
        }
        concerns.categorizeConditions();
        SocialConcern[] outA = new SocialConcern[out.size()];
        for (ConditionSummary s : concerns.getActiveConcerns())
        {
            SocialConcern crn = new SocialConcern();
            crn.setName(s.getCode().getText());
            crn.setDate(s.getFirstOnset());
            crn.setData(s.getClinicalStatus());
            out.add(crn);
        }
        for (ConditionSummary s : concerns.getInactiveConcerns())
        {
            SocialConcern crn = new SocialConcern();
            crn.setName(s.getCode().getText());
            crn.setDate(s.getFirstOnset());
            crn.setData(s.getClinicalStatus());
            out.add(crn);
        }
        outA = out.toArray(outA);
        return outA;
    }

}
