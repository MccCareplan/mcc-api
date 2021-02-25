package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.ConditionHistory;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.primative.FuzzyDate;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import com.cognitive.nih.niddk.mccapi.managers.ProfileManager;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConditionMapper implements IConditionMapper {

    public  MccCondition fhir2local(Condition in, Context ctx)
    {
        IR4Mapper mapper = ctx.getMapper();
        MccCondition out = new MccCondition();
        if (in != null) {
            out.setFHIRId(in.getIdElement().getIdPart());
            out.setCode(mapper.fhir2local(in.getCode(), ctx));
            out.setCategories(mapper.fhir2local(in.getCategory(), ctx));
            out.setSeverity(mapper.fhir2local(in.getSeverity(), ctx));
            out.setClinicalStatus(mapper.fhir2local(in.getClinicalStatus(), ctx));

            if (in.hasAbatement()) {
                out.setAbatement(FuzzyDate.buildString(in.getAbatement(), ctx));
            }
            if (in.hasOnset()) {
                out.setOnset(FuzzyDate.buildString(in.getOnset(), ctx));
            }
            if (in.hasNote()) {
                out.setNote(FHIRHelper.annotationsToString(in.getNote(),ctx));
            }
            if (in.hasAsserter()) {
                out.setAsserter(mapper.fhir2local(in.getAsserter(), ctx));
            }
            if (in.hasRecordedDate()) {
                out.setRecordedDate(mapper.fhir2local(in.getRecordedDate(), ctx));
            }
            if (in.hasRecorder()) {
                out.setRecorder(mapper.fhir2local(in.getRecorder(), ctx));
            }
            if (in.hasIdentifier())
            {
                out.setIdentifer(mapper.fhir2local_identifierArray(in.getIdentifier(),ctx));
            }
            //Find what if any profile we have for this
            out.setProfileId(ProfileManager.getProfileManager().getProfilesForConcept(in.getCode()));
        }
        return out;
    }

    public ConditionHistory fhir2History(Condition in, Context ctx)
    {
        ConditionHistory out = new ConditionHistory();
        IR4Mapper mapper = ctx.getMapper();
        out.setFHIRid(in.getIdElement().getIdPart());
        out.setClinicalStatus(in.getClinicalStatus().getCodingFirstRep().getCode());
        out.setVerificationStatus(in.getVerificationStatus().getCodingFirstRep().getCode());
        if (in.hasAbatement()) {
            out.setAbatement(FuzzyDate.buildString(in.getAbatement(), ctx));
            out.setAbatementDate(new FuzzyDate(in.getAbatement()));
        }
        if (in.hasOnset()) {
            out.setOnset(FuzzyDate.buildString(in.getOnset(), ctx));
            out.setOnsetDate(new FuzzyDate(in.getOnset()));
        }
        out.setCategoriesList(in.getCategory());
        out.setCode(mapper.fhir2local(in.getCode(),ctx));
        return out;
    }

}
