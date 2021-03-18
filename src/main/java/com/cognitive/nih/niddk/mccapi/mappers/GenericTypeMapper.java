package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.data.primative.*;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * TODO:   Switch this to instance based mechanism, abstract interface, handle fhir version
 *
 *  Likely operations like - getMapperForR4. getMapperForR5 etc.
 *  Consumers will need to know HAPI input model in anycase
 *
 */

@Slf4j
@Component
public class GenericTypeMapper implements IGenericTypeMapper {


    private static final int RANGE = 0;
    private static final int QUANTITY = 1;
    private static final int INTEGER = 2;
    private static final int CODEABLE_CONCEPT = 3;
    private static final int STRING = 4;
    private static final int BOOLEAN = 5;
    private static final int RATIO = 6;
    private static final int REFERENCE = 7;
    private static final int PERIOD = 8;
    private static final int DATE = 9;
    private static final int TIME = 10;
    private static final int DATETIME = 11;
    private static final int SAMPLED_DATA = 12;
    private static final int SIMPLE_QUANTITY = 13;
    private static final int TIMING = 14;
    private static final int DURATION = 15;
    private static final int INSTANT = 16;
    private static final int IDENTIFIER = 17;
    private static final int ID = 18;
    private static final int CODING = 19;
    private static final int DECIMAL = 20;

    private final ICodingMapper codingMapper;
    private final ICodeableConceptMapper codeableConceptMapper;

    private static HashMap<String, Integer> activeKeys = new HashMap<>();

    static {

        activeKeys.put(MccRange.fhirType, RANGE);
        activeKeys.put(MccQuantity.fhirType, QUANTITY);
        activeKeys.put("integer", INTEGER);
        activeKeys.put(MccCodeableConcept.fhirType, CODEABLE_CONCEPT);
        activeKeys.put("string", STRING);
        activeKeys.put("boolean", BOOLEAN);
        activeKeys.put(MccRatio.fhirType, RATIO);
        activeKeys.put(MccReference.fhirType, REFERENCE);
        activeKeys.put(MccPeriod.fhirType, PERIOD);
        activeKeys.put(MccDate.fhirType, DATE);
        activeKeys.put(MccTime.fhirType, TIME);
        activeKeys.put(MccDateTime.fhirType, DATETIME);
        activeKeys.put("SampledData", SAMPLED_DATA);
        activeKeys.put(MccSimpleQuantity.fhirType, SIMPLE_QUANTITY);
        activeKeys.put(MccDuration.fhirType, DURATION);
        activeKeys.put(MccTiming.fhirType, TIMING);
        activeKeys.put(MccInstant.fhirType, INSTANT);
        activeKeys.put(MccIdentifer.fhirType, IDENTIFIER);
        activeKeys.put(MccId.fhirType, IDENTIFIER);
        activeKeys.put(MccCoding.fhirType, CODING);
        activeKeys.put("decimal",DECIMAL);
    }

    public GenericTypeMapper(ICodingMapper codingMapper, ICodeableConceptMapper codeableConceptMapper) {
        this.codingMapper = codingMapper;
        this.codeableConceptMapper = codeableConceptMapper;
    }


