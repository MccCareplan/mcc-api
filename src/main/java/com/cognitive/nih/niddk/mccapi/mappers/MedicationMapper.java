/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.Context;
import com.cognitive.nih.niddk.mccapi.data.MccMedicationRecord;
import com.cognitive.nih.niddk.mccapi.data.MedicationSummary;
import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;
import com.cognitive.nih.niddk.mccapi.services.NameResolver;
import com.cognitive.nih.niddk.mccapi.services.ReferenceResolver;
import com.cognitive.nih.niddk.mccapi.util.FHIRHelper;
import com.cognitive.nih.niddk.mccapi.util.JavaHelper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MedicationMapper implements IMedicationMapper {


    public MccMedicationRecord fhir2local(MedicationRequest in, Context ctx) {
        MccMedicationRecord out = new MccMedicationRecord();
        IR4Mapper mapper = ctx.getMapper();
        out.setType("MedicationRequest");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        if (in.hasStatusReason()) {
            MccCodeableConcept[] reasons = new MccCodeableConcept[1];
            reasons[0] = mapper.fhir2local(in.getStatusReason(), ctx);
            out.setStatusReasons(reasons);
        }
        if (in.hasCategory()) {
            out.setCategories(mapper.fhir2local(in.getCategory(), ctx));
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(mapper.fhir2local(in.getMedicationCodeableConcept(), ctx));
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
                        out.setMedication(mapper.fhir2local(med.getCode(), ctx));
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
                }
            }
        }
        if (in.hasReasonCode()) {
            out.setReasons(mapper.fhir2local(in.getReasonCode(), ctx));
        }
        if (in.hasDosageInstruction()) {
            out.setDosages(mapper.fhir2local_dosageList(in.getDosageInstruction(), ctx));
        }
        if (in.hasNote()) {
            out.setNote(FHIRHelper.annotationsToString(in.getNote(),ctx));
        }
        if (in.hasPriority()) {
            out.setPriority(in.getPriority().getDisplay());
        }
        if (in.hasReasonReference()) {
            out.setReasonReferences(mapper.fhir2local_referenceArray(in.getReasonReference(), ctx));
        }
        if (in.hasDetectedIssue()) {
            out.setDetectedIssues(mapper.fhir2local_referenceArray(in.getDetectedIssue(), ctx));
        }

        if (in.hasRequester()) {
            out.setRequester(mapper.fhir2local(in.getRequester(),ctx));
        }
        return out;
    }

    public MccMedicationRecord fhir2local(MedicationStatement in, Context ctx) {
        MccMedicationRecord out = new MccMedicationRecord();
        IR4Mapper mapper = ctx.getMapper();
        out.setType("MedicationStatement");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());
        if (in.hasStatusReason()) {
            out.setStatusReasons(mapper.fhir2local(in.getStatusReason(), ctx));
        }
        if (in.hasCategory()) {
            MccCodeableConcept[] categories = new MccCodeableConcept[1];
            categories[0] = mapper.fhir2local(in.getCategory(), ctx);
            out.setCategories(categories);
        }
        if (in.hasMedication()) {
            if (in.hasMedicationCodeableConcept()) {
                out.setMedication(mapper.fhir2local(in.getMedicationCodeableConcept(), ctx));
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
                        out.setMedication(mapper.fhir2local(med.getCode(), ctx));
                    } else {
                        log.warn("Failed to resolve reference: " + ref.toString());
                    }
                }
            }
        }
        if (in.hasReasonCode()) {
            out.setReasons(mapper.fhir2local(in.getReasonCode(), ctx));
        }

        if (in.hasNote()) {
            out.setNote(FHIRHelper.annotationsToString(in.getNote(),ctx));
        }
        if (in.hasReasonReference()) {
            out.setReasonReferences(mapper.fhir2local_referenceArray(in.getReasonReference(), ctx));
        }
        return out;
    }


    public MedicationSummary fhir2summary(MedicationRequest in, Context ctx) {
        MedicationSummary out = new MedicationSummary();
        out.setType("MedicationRequest");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        if (in.hasCategory()) {
            out.setCategories(FHIRHelper.getConceptsAsDisplayString(in.getCategory()));
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
        StringBuilder reasons = new StringBuilder();
        if (in.hasReasonCode()) {
            reasons.append(FHIRHelper.getConceptsAsDisplayString(in.getReasonCode()));
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
            StringBuilder inst = new StringBuilder();
            List<Dosage> instructions = in.getDosageInstruction();
            if (instructions.size() > 1) {
                inst.append("Multiple Dosage Instructions...");
            } else {

                Dosage d = instructions.get(0);
                if (d.hasText()) {
                    inst.append(d.getText());
                } else {
                    inst.append(FHIRHelper.dosageToString(d));
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

    public MedicationSummary fhir2summary(MedicationStatement in, Context ctx) {
        MedicationSummary out = new MedicationSummary();
        out.setType("MedicationStatement");
        out.setFhirId(in.getIdElement().getIdPart());
        out.setStatus(in.getStatus().toCode());

        if (in.hasCategory()) {
            out.setCategories(FHIRHelper.getConceptDisplayString(in.getCategory()));
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
        StringBuilder reasons = new StringBuilder();
        if (in.hasReasonCode()) {
            reasons.append(FHIRHelper.getConceptsAsDisplayString(in.getReasonCode()));
        }
        if (in.hasReasonReference()) {
            handleReasonReference(in.getReasonReference(), reasons, ctx);
        }
        if (reasons.length() > 0) {
            out.setReasons(reasons.toString());
        }

        if (in.hasDosage())
        {
            StringBuilder inst = new StringBuilder();
            List<Dosage> instructions = in.getDosage();
            if (instructions.size() > 1) {
                inst.append("Multiple Dosages...");
            } else {

                Dosage d = instructions.get(0);
                if (d.hasText()) {
                    inst.append(d.getText());
                } else {
                    inst.append(FHIRHelper.dosageToString(d));
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
    public void handleReasonReference(List<Reference> reasonRefs, StringBuilder reasons, Context ctx) {
        for (Reference r : reasonRefs) {
            if (r.hasDisplay()) {
                JavaHelper.addStringToBufferWithSep(reasons, r.getDisplay(), ", ");
            } else if (r.hasReference()) {
                // Resolve the reference Condition or Observations
                String ref = FHIRHelper.getReferenceType(r);
                if (ref.compareTo("Condition") == 0) {
                    Condition c = ReferenceResolver.findCondition(r, ctx);
                    if (c.hasCode()) {
                        if (c.getCode().hasText()) {
                            JavaHelper.addStringToBufferWithSep(reasons, c.getCode().getText(), ", ");
                        } else {
                            JavaHelper.addStringToBufferWithSep(reasons, c.getCode().getCodingFirstRep().getDisplay(), ", ");
                        }
                    }
                } else if (ref.compareTo("Observation") == 0) {
                    Observation o = ReferenceResolver.findObservation(r, ctx);
                    if (o.hasCode()) {
                        if (o.getCode().hasText()) {
                            JavaHelper.addStringToBufferWithSep(reasons, o.getCode().getText(), ", ");
                        } else {
                            JavaHelper.addStringToBufferWithSep(reasons, o.getCode().getCodingFirstRep().getDisplay(), ", ");
                        }
                    }

                }

            }
        }
    }

    public String handleIssues(List<Reference> issueReferences, Context ctx) {
        StringBuilder issues = new StringBuilder();
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
