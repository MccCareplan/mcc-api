/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import org.hl7.fhir.r4.model.Procedure;

import java.util.List;

public interface IProcedureMapper {
    String performerToString(List<Procedure.ProcedurePerformerComponent> performers, Context ctx);
    String[] performerToStringArray(List<Procedure.ProcedurePerformerComponent> performers, Context ctx);
}