    public GenericType fhir2local(Type in, Context ctx) {
        GenericType out = new GenericType();
        String fhirType = in.fhirType();
        out.setValueType(fhirType);
        Integer localType = activeKeys.get(fhirType);
        if (localType != null) {
            switch (localType.intValue()) {
                case PERIOD: {
                    out.setPeriodValue(fhir2local(in.castToPeriod(in), ctx));
                    break;
                }
                case RANGE: {
                    out.setRangeValue(fhir2local(in.castToRange(in), ctx));
                    break;
                }
                case RATIO: {
                    out.setRatioValue(fhir2local(in.castToRatio(in), ctx));
                    break;
                }
                case QUANTITY: {
                    out.setQuantityValue(fhir2local(in.castToQuantity(in), ctx));
                    break;
                }
                case CODEABLE_CONCEPT: {
                    out.setCodeableConceptValue(ctx.getMapper().fhir2local(in.castToCodeableConcept(in), ctx));
                    break;
                }
                case BOOLEAN: {
                    out.setBooleanValue(in.castToBoolean(in).booleanValue());
                    break;
                }
                case STRING: {
                    out.setStringValue(in.castToString(in).asStringValue());
                    break;
                }
                case INTEGER: {
                    out.setIntegerValue(in.castToInteger(in).getValue().intValue());
                    break;
                }
                case DATE: {
                    out.setDateValue(fhir2local(in.castToDate(in), ctx));
                    break;
                }
                case TIME: {
                    out.setTimeValue(fhir2local(in.castToTime(in), ctx));
                    break;
                }
                case DATETIME: {
                    out.setDateTimeValue(fhir2local(in.castToDateTime(in), ctx));
                    break;
                }
                case ID: {
                    out.setIdValue(fhir2local(in.castToId(in),ctx));
                    break;
                }
                case SAMPLED_DATA: {
                    out.setSampledDataValue(fhir2local(in.castToSampledData(in), ctx));
                    break;
                }
                case SIMPLE_QUANTITY: {
                    out.setQuantityValue(fhir2local(in.castToQuantity(in), ctx));
                    break;
                }
                case DURATION: {
                    out.setDurationValue(fhir2local(in.castToDuration(in), ctx));
                    break;
                }
                case TIMING: {
                    out.setTimingValue(fhir2local(in.castToTiming(in), ctx));
                    break;
                }
                case INSTANT: {
                    out.setInstantValue(fhir2local(in.castToInstant(in), ctx));
                    break;
                }
                case IDENTIFIER: {
                    out.setIdentiferValue(fhir2local(in.castToIdentifier(in), ctx));
                    break;
                }
                case CODING:
                {
                    out.setCodingValue(fhir2local(in.castToCoding(in),ctx));
                    break;
                }
                case DECIMAL:
                {
                    out.setDecimalValue(in.castToDecimal(in).getValue());
                    break;
                }
                default: {
                    log.warn("Type {} regnogized but not yet handled",fhirType);
                    break;
                }
            }
        } else {
            log.warn("Unmapped type {}, ignoring", fhirType);
        }

        return out;
    }

    public MccDate fhir2local(DateType in, Context ctx) {
        MccDate out = new MccDate();
        out.setRawDate(in.getValue());
        out.setDate(FHIRHelper.dateToString(in.getValue()));
        return out;
    }

    public MccId fhir2local(IdType in, Context ctx) {
        MccId out = new MccId();
        out.setValue(in.getValue());
        return out;
    }

    public MccDate fhir2local(Date in, Context ctx) {
        MccDate out = new MccDate();
        out.setRawDate(in);
        out.setDate(FHIRHelper.dateToString(in));
        return out;
    }

    public MccCoding fhir2local(Coding in, Context ctx) {
        return codingMapper.fhir2local(in,ctx);
        //MccCoding out = new MccCoding();
        //out.setCode(in.getCode());
        //out.setDisplay(in.getDisplay());
        //out.setSystem(in.getSystem());
        //out.setVersion(in.getVersion());
        //return out;
    }

    public MccIdentifer[] fhir2local_identifierArray(List<Identifier> in, Context ctx) {
        MccIdentifer[] o = new MccIdentifer[in.size()];
        int i = 0;
        for (Identifier id : in) {
            o[i] = fhir2local(id, ctx);
            i++;
        }
        return o;
    }

    public MccDateTime[] fhir2local(List<DateTimeType> in, Context ctx) {
        MccDateTime[] o = new MccDateTime[in.size()];
        int i = 0;
        for (DateTimeType dateTime : in) {
            o[i] = fhir2local(dateTime, ctx);
            i++;
        }
        return o;
    }

