/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.controllers;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.Demographics;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.MccValueSet;
import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.cognitive.nih.niddk.mccapi.exception.ItemNotFoundException;
import com.cognitive.nih.niddk.mccapi.managers.ContextManager;
import com.cognitive.nih.niddk.mccapi.managers.QueryManager;
import com.cognitive.nih.niddk.mccapi.managers.ValueSetManager;
import com.cognitive.nih.niddk.mccapi.mappers.IR4Mapper;
import com.cognitive.nih.niddk.mccapi.services.FHIRServices;
import com.cognitive.nih.niddk.mccapi.util.MccHelper;
import com.cognitive.nih.niddk.mccapi.util.UCUMHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class ObservationController {
    private static final HashMap<String, String> revPanelMap = new HashMap<>();

    static {
        //Blood Pressure
        revPanelMap.put("8462-4", "85354-9");
        revPanelMap.put("8480-6", "85354-9");
    }

    private final QueryManager queryManager;
    private final IR4Mapper mapper;
    private final ContextManager contextManager;
    private final String egfr_vsid = "2.16.840.1.113762.1.4.1222.179";
    private final String sCr_vsid = "2.16.840.1.113762.1.4.1222.111";
    private final String cs_lonic = "http://loinc.org";
    @Value("${mcc.observation.log.unit.failure:false}")
    private String logUnitFailure;
    private boolean bLogUnitFailure = true;
    @Value("${mcc.observation.log.calls:false}")
    private String logCalls;
    private boolean bLogCalls = true;
    @Value("${mcc.observation.egfr.calculate:false}")
    private String calcEgfr;
    private boolean bcalcEgfr = true;

    public ObservationController(QueryManager queryManager, IR4Mapper mapper, ContextManager contextManager) {
        this.queryManager = queryManager;
        this.mapper = mapper;
        this.contextManager = contextManager;
    }

    protected ArrayList<MccObservation> QueryObservations(String baseQuery, String mode, IGenericClient client, String subjectId, String sortOrder, String unit, WebRequest webRequest, Map<String, String> headers, Map<String, String> values) {
        ArrayList<MccObservation> out = new ArrayList<>();
        List<String> calls = getQueryStrings(baseQuery, mode);

        if (calls.size() > 0) {
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

            LinkedHashSet<String> unitHashSet = null;
            if (unit != null && !unit.isEmpty()) {
                unitHashSet = new LinkedHashSet<String>(Arrays.asList(unit
                        .split(",")));
                if (bLogUnitFailure) {
                    log.info("QueryObserveratation called for " + baseQuery + ", using units = " + unit);
                }
            }
            for (String key : calls) {

                String callUrl = queryManager.setupQuery(key, values, webRequest);
                Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //In general the we expect the return value to be in descending date order

                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Observation") == 0) {
                        Observation o = (Observation) e.getResource();
                        if (includeObservation(o, unitHashSet)) {
                            out.add(mapper.fhir2local(o, ctx));
                        }
                    }
                }
            }
            //Now we need possibly to sort the output
            if (sortOrder.compareTo("ascending") == 0) {
                //We need ascending order
                out.sort((MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective()));
            } else if (calls.size() > 1) {
                Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());

                //We need to merge multiples into one and sort descending
                out.sort(comparator.reversed());
            }

        } else {
            //TODO: Deal with suppressed query return
            log.info(baseQuery + " suppressed by override");
        }
        return out;
    }

    protected ArrayList<Observation> QueryObservationsRaw(String baseQuery, String mode, IGenericClient client, String subjectId, String sortOrder, String unit, WebRequest webRequest, Map<String, String> headers, Map<String, String> values) {
        ArrayList<Observation> out = new ArrayList<>();
        List<String> calls = getQueryStrings(baseQuery, mode);

        if (calls.size() > 0) {
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

            LinkedHashSet<String> unitHashSet = null;
            if (unit != null && !unit.isEmpty()) {
                unitHashSet = new LinkedHashSet<String>(Arrays.asList(unit
                        .split(",")));
                if (bLogUnitFailure) {
                    log.info("QueryObserveratationRaw called for " + baseQuery + ", using units = " + unit);
                }
            }

            for (String key : calls) {
                String callUrl = queryManager.setupQuery(key, values, webRequest);
                Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //In general the we expect the return value to be in descending date order

                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Observation") == 0) {
                        Observation o = (Observation) e.getResource();
                        if (includeObservation(o, unitHashSet)) {
                            out.add(o);
                        }
                    }
                }
            }
            /* TODO: Fix sorting
            //Now we need possibly to sort the output
            if (sortOrder.compareTo("ascending") == 0) {
                //We need ascending order
                out.sort((Observation o1, Observation o2) -> o1.getEffective().compareTo(o2.getEffective()));
            } else if (calls.size() > 1) {
                Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());

                //We need to merge multiples into one and sort descending
                out.sort(comparator.reversed());
            }
             */
        } else {
            //TODO: Deal with suppressed query return
            log.info(baseQuery + " suppressed by override");
        }
        return out;
    }

    private ObservationCollection QueryObservationsSegmented(String baseQuery, String mode, IGenericClient client, String subjectId, String sortOrder, String unit, WebRequest webRequest, Map<String, String> headers, Map<String, String> values) {
        ObservationCollection out = new ObservationCollection();
        List<String> calls = getQueryStrings(baseQuery, mode);

        if (calls.size() > 0) {
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);

            LinkedHashSet<String> unitHashSet = null;
            {
                if (unit != null && !unit.isEmpty()) unitHashSet = new LinkedHashSet<String>(Arrays.asList(unit
                        .split(",")));
                if (bLogUnitFailure) {
                    log.info("QueryObserveratationSegmented called for " + baseQuery + ", using units = " + unit);
                }
            }

            for (String key : calls) {
                String callUrl = queryManager.setupQuery(key, values, webRequest);
                Bundle results = client.fetchResourceFromUrl(Bundle.class, callUrl);
                //In general the we expect the return value to be in descending date order

                for (Bundle.BundleEntryComponent e : results.getEntry()) {
                    if (e.getResource().fhirType().compareTo("Observation") == 0) {
                        Observation o = (Observation) e.getResource();
                        if (includeObservation(o, unitHashSet)) {
                            out.add(mapper.fhir2local(o, ctx), "http://loinc.org");
                        }
                    }
                }
            }

            Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());
            //Now we need possibly to sort the output
            if (sortOrder.compareTo("ascending") == 0) {
                //We need ascending order
                out.sort(comparator);
            } else {
                out.sort(comparator.reversed());
            }

        } else {
            //TODO: Deal with suppressed query return
            log.info(baseQuery + " suppressed by override");
        }
        return out;
    }

    private boolean canCalulateEgfr(IGenericClient client, String subjectId, Context ctx, Map<String, String> headers, WebRequest webRequest) {
        if (ctx.getDemographics() != null) {
            return demographicsOkForEgfr(ctx.getDemographics());
        } else {
            //Fetch new demographics
            Map<String, String> values = new HashMap<>();
            values.put("id", subjectId);
            String callUrl = queryManager.setupQuery("Patient.Lookup", values, webRequest);

            if (callUrl != null) {
                Patient fp = client.fetchResourceFromUrl(Patient.class, callUrl);
                //Patient fp = client.read().resource(Patient.class).withId(id).execute();
                Demographics demographics = mapper.fhir2Demographics(fp, ctx);
                ctx.setDemographics(demographics);
                return demographicsOkForEgfr(demographics);
            } else {
                return false;
            }
        }
    }

    @PostConstruct
    public void config() {
        bLogCalls = Boolean.parseBoolean(logCalls);
        log.info("Config: mcc.observation.log.calls (Observation)= " + logCalls);

        bLogUnitFailure = Boolean.parseBoolean(logUnitFailure);
        log.info("Config: mcc.observation.log.unit.failure (Observation)= " + logUnitFailure);


        bcalcEgfr = Boolean.parseBoolean(calcEgfr);
        log.info("Config: mcc.observation.egfr.calculate (Observation)= " + logUnitFailure);
    }

    private MccObservation createEgfrObs(Observation o, Context ctx, long value, String code, String desc) {
        MccObservation newObs = mapper.fhir2local(o, ctx);
        MccCodeableConcept concept = MccHelper.conceptFromCode(code, desc);
        newObs.setCode(concept);

        // Update the value
        GenericType val = new GenericType();
        MccQuantity sq = new MccQuantity();
        sq.setCode("mL/min/{1.73_m2}");
        sq.setSystem("http://unitsofmeasure.org/");
        sq.setUnit("mL/min/1.73m2");
        sq.setValue(BigDecimal.valueOf(value));
        sq.setDisplay(value + " mL/min/1.73m2");
        val.setQuantityValue(sq);
        val.setValueType(MccQuantity.fhirType);
        newObs.setValue(val);

        //fix derived from
        String oRef = o.getIdElement().getIdPart();
        MccReference[] refs = new MccReference[1];
        refs[0] = new MccReference();
        refs[0].setType("Observation");
        refs[0].setReference(oRef);
        newObs.setBasedOn(refs);
        //Clear the reference range for now
        newObs.setReferenceRanges(null);
        //ix id
        newObs.setFHIRId("[Derived]");
        return newObs;
    }

    private boolean demographicsOkForEgfr(Demographics demographics) {
        if (demographics.getDob() == null)
            return false;
        return demographics.getGender() != null;
    }

    private ObservationCollection efgrSegementedFromSrcr(IGenericClient client, String subjectId, Context ctx, String mode, String sortOrder, int maxItems, Map<String, String> headers, WebRequest webRequest) throws ItemNotFoundException {
        ObservationCollection out = new ObservationCollection();

        List<MccObservation> list = egfrFromSrcr(client, subjectId, ctx, mode, sortOrder, maxItems, headers, webRequest);

        for (MccObservation o : list) {
            out.add(o, "http://loinc.org");
        }

        Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());
        //Now we need possibly to sort the output
        if (sortOrder.compareTo("ascending") == 0) {
            //We need ascending order
            out.sort(comparator);
        } else {
            out.sort(comparator.reversed());
        }
        return out;
    }

    /**
     * @param Scr      Serum Creatinine in mg/dL
     * @param isFemale true is femal
     * @param age      age in whole years
     * @return EgfrValues with values for Blacks and Non Blacks
     */
    private EgfrValues egfrCalculation(BigDecimal Scr, boolean isFemale, Double age) {
        /*
        GFR = 141 × min (Scr /κ, 1)α × max(Scr /κ, 1)-1.209 × 0.993Age × 1.018 [if female] × 1.159 [if black]
            where:
            Scr is serum creatinine in mg/dL,
            κ is 0.7 for females and 0.9 for males,
            α is -0.329 for females and -0.411 for males,
            min indicates the minimum of Scr /κ or 1, and
            max indicates the maximum of Scr /κ or 1.

            2.16.840.1.113762.1.4.1222.111 is Scr
            2.16.840.1.113762.1.4.1222.179 is eGFR

            Output Coding
                http://loinc.org,2.70,88293-6,Glomerular filtration rate/1.73 sq M.predicted among blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)
                http://loinc.org,2.70,88294-4,Glomerular filtration rate/1.73 sq M.predicted among non-blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)

               */

        Double a = isFemale ? -0.329 : -0.411;
        Double b = -1.209;
        Double k = isFemale ? 0.7 : 0.9;
        Double z = Scr.doubleValue() / k;

        Double base = 141 * Math.pow(Math.min(z, 1.0), a) * Math.pow(Math.max(z, 1.0), b) * Math.pow(0.993, age);
        if (isFemale) {
            base = base * 1.018;
        }
        //Codes:
        EgfrValues out = new EgfrValues();
        out.Code882944 = Math.round(base);
        out.Code882936 = Math.round(base * 1.159);
        return out;
    }

    private EgfrValues egfrFromSrc(Observation o, Demographics demographics, Context ctx) {

        //Check date
        if (!o.hasEffectiveDateTimeType() && !o.hasEffectiveInstantType()) {
            log.warn("Rejecting Serum Creatinine for eFGR Calculation Reason = EffectDate no DateTime or Instant");
            return null;
        }

        //Check Quantity
        if (!o.hasValueQuantity()) {
            log.warn("Rejecting Serum Creatinine for eFGR Calculation Reason = Value not a quanity");
            return null;
        }

        if (!o.getValueQuantity().hasUnit()) {
            log.warn("Rejecting Serum Creatinine for eFGR Calculation Reason = Quantity without unit");
            return null;
        }
        BigDecimal sCRValueRaw = o.getValueQuantity().getValue();

        BigDecimal sCRValueMg;
        String molUnit = "umol/L";
        String mgUnit = "mg/dL";
        BigDecimal ScrMolMass = BigDecimal.valueOf(113.12);

        //Preferred unit is umol/L (Mol Mass 113.2);
        String unit = o.getValueQuantity().getUnit();
        if (unit.compareTo(mgUnit) != 0) {

            if (unit.compareTo(molUnit) == 0) {
                sCRValueMg = UCUMHelper.umol_L_2_mg_dL(sCRValueRaw, ScrMolMass);
            } else {
                log.warn("Unable to convert " + sCRValueRaw.toPlainString() + " [" + unit + "] to mg/dL for ucum conversion, only umol/L and mg/dL supported at this time");
                return null;
            }
            /*

            Logic if ucum convert could handle mols.
            UcumEssenceService ucumSrv = UCUMHelper.getHelper().getService
            try {
                Decimal v = null;
                v = new Decimal(sCRValue.toPlainString());
                Decimal nv = ucumSrv.convert(v, unit, calcUnit, 113.12);
                sCRValue = new BigDecimal(nv.toString());
            } catch (UcumException e) {
              log.warn("Unable to convert "+sCRValue.toPlainString()+" ["+ unit+ "] to mg/dL for ucum conversion: ",e);
              return;
            }
             */


        } else {
            sCRValueMg = sCRValueRaw;
        }

        boolean isFemale = demographics.getGender().compareTo("female") == 0;
        Date effective = o.hasEffectiveDateTimeType() ? o.getEffectiveDateTimeType().getValue() : o.getEffectiveInstantType().getValue();
        int age = demographics.calculateAge(effective);

        EgfrValues valuesMg = egfrCalculation(sCRValueMg, isFemale, (double) age);
        //Ok now we need to create the two observations and add them to the output
        valuesMg.obs882944 = createEgfrObs(o, ctx, valuesMg.Code882944, "http://loinc.org|88294-4", "Glomerular filtration rate/1.73 sq M.predicted among non-blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)");
        valuesMg.obs882936 = createEgfrObs(o, ctx, valuesMg.Code882936, "http://loinc.org|88293-6", "Glomerular filtration rate/1.73 sq M.predicted among blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)");
        return valuesMg;
    }

    private List<MccObservation> egfrFromSrcr(IGenericClient client, String subjectId, Context ctx, String mode, String sortOrder, int maxItems, Map<String, String> headers, WebRequest webRequest) throws ItemNotFoundException {

        List<MccObservation> out = new ArrayList<>();

        String unit = null;
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(sCr_vsid);
        if (valueSetObj != null) {
            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count", Integer.toString(maxItems));
            String baseQuery = "Observation.QueryValueSetExpanded";
            ArrayList<Observation> sCrList = QueryObservationsRaw(baseQuery, mode, client, subjectId, sortOrder, unit, webRequest, headers, values);
            //Ok now we process the raw observations and create
            Demographics demo = ctx.getDemographics();
            for (Observation o : sCrList) {
                EgfrValues fnd = egfrFromSrc(o, demo, ctx);
                if (fnd != null) {
                    out.add(fnd.obs882936);
                    out.add(fnd.obs882944);
                }
            }

            Comparator<MccObservation> comparator = (MccObservation o1, MccObservation o2) -> o1.getEffective().compareTo(o2.getEffective());
            //Now we need possibly to sort the output
            if (sortOrder.compareTo("ascending") == 0) {
                //We need ascending order
                out.sort(comparator);
            } else {
                out.sort(comparator.reversed());
            }
        } else {
            throw new ItemNotFoundException("No such valaue set: " + sCr_vsid);
        }

        return out;

    }


    private MccObservation findSingleEGfr(String subjectId, IGenericClient client, String mode,Map<String, String> headers, WebRequest webRequest, String code, String codePart )
    {
        List<MccObservation> list;
        Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);
        if (canCalulateEgfr(client, subjectId, ctx, headers, webRequest)) {

            list = egfrFromSrcr(client, subjectId, ctx, mode, "descending", 100, headers, webRequest);
            if (list.size() == 0) {
                //throw new ItemNotFoundException(code);
                if (bLogCalls) {
                    log.info("/find/latest/observation - No Item found");
                }
                return notFound(code);
            }
            String code0 = list.get(0).getCode().getCoding()[0].getCode();
            String code1 = list.get(1).getCode().getCoding()[0].getCode();
            // Determine in Black value is desired
            if (isBlackeGFRCode(codePart)) {
                if (isBlackeGFRCode(code0)) {
                    return list.get(0);
                } else {
                    return list.get(1);
                }
            } else {
                if (isBlackeGFRCode(code0)) {
                    return list.get(1);
                } else {
                    return list.get(0);
                }
            }
        }
        else
        {
            log.warn("attempt to get eGFR without enough data to calculate it, subject "+subjectId);
            return notFound(code);
        }
    }
    /**
     * Creates a compound query key
     *
     * @param query the Query Name
     * @param mode  the Mode (code, combo, panel, component)
     * @return A compound key
     */
    private String getKey(String query, String mode) {
        return query + "." + mode;
    }

    @GetMapping("/find/latest/observation")
    public MccObservation getLatestObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestParam(name = "translate", defaultValue = "false") String translate, @RequestParam(required = false, name = "requiredunit") String unit, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        if (bLogCalls) {
            log.info("Get: /find/latest/observation for " + subjectId + ", code = " + code);
        }

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        Map<String, String> values = new HashMap<>();
        String codePart = code;  //Hold for eGfr is needed
        List<MccObservation> list = new ArrayList<>();
        //Implement a mode that will translate a single code to it's containing value set
        if (translate.toLowerCase().compareTo("true") == 0) {
            //For now we only support 1 code
            ArrayList<String> vs;
            if (code.contains("|")) {
                String[] parts = code.split("\\|");
                codePart = parts[1];
                vs = ValueSetManager.getValueSetManager().findCodesValuesSets(parts[0], parts[1]);
            } else {
                //Default to loinc
                vs = ValueSetManager.getValueSetManager().findCodesValuesSets("http://loinc.org", code);
            }
            //vs.get(0).compareTo()
            if (vs != null && vs.size() > 0) {
                if (bcalcEgfr && vs.get(0).compareTo(egfr_vsid) == 0) {
                    // Ok we have a egfr and an active override
                    // Fetch the Latest Values sorting descending
                    return findSingleEGfr(subjectId, client,mode, headers, webRequest, code, codePart);
                }

                MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(vs.get(0));
                code = valueSetObj.asQueryString();
            } else if (revPanelMap.containsKey(code)) {
                code = revPanelMap.get(code);
            }


        }

        if (bcalcEgfr)
        {
            MccValueSet vs = ValueSetManager.getValueSetManager().findValueSet(egfr_vsid);
            if (vs.hasCode(cs_lonic,codePart))
            {
                return findSingleEGfr(subjectId, client,mode, headers, webRequest, code, codePart);
            }
        }

        values.put("code", code);
        String baseQuery = "Observation.QueryLatest";
        list = this.QueryObservations(baseQuery, mode, client, subjectId, "descending", unit, webRequest, headers, values);


        if (list.size() == 0) {
            //throw new ItemNotFoundException(code);
            if (bLogCalls) {
                log.info("/find/latest/observation - No Item found");
            }
            return notFound(code);
        }

        if (bLogCalls) {
            log.info("/find/latest/observation - returning first item");
        }

        return list.get(0);


    }

    @GetMapping("/observations")
    public MccObservation[] getObservation(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "code") String code, @RequestParam(name = "count", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestParam(required = false, name = "requiredunit") String unit, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        if (bLogCalls) {
            log.info("Get: /observations " + subjectId + ", code = " + code);
        }

        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        Map<String, String> values = new HashMap<>();
        values.put("count", Integer.toString(maxItems));
        String baseQuery = "Observation.Query";
        List<MccObservation> out = this.QueryObservations(baseQuery, mode, client, subjectId, sortOrder, unit, webRequest, headers, values);
        if (bLogCalls) {
            log.info("/observations found " + out.size() + " items");
        }
        if (out.size() > maxItems) {
            out = out.subList(0, maxItems - 1);
        }
        MccObservation[] outA = new MccObservation[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/observationsbyvalueset")
    public MccObservation[] getObservationsByValueSet(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "valueset") String valueset, @RequestParam(name = "max", defaultValue = "100") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestParam(required = false, name = "requiredunit") String unit, @RequestHeader Map<String, String> headers, WebRequest webRequest) {

        if (bLogCalls) {
            log.info("Get: /observationsbyvalueset " + subjectId + ", for valueset " + valueset);
        }


        List<MccObservation> out = new ArrayList<>();
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);
        ///Logic to override and calculate eGFR
        if (bcalcEgfr && valueset.compareTo(egfr_vsid) == 0) {
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);
            if (canCalulateEgfr(client, subjectId, ctx, headers, webRequest)) {
                out = egfrFromSrcr(client, subjectId, ctx, mode, sortOrder, maxItems, headers, webRequest);
                MccObservation[] outA = new MccObservation[out.size()];
                outA = out.toArray(outA);
                return outA;
            }
        }


        //Right now this search is using the local expanded version of the value set
        //If the server supports code:in then it would be better to use that feature
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(valueset);
        if (valueSetObj != null) {

            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count", Integer.toString(maxItems));
            String baseQuery = "Observation.QueryValueSetExpanded";
            out = this.QueryObservations(baseQuery, mode, client, subjectId, sortOrder, unit, webRequest, headers, values);

        } else {
            throw new ItemNotFoundException("No such value set: " + valueset);

        }
        if (bLogCalls) {
            log.info("/observationsbyvalueset found " + out.size() + " items");
        }
        if (out.size() > maxItems) {
            out = out.subList(0, maxItems - 1);
        }
        MccObservation[] outA = new MccObservation[out.size()];
        outA = out.toArray(outA);
        return outA;
    }

    @GetMapping("/observationssegmented")
    public ObservationCollection getObservationsSegmented(@RequestParam(required = true, name = "subject") String subjectId, @RequestParam(required = true, name = "valueset") String valueset, @RequestParam(name = "max", defaultValue = "1000") int maxItems, @RequestParam(name = "sort", defaultValue = "ascending") String sortOrder, @RequestParam(name = "mode", defaultValue = "code") String mode, @RequestParam(required = false, name = "requiredunit") String unit, @RequestHeader Map<String, String> headers, WebRequest webRequest) {
        if (bLogCalls) {
            log.info("Get: /observationssegmented for " + subjectId + ", for valueset " + valueset);
        }


        ObservationCollection out;
        FHIRServices fhirSrv = FHIRServices.getFhirServices();
        IGenericClient client = fhirSrv.getClient(headers);

        ///Logic to override and calculate eGFR
        if (bcalcEgfr && valueset.compareTo(egfr_vsid) == 0) {
            Context ctx = contextManager.setupContext(subjectId, client, mapper, headers);
            if (canCalulateEgfr(client, subjectId, ctx, headers, webRequest)) {
                return efgrSegementedFromSrcr(client, subjectId, ctx, mode, sortOrder, maxItems, headers, webRequest);
            }
        }
        //Right now this search is using the local expanded version of the value s
        //If the server supports code:in then it would be better to use that feature
        MccValueSet valueSetObj = ValueSetManager.getValueSetManager().findValueSet(valueset);
        if (valueSetObj != null) {

            Map<String, String> values = new HashMap<>();
            values.put("codes", valueSetObj.asQueryString());
            values.put("count", Integer.toString(maxItems));
            String baseQuery = "Observation.QueryValueSetExpanded";
            out = this.QueryObservationsSegmented(baseQuery, mode, client, subjectId, sortOrder, unit, webRequest, headers, values);

        } else {
            throw new ItemNotFoundException("No such valaue set: " + valueset);
        }

        if (bLogCalls) {
            log.info("/observationssegmented found " + out.simpleCensus());
        }
        return out;
    }

    /**
     * Finds the queries that will match the request, for most modes this will be a single item
     *
     * @param query the Query Name
     * @param mode  the Mode (code, combo, panel, component)
     * @return a list of query keys
     */
    private List<String> getQueryStrings(String query, String mode) {
        //Normalize the mode (Combo, Code, Panel, Component)
        mode = mode.toLowerCase();
        //Prep Output
        ArrayList<String> out = new ArrayList<>();
        String key;
        //Search for matches
        if (mode.equals("combo")) {
            key = getKey(query, "combo");
            if (queryManager.doesQueryExist(key) == false) {
                key = getKey(query, "code");
                if (queryManager.doesQueryExist(key)) {
                    out.add(key);
                }
                key = getKey(query, "component");
                if (queryManager.doesQueryExist(key)) {
                    out.add(key);
                }
            } else {
                out.add(key);
            }

        } else {
            key = getKey(query, mode);
            if (queryManager.doesQueryExist(key)) {
                out.add(key);
            }
        }
        return out;
    }

    protected boolean includeObservation(Observation o, LinkedHashSet<String> unitSet) {
        if (unitSet == null) return true;
        if (o.hasValue() == false) return true;
        if (o.getValue().fhirType().compareTo(MccQuantity.fhirType) != 0) return true;
        Quantity q = o.getValueQuantity();
        if (q.hasUnit() == false) {
            Iterator<String> itr = unitSet.iterator();
            if (itr.hasNext()) {
                String defUnit = itr.next();
                q.setUnit(defUnit);
                return true;
            }
            if (bLogUnitFailure) {
                log.info("Observation suppressed, not units or default units present, code = " + o.getCode().getCodingFirstRep().getCode());
            }
            return false;
        }
        if (q.getUnit().isEmpty()) {
            String[] a = (String[]) unitSet.toArray();
            q.setUnit(a[0]);
            return true;
        }
        boolean out = unitSet.contains(q.getUnit());
        if (bLogUnitFailure && out == false) {
            log.info("Observation suppressed, unit " + q.getUnit() + " not in accepted set, code = " + o.getCode().getCodingFirstRep().getCode());
        }
        return out;
    }

    private boolean isBlackeGFRCode(String code) {
        switch (code) {
            case "48643-1":
            case "88293-6":
                return true;
            default:
                return false;
        }
    }

    private MccObservation notFound(String code) {
        MccObservation out = new MccObservation();
        out.setFHIRId("notfound");
        out.setStatus("notfound");
        MccCodeableConcept cc = MccHelper.conceptFromCode(code, null);
        out.setCode(cc);
        GenericType value = MccHelper.genericFromString("No Data Available");
        out.setValue(value);
        return out;
    }

    private class EgfrValues {
        public long Code882944;  //Glomerular filtration rate/1.73 sq M.predicted among non-blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)
        public long Code882936; //Glomerular filtration rate/1.73 sq M.predicted among blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (CKD-EPI)
        public MccObservation obs882944;
        public MccObservation obs882936;
    }

}
