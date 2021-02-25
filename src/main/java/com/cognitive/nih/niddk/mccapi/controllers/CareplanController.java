package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlan;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.data.MccCarePlanSummary;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.ProfileManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.ICareplanMapper;
import com.cognitive.nih.niddk.mccapi.mappers.IConditionMapper;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;;

import java.util.*;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class CareplanController {

    private final QueryManager queryManager;
    private final IR4Mapper mapper;

    public CareplanController(QueryManager queryManager,IR4Mapper mapper) {
        this.queryManager = queryManager;
        this.mapper = mapper;
    }

    /**
     * Return a list of care plans that are active and that address recognizated MCC Conditions
     * @param subjectId
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/find/supported/careplans")
    public MccCarePlanSummary[] getSupportedCarePlans(@RequestParam(required = true, name = "subject") String subjectId,  @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccCarePlanSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        String callUrl=queryManager.setupQuery("CarePlan.Query",values,webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType() == "CarePlan") {
                    CarePlan c = (CarePlan) e.getResource();
                    if (c.getStatus().toCode().compareTo("active")==0 && c.getIntent().toCode().compareTo("plan") ==0 ) {
                        Set<String> profiles = carePlanRecognizedFor(c, ctx);
                        if (profiles.size()>0) {
                            out.add(mapper.fhir2Summary(c, profiles, ctx));
                        }
                    }
                }
            }
        }

        MccCarePlanSummary[] outA = new MccCarePlanSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    /**
     * Return a list of care plans that are active and that address recognized MCC Conditions, sorted
     * @param subjectId
     * @poram matchScheme    order, profiles, created, lastModified
     * @param headers
     * @param webRequest
     * @return
     */
    @GetMapping("/find/best/careplan")
    public MccCarePlanSummary[] getBest(@RequestParam(required = true, name = "subject") String subjectId,  @RequestParam(name = "matchScheme", defaultValue = "profiles") String matchScheme, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<MccCarePlanSummary> out = new ArrayList<>();

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String,String> values = new HashMap<>();
        String callUrl=queryManager.setupQuery("CarePlan.Query",values,webRequest);

        if (callUrl != null) {
            Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);

            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);

            for (Bundle.BundleEntryComponent e : results.getEntry()) {
                if (e.getResource().fhirType() == "CarePlan") {
                    CarePlan c = (CarePlan) e.getResource();
                    if (c.getStatus().toCode().compareTo("active")==0 && c.getIntent().toCode().compareTo("plan") ==0 ) {
                        Set<String> addresses = carePlanRecognizedFor(c, ctx);
                        if (addresses.size()>0) {
                            out.add(mapper.fhir2Summary(c, addresses,ctx));
                        }
                    }
                }
            }
        }

        if (out.size()>1)
        {
            MccCarePlanSummary first = out.get(0);
            //Ok We have more then one result with need to use a scheme to sort thm
            switch (matchScheme)
            {
                case "created":
                {
                    out.sort(first.CompareByCreate().reversed());
                    break;
                }
                case "lastModified":
                {
                    out.sort(first.CompareByLastUpdate().reversed());
                    break;
                }
                case "profiles":
                {
                    out.sort(first.CompareByProfiles().reversed().thenComparing(first.CompareByLastUpdate()));

                    break;
                }
                case "order":
                default:
                {
                    break;
                }
            }
        }
        MccCarePlanSummary[] outA = new MccCarePlanSummary[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    private Set<String> carePlanRecognizedFor(CarePlan plan, Context ctx)
    {
        HashSet<String> out = new HashSet<>();
        List<Reference> addresses = plan.getAddresses();
        if (addresses != null && addresses.size() > 0 )
        {
            for (Reference ref: addresses)
            {
                if (ref.hasReference())
                {
                    try {
                        Condition condition = ctx.getClient().fetchResourceFromUrl(Condition.class, ref.getReference());
                          List<String> profiles = getConditionProfiles(condition);
                        if (profiles != null)
                        {
                            for (String p:profiles)
                            {
                                out.add(p);
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        log.warn("Problem resolving condition references: "+ref.getReference(),e);
                    }
                }
            }
        }
        return out;
    }

    protected List<String> getConditionProfiles(Condition condition)
    {
        List<String> out=null;
        if (condition.hasCode())
        {
            out = ProfileManager.getProfileManager().getProfilesForConceptAsList(condition.getCode());
        }
        return out;
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

            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);

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
            Context ctx = ContextManager.getManager().setupContext(subjectId, client, mapper, headers);
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
        c = mapper.fhir2local(fc,ctx);
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
         addSum.append(FHIRHelper.getConceptDisplayString(add.getCode()));
         mccAddrs[index] = mapper.fhir2local(add, ctx);
         index++;
        }
        c.setAddressesSummary(addSum.toString());
        c.setAddresses(mccAddrs);


        return c;
    }
}