    public MccDateTime fhir2local(DateTimeType in, Context ctx) {
        MccDateTime out = new MccDateTime();
        out.setRawDate(in.getValue());
        out.setDate(FHIRHelper.dateTimeToString(in.getValue()));
        return out;
    }

    public MccTime fhir2local(TimeType in, Context ctx) {
        MccTime out = new MccTime();

        out.setValue(in.getValue());
        return out;
    }

    public MccPeriod fhir2local(Period in, Context ctx) {
        MccPeriod out = new MccPeriod();
        in.fhirType();
        Period p = in.castToPeriod(in);
        out.setStart(fhir2local(p.getStart(), ctx));
        out.setEnd(fhir2local(p.getEnd(), ctx));
        return out;
    }

    public MccSampledData fhir2local(SampledData in, Context ctx) {
        MccSampledData out = new MccSampledData();
        out.setOrigin(fhir2local((SimpleQuantity) in.getOrigin(), ctx));
        out.setData(in.getData());
        out.setDimensions(in.getDimensions());
        out.setFactor(in.getFactor());
        out.setLowerlimit(in.getLowerLimit());
        out.setUpperlimit(in.getUpperLimit());
        out.setPeriod(in.getPeriod());
        return out;
    }

    public MccSimpleQuantity fhir2local(SimpleQuantity in, Context ctx) {
        MccSimpleQuantity out = new MccSimpleQuantity();
        out.setCode(in.getCode());
        out.setSystem(in.getSystem());
        out.setUnit(in.getUnit());
        out.setValue(in.getValue());
        out.setDisplay(in.getDisplay());
        return out;
    }

    public MccIdentifer fhir2local(Identifier in, Context ctx) {
        MccIdentifer out = new MccIdentifer();
        if (in.hasUse()) {
            out.setUse(in.getUse().toCode());
        }
        if (in.hasType()) {
            out.setType(fhir2local(in.getType(), ctx));
        }
        out.setSystem(in.getSystem());
        out.setValue(in.getValue());
        if (in.hasPeriod()) {
            out.setPeriod(fhir2local(in.getPeriod(), ctx));
        }
        if (in.hasAssigner()) {
            out.setAssigner(fhir2local(in.getAssigner(), ctx));
        }

        return out;
    }

    public MccReference fhir2local(Reference in, Context ctx) {
        return ctx.getMapper().fhir2local(in, ctx);
    }

    public MccCodeableConcept fhir2local(CodeableConcept in, Context ctx) {
        return codeableConceptMapper.fhir2local(in, ctx);
    }

    public MccQuantity fhir2local(Quantity in, Context ctx) {
        MccQuantity out = new MccQuantity();
        out.setCode(in.getCode());
        if (in.getComparator() != null) {
            out.setComparator(in.getComparator().toCode());
        }
        out.setSystem(in.getSystem());
        out.setUnit(in.getUnit());
        out.setValue(in.getValue());
        out.setDisplay(in.getDisplay());
        return out;
    }

    public MccRatio fhir2local(Ratio in, Context ctx) {
        MccRatio out = new MccRatio();
        out.setDenominator(fhir2local(in.getDenominator(), ctx));
        out.setNumerator(fhir2local(in.getNumerator(), ctx));
        return out;
    }

    public MccRange fhir2local(Range in, Context ctx) {
        MccRange out = new MccRange();
        out.setLow(fhir2local(in.getLow(), ctx));
        out.setHigh(fhir2local(in.getHigh(), ctx));
        return out;
    }

    public MccDuration fhir2local(Duration in, Context ctx) {
        MccDuration out = new MccDuration();
        out.setCode(in.getCode());
        if (in.getComparator() != null) {
            out.setComparator(in.getComparator().toCode());
        }
        out.setSystem(in.getSystem());
        out.setUnit(in.getUnit());
        out.setValue(in.getValue());
        out.setDisplay(in.getDisplay());
        return out;
    }

