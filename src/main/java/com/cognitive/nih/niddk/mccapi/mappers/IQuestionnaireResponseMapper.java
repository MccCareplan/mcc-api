package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

public interface IQuestionnaireResponseMapper {
    MccQuestionnaireResponse fhir2local(QuestionnaireResponse in, Context ctx);
    SimpleQuestionnaireItem fhir2SimpleItem(QuestionnaireResponse in, Context ctx, String linkId);
    QuestionnaireResponseItem findItem(QuestionnaireResponse in, String linkId, Context ctx);
    QuestionnaireResponseItem findItem(QuestionnaireResponse.QuestionnaireResponseItemComponent in, String linkId, Context ctx);
    QuestionnaireResponseItem fhir2local(QuestionnaireResponse.QuestionnaireResponseItemComponent in, Context ctx);
    QuestionnaireResponseItemAnswer fhir2local(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in, Context ctx);
    QuestionnaireResponseSummary fhir2summary(QuestionnaireResponse in, Context ctx);
}
