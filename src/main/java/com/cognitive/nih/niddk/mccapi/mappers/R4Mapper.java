package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.data.primative.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class R4Mapper implements IR4Mapper {
    private final ICareplanMapper careplanMapper;
    private final ICareTeamMapper careTeamMapper;
    private final ICodeableConceptMapper codeableConceptMapper;
    private final IConditionMapper conditionMapper;
    private final ICounselingMapper counselingMapper;
    private final IEducationMapper educationMapper;
    private final IGenericTypeMapper genericTypeMapper;
    private final IGoalMapper goalMapper;
    private final IMedicationMapper medicationMapper;
    private final IObservationMapper observationMapper;
    private final IOrganizationMapper organizationMapper;
    private final IPatientMapper patientMapper;
    private final IPractitionerMapper practitionerMapper;
    private final IProcedureMapper procedureMapper;
    private final IQuestionnaireResponseMapper questionnaireResponseMapper;
    private final IReferenceMapper referenceMapper;
    private final IReferralMapper referralMapper;
    private final IRelatedPersonMapper relatedPersonMapper;

    public R4Mapper(ICareplanMapper careplanMapper, ICareTeamMapper careTeamMapper, ICodeableConceptMapper codeableConceptMapper, IConditionMapper conditionMapper, ICounselingMapper counselingMapper, IEducationMapper educationMapper, IGenericTypeMapper genericTypeMapper, IGoalMapper goalMapper, IMedicationMapper medicationMapper, IObservationMapper observationMapper, IOrganizationMapper organizationMapper, IPatientMapper patientMapper, IPractitionerMapper practitionerMapper, IProcedureMapper procedureMapper, IQuestionnaireResponseMapper questionnaireResponseMapper, IReferenceMapper referenceMapper, IReferralMapper referralMapper, IRelatedPersonMapper relatedPersonMapper) {
        this.careplanMapper = careplanMapper;
        this.careTeamMapper = careTeamMapper;
        this.codeableConceptMapper = codeableConceptMapper;
        this.conditionMapper = conditionMapper;
        this.counselingMapper = counselingMapper;
        this.educationMapper = educationMapper;
        this.genericTypeMapper = genericTypeMapper;
        this.goalMapper = goalMapper;
        this.medicationMapper = medicationMapper;
        this.observationMapper = observationMapper;
        this.organizationMapper = organizationMapper;
        this.patientMapper = patientMapper;
        this.practitionerMapper = practitionerMapper;
        this.procedureMapper = procedureMapper;
        this.questionnaireResponseMapper = questionnaireResponseMapper;
        this.referenceMapper = referenceMapper;
        this.referralMapper = referralMapper;
        this.relatedPersonMapper = relatedPersonMapper;
    }

    @Override
    public MccCarePlan fhir2local(CarePlan in, Context ctx) {
        return careplanMapper.fhir2local(in,ctx);
    }

    @Override
    public MccCarePlanSummary fhir2Summary(CarePlan in, Set<String> profiles, Context ctx) {
        return careplanMapper.fhir2Summary(in, profiles, ctx);
    }

    @Override
    public List<Contact> fhir2Contacts(CareTeam in, Context ctx) {
        return careTeamMapper.fhir2Contacts(in, ctx);
    }

    @Override
    public Contact fhir2Contact(CareTeam.CareTeamParticipantComponent in, Context ctx) {
        return careTeamMapper.fhir2Contact(in,ctx);
    }

    @Override
    public MccCodeableConcept conceptFromCode(String code, String text) {
        return codeableConceptMapper.conceptFromCode(code,text);
    }

    @Override
    public MccCodeableConcept[] fhir2local(List<CodeableConcept> in, Context ctx) {
        return codeableConceptMapper.fhir2local(in,ctx);
    }

    @Override
    public MccCondition fhir2local(Condition in, Context ctx) {
        return conditionMapper.fhir2local(in,ctx);
    }

    @Override
    public ConditionHistory fhir2History(Condition in, Context ctx) {
        return conditionMapper.fhir2History(in,ctx);
    }

    @Override
    public Counseling fhir2localAsCounseling(Procedure in, Context ctx) {
        return counselingMapper.fhir2local(in,ctx);
    }

    @Override
    public CounselingSummary fhir2CounselingSummary(Procedure in, Context ctx) {
        return counselingMapper.fhir2summary(in,ctx);
    }

    @Override
    public Counseling fhir2localAsCounseling(ServiceRequest in, Context ctx) {
        return counselingMapper.fhir2local(in,ctx);
    }

    @Override
    public CounselingSummary fhir2CounselingSummary(ServiceRequest in, Context ctx) {
        return counselingMapper.fhir2summary(in,ctx);
    }

    @Override
    public Education fhir2localAsEducation(Procedure in, Context ctx) {
        return educationMapper.fhir2local(in,ctx);
    }

    @Override
    public EducationSummary fhir2EducationSummary(Procedure in, Context ctx) {
        return educationMapper.fhir2summary(in,ctx);
    }

    @Override
    public Education fhir2localAsEducation(ServiceRequest in, Context ctx) {
        return educationMapper.fhir2local(in,ctx);
    }

    @Override
    public EducationSummary fhir2EducationSummary(ServiceRequest in, Context ctx) {
        return educationMapper.fhir2summary(in,ctx);
    }

    @Override
    public MccGoal fhir2local(Goal in, Context ctx) {
        return goalMapper.fhir2local(in,ctx);
    }

    @Override
    public GoalSummary fhir2summary(Goal in, Context ctx) {
        return goalMapper.fhir2summary(in,ctx);
    }

    @Override
    public GoalTarget fhir2local(Goal.GoalTargetComponent in, Context ctx) {
        return goalMapper.fhir2local(in,ctx);
    }

    @Override
    public MccMedicationRecord fhir2local(MedicationRequest in, Context ctx) {
        return medicationMapper.fhir2local(in,ctx);
    }

    @Override
    public MccMedicationRecord fhir2local(MedicationStatement in, Context ctx) {
        return medicationMapper.fhir2local(in,ctx);
    }

    @Override
    public MedicationSummary fhir2summary(MedicationRequest in, Context ctx) {
        return medicationMapper.fhir2summary(in,ctx);
    }

    @Override
    public MedicationSummary fhir2summary(MedicationStatement in, Context ctx) {
        return medicationMapper.fhir2summary(in,ctx);
    }

    @Override
    public MccObservation fhir2local(Observation in, Context ctx) {
        return observationMapper.fhir2local(in,ctx);
    }

    @Override
    public ObservationComponent fhir2local(Observation.ObservationComponentComponent in, Context ctx) {
        return observationMapper.fhir2local(in,ctx);
    }

    @Override
    public Contact fhir2Contact(Organization in, Context ctx) {
        return organizationMapper.fhir2Contact(in,ctx);
    }

    @Override
    public List<Patient.ContactComponent> getActiveContactOfType(Patient in, String type) {
        return patientMapper.getActiveContactOfType(in, type);
    }

    @Override
    public Contact fhir2Contact(Patient.ContactComponent in, Context ctx) {
        return patientMapper.fhir2Contact(in,ctx);
    }

    @Override
    public Contact fhir2Contact(Patient in, Context ctx) {
        return patientMapper.fhir2Contact(in,ctx);
    }

    @Override
    public MccPatient fhir2local(Patient in, Context ctx) {
        return patientMapper.fhir2local(in,ctx);
    }

    @Override
    public Contact fhir2Contact(Practitioner in, Context ctx) {
        return practitionerMapper.fhir2Contact(in,ctx);
    }

    @Override
    public String performerToString(List<Procedure.ProcedurePerformerComponent> performers, Context ctx) {
        return procedureMapper.performerToString(performers,ctx);
    }

    @Override
    public String[] performerToStringArray(List<Procedure.ProcedurePerformerComponent> performers, Context ctx) {
        return procedureMapper.performerToStringArray(performers,ctx);
    }

    @Override
    public MccQuestionnaireResponse fhir2local(QuestionnaireResponse in, Context ctx) {
        return questionnaireResponseMapper.fhir2local(in,ctx);
    }

    @Override
    public SimpleQuestionnaireItem fhir2SimpleItem(QuestionnaireResponse in, Context ctx, String linkId) {
        return questionnaireResponseMapper.fhir2SimpleItem(in,ctx,linkId);
    }

    @Override
    public QuestionnaireResponseItem findItem(QuestionnaireResponse in, String linkId, Context ctx) {
        return questionnaireResponseMapper.findItem(in,linkId,ctx);
    }

    @Override
    public QuestionnaireResponseItem findItem(QuestionnaireResponse.QuestionnaireResponseItemComponent in, String linkId, Context ctx) {
        return questionnaireResponseMapper.findItem(in,linkId, ctx);
    }

    @Override
    public QuestionnaireResponseItem fhir2local(QuestionnaireResponse.QuestionnaireResponseItemComponent in, Context ctx) {
        return questionnaireResponseMapper.fhir2local(in,ctx);
    }

    @Override
    public QuestionnaireResponseItemAnswer fhir2local(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in, Context ctx) {
        return questionnaireResponseMapper.fhir2local(in,ctx);
    }

    @Override
    public QuestionnaireResponseSummary fhir2summary(QuestionnaireResponse in, Context ctx) {
        return questionnaireResponseMapper.fhir2summary(in,ctx);
    }

    @Override
    public MccReference fhir2local(Reference in, Context ctx) {
        return referenceMapper.fhir2local(in,ctx);
    }

    @Override
    public MccReference[] fhir2local_referenceArray(List<Reference> in, Context ctx) {
        return referenceMapper.fhir2local(in,ctx);
    }

    @Override
    public MccDateTime fhir2local(DateTimeType in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccTime fhir2local(TimeType in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccPeriod fhir2local(Period in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccSampledData fhir2local(SampledData in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccSimpleQuantity fhir2local(SimpleQuantity in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccIdentifer fhir2local(Identifier in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccCodeableConcept fhir2local(CodeableConcept in, Context ctx) {
        return codeableConceptMapper.fhir2local(in,ctx);
    }

    @Override
    public MccQuantity fhir2local(Quantity in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);

    }

    @Override
    public MccRatio fhir2local(Ratio in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccRange fhir2local(Range in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccDuration fhir2local(Duration in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccInstant fhir2local(InstantType in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccTiming fhir2local(Timing in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccDosage[] fhir2local_dosageList(List<Dosage> in, Context ctx) {
        return genericTypeMapper.fhir2local_dosageList(in,ctx);
    }

    @Override
    public MccDosage fhir2local(Dosage in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public Referral fhir2local(ServiceRequest in, Context ctx) {
        return referralMapper.fhir2local(in,ctx);
    }

    @Override
    public ReferralSummary fhir2summary(ServiceRequest in, Context ctx) {
        return referralMapper.fhir2summary(in,ctx);
    }

    @Override
    public Contact fhir2Contact(RelatedPerson in, Context ctx) {
        return relatedPersonMapper.fhir2Contact(in,ctx);
    }

    @Override
    public GenericType fhir2local(Type in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccDate fhir2local(DateType in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccId fhir2local(IdType in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccDate fhir2local(Date in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccCoding fhir2local(Coding in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }

    @Override
    public MccIdentifer[] fhir2local_identifierArray(List<Identifier> in, Context ctx) {
        return genericTypeMapper.fhir2local_identifierArray(in,ctx);
    }

    @Override
    public MccDateTime[] fhir2local_dateTimeArray(List<DateTimeType> in, Context ctx) {
        return genericTypeMapper.fhir2local(in,ctx);
    }
}