    public MccInstant fhir2local(InstantType in, Context ctx) {
        MccInstant out = new MccInstant();
        out.setValue(in.getValueAsString());
        return out;
    }

    public MccTiming fhir2local(Timing in, Context ctx) {
          MccTiming out = new MccTiming();
        if (in.hasEvent()) {
            out.setEvent(fhir2local(in.getEvent(), ctx));
        }
        if (in.hasCode()) {
            out.setCode(ctx.getMapper().fhir2local(in.getCode(), ctx));
        }
        if (in.hasRepeat()) {
            MccTiming.Repeat mccRepeat = out.defineRepeat();
            Timing.TimingRepeatComponent repeat = in.getRepeat();
            if (repeat.hasBounds()) {
                MccTiming.Repeat.Bounds mccBounds = mccRepeat.defineBounds();
                if (repeat.hasBoundsDuration()) {
                    mccBounds.setDuration(fhir2local(repeat.getBoundsDuration(), ctx));
                } else if (repeat.hasBoundsPeriod()) {
                    mccBounds.setPeriod(fhir2local(repeat.getBoundsPeriod(), ctx));
                } else if (repeat.hasBoundsRange()) {
                    mccBounds.setRange(fhir2local(repeat.getBoundsRange(), ctx));
                }
            }
            if (repeat.hasCount())
            {
                mccRepeat.setCount(Integer.valueOf(repeat.getCount()));
            }
            if (repeat.hasCountMax()) {
                mccRepeat.setCountMax(Integer.valueOf(repeat.getCountMax()));
            }
            if (repeat.hasDuration()) {
                mccRepeat.setDuration(repeat.getDuration().toPlainString());
            }
            if (repeat.hasDurationMax()) {
                mccRepeat.setDurationMax(repeat.getDurationMax().toPlainString());
            }
            if (repeat.hasDurationUnit()) {
                mccRepeat.setDurationUnit(repeat.getDurationUnit().toCode());
            }
            if (repeat.hasFrequency()) {
                mccRepeat.setFrequency(Integer.valueOf(repeat.getFrequency()));
            }
            if (repeat.hasFrequencyMax()) {
                mccRepeat.setFrequencyMax(Integer.valueOf(repeat.getFrequencyMax()));
            }
            if (repeat.hasPeriod()) {
                mccRepeat.setPeriod(repeat.getPeriod().toPlainString());
            }
            if (repeat.hasPeriodMax()) {
                mccRepeat.setPeriodMax(repeat.getPeriodMax().toEngineeringString());
            }
            if (repeat.hasPeriodUnit()) {
                mccRepeat.setPeriodUnit(repeat.getPeriodUnit().toCode());
            }
            if (repeat.hasDayOfWeek()) {
                List<Enumeration<Timing.DayOfWeek>> list = repeat.getDayOfWeek();
                String[] days = new String[list.size()];
                int i = 0;
                for (Enumeration<Timing.DayOfWeek> t : list) {
                    days[i] = t.getCode();
                    i++;
                }
                mccRepeat.setDayOfWeek(days);
            }
            if (repeat.hasTimeOfDay()) {
                List<TimeType> list = repeat.getTimeOfDay();
                MccTime[] times = new MccTime[list.size()];
                int i = 0;
                for (TimeType t : list) {
                    times[i] = fhir2local(t, ctx);
                    i++;
                }
                mccRepeat.setTimeOfDay(times);
            }
            if (repeat.hasWhen()) {
                List<Enumeration<Timing.EventTiming>> list = repeat.getWhen();
                String when[] = new String[list.size()];
                int i = 0;
                for (Enumeration<Timing.EventTiming> w : list) {
                    when[i] = w.getCode();
                    i++;
                }
            }
            mccRepeat.setReadable(FHIRHelper.translateRepeat(repeat));
            if (repeat.hasOffset())
            {
                mccRepeat.setOffset(Integer.valueOf(repeat.getOffset()));
            }
        }
        out.setReadable(FHIRHelper.translateTiming(in));
        return out;

    }

