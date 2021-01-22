package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import java.util.List;

public class QuestionnaireResponseMapper {

    public static MccQuestionnaireResponse fhir2local(QuestionnaireResponse in, Context ctx) {
        MccQuestionnaireResponse out = new MccQuestionnaireResponse();

        out.setFHIRId(in.getIdElement().getIdPart());
        if (in.hasAuthored())
        {
            out.setAuthored(in.getAuthored());
        }

        out.setStatus(in.getStatus().toCode());

        List<QuestionnaireResponse.QuestionnaireResponseItemComponent> x = in.getItem();
        return out;
    }

    public static QuestionnaireResponseItem fhir2local(QuestionnaireResponse.QuestionnaireResponseItemComponent in, Context ct, String path) {
        QuestionnaireResponseItem out = new QuestionnaireResponseItem();

        return out;
    }

    public static QuestionnaireResponseSummary fhir2summary(QuestionnaireResponse in, Context ctx) {
        QuestionnaireResponseSummary out = new QuestionnaireResponseSummary();

        out.setFHIRId(in.getIdElement().getIdPart());
        if (in.hasAuthored())
        {
            out.setAuthored(in.getAuthored());
        }

        out.setStatus(in.getStatus().toCode());

        return out;
    }


}
