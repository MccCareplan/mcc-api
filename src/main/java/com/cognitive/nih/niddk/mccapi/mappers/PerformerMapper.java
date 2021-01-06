package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import org.hl7.fhir.r4.model.Procedure;

import java.util.ArrayList;
import java.util.List;

public class PerformerMapper {


    public static String performerToString(List<Procedure.ProcedurePerformerComponent> performers, Context ctx) {
        StringBuilder perf = new StringBuilder();
        boolean addComma = false;

        for (Procedure.ProcedurePerformerComponent comp : performers) {
            if (comp.hasActor()) {
                if (addComma) {
                    perf.append(",");
                }
                perf.append(NameResolver.getReferenceName(comp.getActor(), ctx));
                addComma = true;
            }
        }
        return perf.toString();
    }

    public static String[] performerToStringArray(List<Procedure.ProcedurePerformerComponent> performers, Context ctx) {
        ArrayList<String> perfs = new ArrayList<>();

        for (Procedure.ProcedurePerformerComponent comp : performers) {
            if (comp.hasActor()) {
                perfs.add(NameResolver.getReferenceName(comp.getActor(), ctx));
            }
        }

        String[] outA = new String[perfs.size()];
        return perfs.toArray(outA);
    }
}
