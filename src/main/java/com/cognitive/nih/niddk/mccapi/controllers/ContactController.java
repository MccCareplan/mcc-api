package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.mappers.*;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ContactController {
    private final QueryManager queryManager;
    private final IR4Mapper mapper;
    private final ContextManager contextManager;

    @Value("${mcc.careteam.use.active}")
    private String useActiveCareTeams;
    boolean isUseActiveCareTeams;

    @Value("${mcc.careteam.use.careplan}")
    private String useCareTeamsFromPlan;
    private boolean isUseCareTeamsFromPlan;

    public ContactController(QueryManager queryManager, IR4Mapper mapper, ContextManager contextManager) {
        this.queryManager = queryManager;
        this.mapper = mapper;
        this.contextManager = contextManager;
    }

    @PostConstruct
    public void config()
    {
        isUseActiveCareTeams = Boolean.parseBoolean(useActiveCareTeams);
        isUseCareTeamsFromPlan = Boolean.parseBoolean(useCareTeamsFromPlan);
        log.info("Config: mcc.careteam.use.active = "+useActiveCareTeams);
        log.info("Config: mcc.careteam.use.careplan = "+useCareTeamsFromPlan);
    }

    @GetMapping(value = "/image/contact/{id}", produces = "image/jpeg")
    public Byte[] getImage(@PathVariable(value="id") String id,  @RequestHeader Map<String, String> headers, WebRequest webRequest)
    {
        //Grab the reference
        //Make sure it is jpeg
        //Decode the image
        return null;
    }

    @GetMapping("/contact")
    public Contact[] getContacts(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = false, name = "careplan") String carePlanId, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        ArrayList<Contact> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

        Contact contact;
        Patient fp = client.read().resource(Patient.class).withId(subjectId).execute();

        contact = mapper.fhir2Contact(fp, ctx);
        out.add(contact);

        //Find Emergency Contacts
        List<Patient.ContactComponent> contacts = mapper.getActiveContactOfType(fp, "C");
        if (contacts.size() > 0) {
            for (Patient.ContactComponent pc : contacts) {
                contact = mapper.fhir2Contact(pc, ctx);
                contact.setRole(Contact.ROLE_EMERGENCY);
                out.add(contact);
            }
        }
        //Look for primary care
        if (fp.hasGeneralPractitioner()) {
            List<Reference> gp = fp.getGeneralPractitioner();
            for (Reference ref : gp) {
                String type = ref.getType();
                if (FHIRHelper.isReferenceOfType(ref, "Practitioner")) {
                    //DIRECT-FHIR-REF
                    Practitioner p = client.fetchResourceFromUrl(Practitioner.class, ref.getReference());
                    Contact pc = mapper.fhir2Contact(p, ctx);
                    pc.setRole(Contact.ROLE_PRIMARY_CARE);
                    out.add(pc);
                }
                //TODO: Handle other types
            }

        }

        boolean foundCareTeam = false;
        //If a care plan is presented then we process it to find the care teams
        if (StringUtils.hasText(carePlanId) && isUseCareTeamsFromPlan) {
            // Fetch Careplan

            try {
                Map<String, String> values = new HashMap<>();
                values.put("id", carePlanId);

                String callUrl = queryManager.setupQuery("CarePlan.Lookup", values, webRequest);

                if (callUrl != null) {
                    CarePlan fc = client.fetchResourceFromUrl(CarePlan.class, callUrl);
                    //CarePlan fc = client.read().resource(CarePlan.class).withId(carePlanId).execute();

                    if (fc != null) {
                        List<Reference> teams = fc.getCareTeam();

                        //TODO: In the future maybe remove duplicate when more then one team is present
                        for (Reference ref : teams) {
                            if (FHIRHelper.isReferenceOfType(ref, "CareTeam")) {
                                foundCareTeam = true;
                                CareTeam t = client.fetchResourceFromUrl(CareTeam.class, ref.getReference());
                                if (t != null) {
                                    out.addAll(mapper.fhir2Contacts(t, ctx));
                                }
                            }
                        }
                    }
                }
            } catch (Exception exp) {
                log.warn("Error tyring to fetch careplan " + carePlanId + ", while loading contact " + exp.getMessage());
            }
        }

        if (!foundCareTeam && isUseActiveCareTeams)
        {
            //For some reason there was no careplan or associated careteam - So we will look for any active careteam
            Map<String, String> values = new HashMap<>();
            values.put("subject", subjectId);

            String callUrl = queryManager.setupQuery("CareTeam.Query.ActiveList", values, webRequest);
            if (callUrl != null)
            {
                Bundle fc = client.fetchResourceFromUrl(Bundle.class, callUrl);
                if (fc != null && !fc.isEmpty())
                {
                    for (Bundle.BundleEntryComponent e: fc.getEntry())
                    {
                        if (e.getResource().fhirType().compareTo("CareTeam") == 0) {
                            CareTeam t = (CareTeam) e.getResource();
                            out.addAll(mapper.fhir2Contacts(t, ctx));
                        }
                    }
                }

            }

        }
        //Finally we look for Insurance
        contacts = mapper.getActiveContactOfType(fp, "I");
        if (contacts.size() > 0) {
            for (Patient.ContactComponent pc : contacts) {
                contact = mapper.fhir2Contact(pc, ctx);
                contact.setRole(Contact.ROLE_INSURANCE);
                out.add(contact);
            }
        }


        Contact[] outA = new Contact[out.size()];
        outA = out.toArray(outA);
        return outA;
    }
}
