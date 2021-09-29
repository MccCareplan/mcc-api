/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccObservation;
import com.cognitive.nih.niddk.mccapi.data.ObservationComponent;
import com.cognitive.nih.niddk.mccapi.data.SimpleQuestionnaireItem;
import org.hl7.fhir.r4.model.Observation;

public interface IObservationMapper {
    MccObservation fhir2local(Observation in, Context ctx);
    ObservationComponent fhir2local(Observation.ObservationComponentComponent in, Context ctx);
    SimpleQuestionnaireItem fhir2SimpleItem(Observation in, Context ctx, String linkId);
}
