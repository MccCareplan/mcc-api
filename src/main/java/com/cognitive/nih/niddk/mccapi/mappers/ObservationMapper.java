package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.ObservationComponent;
import com.cognitive.nih.niddk.mccapi.data.ReferenceRange;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.SimpleQuantity;

import java.util.List;

public class ObservationMapper {

    public static MccObservation fhir2local(Observation in, Context ctx) {
        MccObservation out = new MccObservation();

        out.setFHIRId(in.getIdElement().getIdPart());
        out.setCode(CodeableConceptMapper.fhir2local(in.getCode(),ctx));
        if (in.hasBasedOn())
        {
            out.setBasedOn(ReferenceMapper.fhir2local(in.getBasedOn(),ctx));
        }
        out.setStatus(in.getStatus().toCode());
        if (in.hasEffective())
        {
            MccObservation.Effective effective = out.defineEffective();

            if (in.hasEffectiveDateTimeType())
            {
                effective.setDateTime(GenericTypeMapper.fhir2local(in.getEffectiveDateTimeType(),ctx));
            }
            else if (in.hasEffectiveTiming())
            {
                effective.setTiming(GenericTypeMapper.fhir2local(in.getEffectiveTiming(),ctx));
            }
            else if (in.hasEffectiveInstantType())
            {
                effective.setInstant(GenericTypeMapper.fhir2local(in.getEffectiveInstantType(),ctx));
            }
            else if (in.hasEffectivePeriod())
            {
                effective.setPeriod(GenericTypeMapper.fhir2local(in.getEffectivePeriod(),ctx));
            }
        }
        if (in.hasValue())
        {
            out.setValue(GenericTypeMapper.fhir2local(in.getValue(),ctx));
        }
        if (in.hasNote())
        {
            out.setNote(Helper.AnnotationsToString(in.getNote()));

        }
        if (in.hasReferenceRange()) {
            out.setReferenceRanges(fhir2local(in.getReferenceRange(),ctx));
        }
        if (in.hasComponent()) {
            List<Observation.ObservationComponentComponent> comps = in.getComponent();
            ObservationComponent[] ocomps = new ObservationComponent[comps.size()];
            int i = 0;
            for (Observation.ObservationComponentComponent comp: comps)
            {
                ocomps[i] = fhir2local(comp,ctx);
                i++;
            }
            out.setComponents(ocomps);
        }
        if (in.hasDataAbsentReason())
        {
            out.setDataAbsentReason(GenericTypeMapper.fhir2local(in.getDataAbsentReason(),ctx));
        }
        if (in.hasCategory())
        {
            out.setCategory(CodeableConceptMapper.fhir2local(in.getCategory(),ctx));
        }
        return out;
    }

    public static ObservationComponent fhir2local(Observation.ObservationComponentComponent in, Context ctx) {
        ObservationComponent out = new ObservationComponent();
        if (in.hasCode()) {
            out.setCode(GenericTypeMapper.fhir2local(in.getCode(), ctx));
        }
        if (in.hasDataAbsentReason()) {
            out.setDataAbsentReason(GenericTypeMapper.fhir2local(in.getDataAbsentReason(), ctx));
        }
        if (in.hasValue()) {
            out.setValue(GenericTypeMapper.fhir2local(in.getValue(), ctx));
        }
        if (in.hasReferenceRange())
        {
            out.setReferenceRanges(fhir2local(in.getReferenceRange(),ctx));
        }
        if (in.hasInterpretation())
        {
            out.setInterpretation(CodeableConceptMapper.fhir2local(in.getInterpretation(),ctx));
        }
        return out;
    }

    public static ReferenceRange fhir2local(Observation.ObservationReferenceRangeComponent in, Context ctx) {
        ReferenceRange out = new ReferenceRange();

        if (in.hasLow())
        {
            out.setLow(GenericTypeMapper.fhir2local((SimpleQuantity) in.getLow(),ctx));
        }
        if (in.hasHigh())
        {
            out.setLow(GenericTypeMapper.fhir2local((SimpleQuantity) in.getHigh(),ctx));
        }
        if (in.hasType())
        {
            out.setType(GenericTypeMapper.fhir2local(in.getType(),ctx));
        }
        if (in.hasAppliesTo())
        {
            out.setAppliesTo(CodeableConceptMapper.fhir2local(in.getAppliesTo(),ctx));
        }
        if (in.hasAge())
        {
            out.setAge(GenericTypeMapper.fhir2local(in.getAge(),ctx));
        }
        out.setText(in.getText());


        return out;
    }

    public static ReferenceRange[] fhir2local(List<Observation.ObservationReferenceRangeComponent> in, Context ctx)
    {
        ReferenceRange[] out = new ReferenceRange[in.size()];
        int i = 0;
        for (Observation.ObservationReferenceRangeComponent rr: in)
        {
            out[i] = fhir2local(rr,ctx);
            i++;
        }
        return out;

    }
}
