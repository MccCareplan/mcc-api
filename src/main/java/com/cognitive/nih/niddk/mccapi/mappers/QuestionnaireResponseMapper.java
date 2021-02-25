package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class QuestionnaireResponseMapper implements IQuestionnaireResponseMapper{



    public  MccQuestionnaireResponse fhir2local(QuestionnaireResponse in, Context ctx) {
        MccQuestionnaireResponse out = new MccQuestionnaireResponse();
        IR4Mapper mapper = ctx.getMapper();

        out.setFHIRId(in.getIdElement().getIdPart());
        if (in.hasAuthored())
        {
            out.setAuthored(mapper.fhir2local(in.getAuthored(),ctx));
        }

        out.setStatus(in.getStatus().toCode());

        List<QuestionnaireResponse.QuestionnaireResponseItemComponent> inItems = in.getItem();
        if (inItems.size()>0) {
            ArrayList<QuestionnaireResponseItem> items = new ArrayList<>();
            for(QuestionnaireResponse.QuestionnaireResponseItemComponent rc : inItems)
            {
                //Since we want the items in order we ask the fhir2local to handle the add
               items.add(fhir2local(rc,ctx));
            }
            QuestionnaireResponseItem[] outA = new QuestionnaireResponseItem[items.size()];
            outA = items.toArray(outA);
            out.setItems(outA);
        }

        return out;
    }
    public  SimpleQuestionnaireItem fhir2SimpleItem(QuestionnaireResponse in, Context ctx, String linkId)
    {
        SimpleQuestionnaireItem out = new SimpleQuestionnaireItem();
        IR4Mapper mapper = ctx.getMapper();

        out.setFHIRId(in.getIdElement().getIdPart());
        if (in.hasAuthored())
        {
            out.setAuthored(mapper.fhir2local(in.getAuthored(),ctx));
        }
        if (in.hasItem()) {
            QuestionnaireResponseItem fnd = findItem(in, linkId, ctx);
            if (fnd != null)
                out.setItem(fnd);
        }
        return out;
    }

    public QuestionnaireResponseItem findItem(QuestionnaireResponse in, String linkId, Context ctx)
    {
        QuestionnaireResponseItem fnd=null;
        List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items = in.getItem();
        for(QuestionnaireResponse.QuestionnaireResponseItemComponent item: items)
        {
            fnd = findItem(item, linkId, ctx);
            if (fnd != null) break;
        }
        return fnd;
    }

    public QuestionnaireResponseItem findItem(QuestionnaireResponse.QuestionnaireResponseItemComponent in, String linkId, Context ctx)
    {
        QuestionnaireResponseItem fnd=null;
        if (in.getLinkId().compareTo(linkId)==0)
        {
            fnd = fhir2local(in, ctx);
        }
        else
        {
            //Try searching the anwsers
            if (in.hasAnswer()) {
                List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answers = in.getAnswer();
                searchLoop:
                for (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer : answers)
                {
                    if (answer.hasItem())
                    {
                        for  (QuestionnaireResponse.QuestionnaireResponseItemComponent  item: answer.getItem())
                        {
                            fnd = findItem(item,linkId, ctx);
                            if (fnd != null) break searchLoop;;
                        }
                    }
                }
            }

            if(fnd == null && in.hasItem()){
                for (QuestionnaireResponse.QuestionnaireResponseItemComponent  item: in.getItem())
                {
                    fnd = findItem(item,linkId, ctx);
                    if (fnd != null) break;
                }
            }
        }
        return fnd;
    }

    public  QuestionnaireResponseItem fhir2local(QuestionnaireResponse.QuestionnaireResponseItemComponent in, Context ctx) {
        QuestionnaireResponseItem out = new QuestionnaireResponseItem();

        out.setLinkid(in.getLinkId());
        if (in.hasText())
        {
            out.setText(in.getText());
        }
        if (in.hasAnswer())
        {

            ArrayList<QuestionnaireResponseItemAnswer> answers = new ArrayList<>();
            for(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer : in.getAnswer())
            {
                answers.add(fhir2local(answer, ctx));
            }
            QuestionnaireResponseItemAnswer[] outA = new QuestionnaireResponseItemAnswer[answers.size()];
            outA = answers.toArray(outA);
            out.setAnswers(outA);

        }
        if (in.hasItem())
        {
            ArrayList<QuestionnaireResponseItem> items = new ArrayList<>();
            for(QuestionnaireResponse.QuestionnaireResponseItemComponent rc : in.getItem())
            {
                //Since we want the items in order we ask the fhir2local to handle the add
                items.add(fhir2local(rc,ctx));
            }
            QuestionnaireResponseItem[] outA = new QuestionnaireResponseItem[items.size()];
            outA = items.toArray(outA);
            out.setItems(outA);
        }
        return out;
    }

    public QuestionnaireResponseItemAnswer fhir2local(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in, Context ctx) {
        QuestionnaireResponseItemAnswer out = new QuestionnaireResponseItemAnswer();
        IR4Mapper mapper = ctx.getMapper();
        if (in.hasValue())
        {
            out.setValue(mapper.fhir2local(in.getValue(),ctx));
        }
        if (in.hasItem())
        {
            ArrayList<QuestionnaireResponseItem> items = new ArrayList<>();
            for(QuestionnaireResponse.QuestionnaireResponseItemComponent rc : in.getItem())
            {
                //Since we want the items in order we ask the fhir2local to handle the add
                items.add(fhir2local(rc,ctx));
            }
            QuestionnaireResponseItem[] outA = new QuestionnaireResponseItem[items.size()];
            outA = items.toArray(outA);
            out.setItems(outA);
        }
        return out;
    }

    public QuestionnaireResponseSummary fhir2summary(QuestionnaireResponse in, Context ctx) {
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
