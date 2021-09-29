/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.GoalSummary;
import com.cognitive.nih.niddk.mccapi.data.GoalTarget;
import com.cognitive.nih.niddk.mccapi.data.MccGoal;
import org.hl7.fhir.r4.model.Goal;

public interface IGoalMapper {
    MccGoal fhir2local(Goal in, Context ctx);
    GoalSummary fhir2summary(Goal in, Context ctx);
    GoalTarget fhir2local(Goal.GoalTargetComponent in, Context ctx);
}