    public MccDosage[] fhir2local_dosageList(List<Dosage> in, Context ctx) {
        MccDosage[] o = new MccDosage[in.size()];
        int i = 0;
        for (Dosage dosage : in) {
            o[i] = fhir2local(dosage, ctx);
            i++;
        }
        return o;

    }

    public MccDosage fhir2local(Dosage in, Context ctx) {
        MccDosage out = new MccDosage();

        if (in.hasSequence())
        {
            out.setSequence(Integer.valueOf(in.getSequence()));
        }
        if (in.hasAdditionalInstruction())
        {
            out.setAdditionInstructions(ctx.getMapper().fhir2local(in.getAdditionalInstruction(),ctx));
        }
        if (in.hasText())
        {
            out.setText(in.getText());
        }
        if (in.hasPatientInstruction())
        {
            out.setPatientInstructions(in.getPatientInstruction());
        }
        if (in.hasTiming())
        {
            out.setTiming(fhir2local(in.getTiming(),ctx));
        }
        if (in.hasDoseAndRate())
        {
            List<Dosage.DosageDoseAndRateComponent> list = in.getDoseAndRate();
            MccDosage.DoseAndRate[] outl = out.createDoseAndRateArray(list.size());
            int i =0;
            for (Dosage.DosageDoseAndRateComponent dr : list)
            {
                MccDosage.DoseAndRate odr = outl[i];
                i++;
                if (dr.hasType()) {
                    odr.setType(fhir2local(dr.getType(),ctx));
                }
                if (dr.hasRate())
                {
                    if (dr.hasRateQuantity())
                    {
                        odr.setRateQuantity(fhir2local((SimpleQuantity)dr.getRateQuantity(),ctx));
                    }
                    else if (dr.hasRateRange())
                    {
                        odr.setRateRange(fhir2local(dr.getRateRange(),ctx));
                    }
                    else if (dr.hasRateRatio())
                    {
                        odr.setRateRatio(fhir2local(dr.getRateRatio(),ctx));
                    }
                }
                if (dr.hasDose())
                {
                    if (dr.hasDoseQuantity())
                    {
                        odr.setDoseQuantity(fhir2local(dr.getDoseQuantity(),ctx));
                    }
                    else if (dr.hasDoseRange())
                    {
                        odr.setDoseRange(fhir2local(dr.getDoseRange(),ctx));
                    }
                }
            }

        }
        if (in.hasAsNeeded())
        {
            if (in.hasAsNeededBooleanType())
            {
                out.setAsNeededBoolean(in.getAsNeededBooleanType().getValue());
            }
            else if (in.hasAsNeededCodeableConcept())
            {
                out.setAsNeededCodableConcept(fhir2local(in.getAsNeededCodeableConcept(),ctx));
            }
        }
        if (in.hasSite())
        {
            out.setSite(fhir2local(in.getSite(),ctx));
        }
        if (in.hasRoute())
        {
            out.setRoute(fhir2local(in.getRoute(),ctx));
        }
        if (in.hasMethod())
        {
            out.setMethod(fhir2local(in.getMethod(),ctx));
        }

        if (in.hasMaxDosePerAdministration())
        {
            out.setMaxDosePerAdministration(fhir2local((SimpleQuantity)in.getMaxDosePerAdministration(),ctx));
        }
        if (in.hasMaxDosePerPeriod())
        {
            out.setMaxDosePerPeriod(fhir2local(in.getMaxDosePerPeriod(),ctx));
        }
        if (in.hasMaxDosePerLifetime())
        {
            out.setMaxDosePerLifetime(fhir2local((SimpleQuantity)in.getMaxDosePerLifetime(),ctx));
        }
        return out;
    }
}
