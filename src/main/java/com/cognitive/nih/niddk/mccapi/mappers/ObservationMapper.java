package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.ObservationComponent;
import com.cognitive.nih.niddk.mccapi.data.ReferenceRange;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ObservationMapper implements IObservationMapper {

    public MccObservation fhir2local(Observation in, Context ctx) {
        MccObservation out = new MccObservation();
        IR4Mapper mapper = ctx.getMapper();

        out.setFHIRId(in.getIdElement().getIdPart());
        out.setCode(mapper.fhir2local(in.getCode(),ctx));
        if (in.hasBasedOn())
        {
            out.setBasedOn(mapper.fhir2local_referenceArray(in.getBasedOn(),ctx));
        }
        out.setStatus(in.getStatus().toCode());
        if (in.hasEffective())
        {
            MccObservation.Effective effective = out.defineEffective();

            if (in.hasEffectiveDateTimeType())
            {
                effective.setDateTime(mapper.fhir2local(in.getEffectiveDateTimeType(),ctx));
            }
            else if (in.hasEffectiveTiming())
            {
                effective.setTiming(mapper.fhir2local(in.getEffectiveTiming(),ctx));
            }
            else if (in.hasEffectiveInstantType())
            {
                effective.setInstant(mapper.fhir2local(in.getEffectiveInstantType(),ctx));
            }
            else if (in.hasEffectivePeriod())
            {
                effective.setPeriod(mapper.fhir2local(in.getEffectivePeriod(),ctx));
            }
        }
        if (in.hasValue())
        {
            out.setValue(mapper.fhir2local(in.getValue(),ctx));
        }
        if (in.hasNote())
        {
            out.setNote(FHIRHelper.annotationsToString(in.getNote(),ctx));

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
            out.setDataAbsentReason(mapper.fhir2local(in.getDataAbsentReason(),ctx));
        }
        if (in.hasCategory())
        {
            out.setCategory(mapper.fhir2local(in.getCategory(),ctx));
        }
        return out;
    }

    public ObservationComponent fhir2local(Observation.ObservationComponentComponent in, Context ctx) {
        ObservationComponent out = new ObservationComponent();
        IR4Mapper mapper = ctx.getMapper();

        if (in.hasCode()) {
            out.setCode(mapper.fhir2local(in.getCode(), ctx));
        }
        if (in.hasDataAbsentReason()) {
            out.setDataAbsentReason(mapper.fhir2local(in.getDataAbsentReason(), ctx));
        }
        if (in.hasValue()) {
            out.setValue(mapper.fhir2local(in.getValue(), ctx));
        }
        if (in.hasReferenceRange())
        {
            out.setReferenceRanges(fhir2local(in.getReferenceRange(),ctx));
        }
        if (in.hasInterpretation())
        {
            out.setInterpretation(mapper.fhir2local(in.getInterpretation(),ctx));
        }
        return out;
    }

    public ReferenceRange fhir2local(Observation.ObservationReferenceRangeComponent in, Context ctx) {
        ReferenceRange out = new ReferenceRange();
        IR4Mapper mapper = ctx.getMapper();

        if (in.hasLow())
        {
            out.setLow(mapper.fhir2local((Quantity) in.getLow(),ctx));
        }
        if (in.hasHigh())
        {
            out.setLow(mapper.fhir2local((Quantity) in.getHigh(),ctx));
        }
        if (in.hasType())
        {
            out.setType(mapper.fhir2local(in.getType(),ctx));
        }
        if (in.hasAppliesTo())
        {
            out.setAppliesTo(mapper.fhir2local(in.getAppliesTo(),ctx));
        }
        if (in.hasAge())
        {
            out.setAge(mapper.fhir2local(in.getAge(),ctx));
        }
        out.setText(in.getText());


        return out;
    }

    public  ReferenceRange[] fhir2local(List<Observation.ObservationReferenceRangeComponent> in, Context ctx)
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
