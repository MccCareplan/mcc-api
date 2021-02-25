package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.ConditionHistory;
import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccCondition;
import org.hl7.fhir.r4.model.Condition;

public interface IConditionMapper {
    MccCondition fhir2local(Condition in, Context ctx);
    ConditionHistory fhir2History(Condition in, Context ctx);
}
