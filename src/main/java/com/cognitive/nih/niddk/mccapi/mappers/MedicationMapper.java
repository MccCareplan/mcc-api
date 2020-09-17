package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.MedicationSummary;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;

import java.util.List;

@Slf4j
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
                    Medication med = ReferenceResolver.findMedication(ref, ctx);
                    if (med != null) {
                        out.setMedication(CodeableConceptMapper.fhir2local(med.getCode(), ctx));
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
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
        if (in.hasDetectedIssue()) {
            out.setDetectedIssues(ReferenceMapper.fhir2local(in.getDetectedIssue(), ctx));
        }

        if (in.hasRequester()) {
            out.setRequester(ReferenceMapper.fhir2local(in.getRequester(),ctx));
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
                    Medication med = ReferenceResolver.findMedication(ref, ctx);
                    if (med != null) {
                        out.setMedication(CodeableConceptMapper.fhir2local(med.getCode(), ctx));
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
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


    public static MedicationSummary fhir2summary(MedicationRequest in, Context ctx) {
        MedicationSummary out = new MedicationSummary();
        out.setType("MedicationRequest");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        if (in.hasCategory()) {
            out.setCategories(Helper.getConceptsAsDisplayString(in.getCategory()));
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(in.getMedicationCodeableConcept().getText());
            }
            if (in.hasMedicationReference()) {
                Reference ref = in.getMedicationReference();
                String name = NameResolver.getReferenceName(ref,ctx);
                if (name != null)
                    out.setMedication(name);
                if (ref.getDisplay() != null) {
                    out.setMedication(ref.getDisplay());
                } else {
                    Medication med = ReferenceResolver.findMedication(ref, ctx);
                    if (med != null) {
                        out.setMedication(med.getCode().getText());
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
                }
            }
        }

        ///Handle Reasons
        StringBuffer reasons = new StringBuffer();
        if (in.hasReasonCode()) {
            reasons.append(Helper.getConceptsAsDisplayString(in.getReasonCode()));
        }
        if (in.hasReasonReference()) {
            List<Reference> reasonRefs = in.getReasonReference();
            handleReasonReference(in.getReasonReference(), reasons, ctx);
        }
        if (reasons.length() > 0) {
            out.setReasons(reasons.toString());
        }


        //Boil down the dosage instructions
        if (in.hasDosageInstruction()) {
            StringBuffer inst = new StringBuffer();
            List<Dosage> instructions = in.getDosageInstruction();
            if (instructions.size() > 1) {
                inst.append("Multiple Dosage Instructions...");
            } else {

                Dosage d = instructions.get(0);
                if (d.hasText()) {
                    inst.append(d.getText());
                } else {
                    inst.append(Helper.dosageToString(d));
                }
            }
            if (inst.length() > 0) {
                out.setDosages(inst.toString());
            }
        }

        if (in.hasPriority()) {
            out.setPriority(in.getPriority().getDisplay());
        }

        if (in.hasDetectedIssue()) {
            List<Reference> issueReferences = in.getDetectedIssue();
            out.setIssues(handleIssues(issueReferences, ctx));
        }

        if (in.hasRequester()) {
            out.setRequestedBy(NameResolver.getReferenceName(in.getRequester(),ctx));
        }
        return out;
    }

    public static MedicationSummary fhir2summary(MedicationStatement in, Context ctx) {
        MedicationSummary out = new MedicationSummary();
        out.setType("MedicationStatement");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        if (in.hasCategory()) {
            out.setCategories(Helper.getConceptDisplayString(in.getCategory()));
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(in.getMedicationCodeableConcept().getText());
            }
            if (in.hasMedicationReference()) {
                Reference ref = in.getMedicationReference();
                String name = NameResolver.getReferenceName(ref,ctx);
                if (name != null)
                    out.setMedication(name);
                if (ref.getDisplay() != null) {
                    out.setMedication(ref.getDisplay());
                } else {
                    Medication med = ReferenceResolver.findMedication(ref, ctx);
                    if (med != null) {
                        out.setMedication(med.getCode().getText());
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
                }
            }
        }

        ///Handle Reasons
        StringBuffer reasons = new StringBuffer();
        if (in.hasReasonCode()) {
            reasons.append(Helper.getConceptsAsDisplayString(in.getReasonCode()));
        }
        if (in.hasReasonReference()) {
            handleReasonReference(in.getReasonReference(), reasons, ctx);
        }
        if (reasons.length() > 0) {
            out.setReasons(reasons.toString());
        }

        if (in.hasDosage())
        {
            StringBuffer inst = new StringBuffer();
            List<Dosage> instructions = in.getDosage();
            if (instructions.size() > 1) {
                inst.append("Multiple Dosages...");
            } else {

                Dosage d = instructions.get(0);
                if (d.hasText()) {
                    inst.append(d.getText());
                } else {
                    inst.append(Helper.dosageToString(d));
                }
            }
            if (inst.length() > 0) {
                out.setDosages(inst.toString());
            }
        }
        /*
        if (in.hasPriority()) {
            out.setPriority(in.getPriority().getDisplay());
        }

        if (in.hasDetectedIssue()) {
            List<Reference> issueReferences = in.getDetectedIssue();
            out.setIssues(handleIssues(issueReferences, ctx));
        }

        if (in.hasRequester()) {
            out.setRequestedBy(NameResolver.getReferenceName(in.getRequester(),ctx));
        }
        */
        return out;
    }
    public static void handleReasonReference(List<Reference> reasonRefs, StringBuffer reasons, Context ctx) {
        for (Reference r : reasonRefs) {
            if (r.hasDisplay()) {
                Helper.addStringToBufferWithSep(reasons, r.getDisplay(), ", ");
            } else if (r.hasReference()) {
                // Resolve the reference Condition or Observations
                String ref = Helper.getReferenceType(r);
                if (ref.compareTo("Condition") == 0) {
                    Condition c = ReferenceResolver.findCondition(r, ctx);
                    if (c.hasCode()) {
                        if (c.getCode().hasText()) {
                            Helper.addStringToBufferWithSep(reasons, c.getCode().getText(), ", ");
                        } else {
                            Helper.addStringToBufferWithSep(reasons, c.getCode().getCodingFirstRep().getDisplay(), ", ");
                        }
                    }
                } else if (ref.compareTo("Observation") == 0) {
                    Observation o = ReferenceResolver.findObservation(r, ctx);
                    if (o.hasCode()) {
                        if (o.getCode().hasText()) {
                            Helper.addStringToBufferWithSep(reasons, o.getCode().getText(), ", ");
                        } else {
                            Helper.addStringToBufferWithSep(reasons, o.getCode().getCodingFirstRep().getDisplay(), ", ");
                        }
                    }

                }

            }
        }
    }

    public static String handleIssues(List<Reference> issueReferences, Context ctx) {
        StringBuffer issues = new StringBuffer();
        int count = 0;
        for (Reference ref : issueReferences) {
            if (count > 0) {
                issues.append(",");
            }
            if (ref.hasDisplay()) {
                issues.append(ref.getDisplay());
            } else if (ref.hasReference()) {
                // Fetch the issue and interpret
                DetectedIssue di = ReferenceResolver.findDetectedIssues(ref, ctx);
                if (di.hasText()) {
                    issues.append(di.getText());
                } else if (di.hasCode()) {
                    if (di.getCode().hasText()) {
                        issues.append(di.getCode().getText());
                    } else {
                        issues.append(di.getCode().getCodingFirstRep().getDisplay());
                    }
                }
            }
            count++;
        }
        return issues.toString();
    }
}
