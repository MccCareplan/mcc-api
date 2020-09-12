package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Reference;

public class MedicationMapper {

    public static MccMedicationRecord fhir2local(MedicationRequest in, Context ctx) {
        MccMedicationRecord out = new MccMedicationRecord();
        out.setType("MedicationRequest");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        if (in.hasStatusReason()) {
            MccCodeableConcept[] reasons = new MccCodeableConcept[1];
            reasons[0] = CodeableConceptMapper.fhir2local(in.getStatusReason(), ctx);
            out.setStatusReasons(reasons);
        }
        if (in.hasCategory()) {
            out.setCategories(CodeableConceptMapper.fhir2local(in.getCategory(), ctx));
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(CodeableConceptMapper.fhir2local(in.getMedicationCodeableConcept(), ctx));
            }
            if (in.hasMedicationReference()) {
                Reference ref = in.getMedicationReference();
                if (ref.getDisplay() != null) {
                    MccCodeableConcept med = new MccCodeableConcept();
                    med.setText(ref.getDisplay());
                    out.setMedication(med);
                } else {
                    //Todo - Consult with reference Cache for medications

                }
            }
        }
        if (in.hasReasonCode()) {
            out.setReasons(CodeableConceptMapper.fhir2local(in.getReasonCode(), ctx));
        }
        if (in.hasDosageInstruction()) {
            out.setDosages(GenericTypeMapper.fhir2local_dosageList(in.getDosageInstruction(), ctx));
        }
        if (in.hasNote()) {
            out.setNote(Helper.annotationsToString(in.getNote()));
        }
        if (in.hasPriority()) {
            out.setPriority(in.getPriority().getDisplay());
        }
        if (in.hasReasonReference()) {
            out.setReasonReferences(ReferenceMapper.fhir2local(in.getReasonReference(), ctx));
        }
        return out;
    }

    public static MccMedicationRecord fhir2local(MedicationStatement in, Context ctx) {
        MccMedicationRecord out = new MccMedicationRecord();
        out.setType("MedicationStatement");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        if (in.hasStatusReason()) {
            out.setStatusReasons(CodeableConceptMapper.fhir2local(in.getStatusReason(), ctx));
        }
        if (in.hasCategory()) {
            MccCodeableConcept[] categories = new MccCodeableConcept[1];
            categories[0] = CodeableConceptMapper.fhir2local(in.getCategory(), ctx);
            out.setCategories(categories);
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(CodeableConceptMapper.fhir2local(in.getMedicationCodeableConcept(), ctx));
            }
            if (in.hasMedicationReference()) {
                Reference ref = in.getMedicationReference();
                if (ref.getDisplay() != null) {
                    MccCodeableConcept med = new MccCodeableConcept();
                    med.setText(ref.getDisplay());
                    out.setMedication(med);
                } else {
                    //Todo - Consult with reference Cache for medications

                }
            }
        }
        if (in.hasReasonCode()) {
            out.setReasons(CodeableConceptMapper.fhir2local(in.getReasonCode(), ctx));
        }

        if (in.hasNote()) {
            out.setNote(Helper.annotationsToString(in.getNote()));
        }
        if (in.hasReasonReference()) {
            out.setReasonReferences(ReferenceMapper.fhir2local(in.getReasonReference(), ctx));
        }
        return out;
    }

}
