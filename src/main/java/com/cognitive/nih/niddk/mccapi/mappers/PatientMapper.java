package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Contact;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccPatient;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import org.hl7.fhir.r4.model.*;

import java.util.*;

public class PatientMapper {
    private static String RACE_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
    private static String ENTHNICITY_KEY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
    private static String OMB_CATEGORY = "ombCategory";

    private static String PRIORITY_PRIVATE = "home|mobile|work";
    private static String PRIORITY_PUBLIC = "work|mobile|home";

    private static HashMap<String, String> useMap = new HashMap<>();

    static {
        useMap.put("C", PRIORITY_PRIVATE); // Emergency Contact
        useMap.put("K", PRIORITY_PRIVATE); // Next Of Kin

        useMap.put("E", PRIORITY_PUBLIC); // Employer
        useMap.put("F", PRIORITY_PUBLIC); // Federal Agency
        useMap.put("I", PRIORITY_PUBLIC); // Insurance Company
        useMap.put("S", PRIORITY_PUBLIC); // State Agency
        useMap.put("U", PRIORITY_PUBLIC ); // Unknown

    }

    public static List<Patient.ContactComponent> getActiveContactOfType(Patient in, String type)
    {
        ArrayList<Patient.ContactComponent> out = new ArrayList<>();
        if (in.hasContact())
        {
            List<Patient.ContactComponent> contacts = in.getContact();
            if (contacts.size()>0){
                Date now = new Date();
             out.ensureCapacity(contacts.size());
             for (Patient.ContactComponent component: contacts)
             {
                 if (component.hasPeriod())
                 {
                     Period p = component.getPeriod();
                     if (!FHIRHelper.isInPeriod(p,now))
                     {
                         //Skip the contact
                         continue;
                     }
                 }
                 if (component.hasRelationship())
                 {
                     List<CodeableConcept> rel = component.getRelationship();
                     for (CodeableConcept concept: rel) {
                         //TODO: Harden up for System of the Relationship
                         //Maybe http://terminology.hl7.org/CodeSystem/v2-0131 version 2.9
                         if (FHIRHelper.containsCode(concept, type))
                         {
                             out.add(component);
                             break;
                         }
                     }
                 }
             }
            }
        }
        return out;
    }
    public static Contact fhir2Contact(Patient.ContactComponent in, Context ctx) {
        String usePrioriy = PRIORITY_PRIVATE;

        if (in.hasRelationship()) {
            //We just use the first relationship
            String code = in.getRelationshipFirstRep().getCodingFirstRep().getCode();
            if (useMap.containsKey(code)) {
                usePrioriy = useMap.get(code);
            }
        }

        Contact out = new Contact();
        if (in.hasName()) {
            out.setName(FHIRHelper.getName(in.getName()));
        } else {
            if (in.hasOrganization()) {
                Reference orgRef = in.getOrganization();
                if (orgRef.hasReference()) {
                    out.setRelFhirId(orgRef.getReference());
                }
                out.setName(NameResolver.getReferenceName(orgRef,ctx));
            }
        }

        if (in.hasAddress()) {
            out.setAddress(FHIRHelper.addressToString(in.getAddress()));
        }

        //Deal with contact points
        List<ContactPoint> contactPoints = FHIRHelper.filterToCurrentContactPoints(in.getTelecom());
        Map<String, List<ContactPoint>> cpBySystem = FHIRHelper.organizeContactTypesBySystem(contactPoints);
        ContactPoint bestPhone = FHIRHelper.findBestContactByType(cpBySystem, "phone", usePrioriy);
        if (bestPhone != null) {
            out.setPhone(bestPhone.getValue());
        }
        ContactPoint bestEmail = FHIRHelper.findBestContactByType(cpBySystem, "email", usePrioriy);
        if (bestEmail != null) {
            out.setEmail(bestEmail.getValue());
        }


        return out;

    }

    public static Contact fhir2Contact(Patient in, Context ctx) {
        Contact out = new Contact();
        out.setName(FHIRHelper.getBestName(in.getName()));
        out.setRelFhirId(FHIRHelper.getIdString(in.getIdElement()));

        Address a = FHIRHelper.findBestAddress(in.getAddress(), "home");
        if (a != null) {
            out.setAddress(FHIRHelper.addressToString(a));
        }
        //in.getContact();   //What to d this this in the future

        //Deal with contact points
        List<ContactPoint> contactPoints = FHIRHelper.filterToCurrentContactPoints(in.getTelecom());
        Map<String, List<ContactPoint>> cpBySystem = FHIRHelper.organizeContactTypesBySystem(contactPoints);
        ContactPoint bestPhone = FHIRHelper.findBestContactByType(cpBySystem, "phone", "home|mobile|work");
        if (bestPhone != null) {
            out.setPhone(bestPhone.getValue());
        }
        ContactPoint bestEmail = FHIRHelper.findBestContactByType(cpBySystem, "email", "home|work");
        if (bestEmail != null) {
            out.setEmail(bestEmail.getValue());
        }


        out.setType(Contact.TYPE_PERSON);
        out.setRole(Contact.ROLE_PATIENT);
        return out;
    }


    public static MccPatient fhir2local(Patient in, Context ctx) {
        MccPatient out = new MccPatient();
        out.setDateOfBirth(FHIRHelper.dateToString(in.getBirthDate()));
        out.setGender(in.getGender().getDisplay());
        out.setName(FHIRHelper.getBestName(in.getName()));
        out.setFHIRId(in.getIdElement().getIdPart());
        out.setRace(FHIRHelper.getCodingDisplayExtensionAsString(in, RACE_KEY, OMB_CATEGORY, "Undefined"));
        out.setEthnicity(FHIRHelper.getCodingDisplayExtensionAsString(in, ENTHNICITY_KEY, OMB_CATEGORY, "Undefined"));
        out.setId(in.hasIdentifier() ? in.getIdentifierFirstRep().getValue() : "Unknown");
        return out;
    }

}
