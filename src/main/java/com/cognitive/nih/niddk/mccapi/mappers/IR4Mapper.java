package com.cognitive.nih.niddk.mccapi.mappers;

import com.cognitive.nih.niddk.mccapi.data.*;
import com.cognitive.nih.niddk.mccapi.data.primative.*;
import org.hl7.fhir.r4.model.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IR4Mapper {
    MccCarePlan fhir2local(CarePlan in, Context ctx);
    MccCarePlanSummary fhir2Summary(CarePlan in, Set<String> profiles, Context ctx);
    List<Contact> fhir2Contacts(CareTeam in, Context ctx);
    Contact fhir2Contact(CareTeam.CareTeamParticipantComponent in, Context ctx);
    MccCondition fhir2local(Condition in, Context ctx);
    ConditionHistory fhir2History(Condition in, Context ctx);
    Counseling fhir2localAsCounseling(Procedure in, Context ctx);
    CounselingSummary fhir2CounselingSummary(Procedure in, Context ctx);
    Counseling fhir2localAsCounseling(ServiceRequest in, Context ctx);
    CounselingSummary fhir2CounselingSummary(ServiceRequest in, Context ctx);

    Education fhir2localAsEducation(Procedure in, Context ctx);
    EducationSummary fhir2EducationSummary(Procedure in, Context ctx);
    Education fhir2localAsEducation(ServiceRequest in, Context ctx);
    EducationSummary fhir2EducationSummary(ServiceRequest in, Context ctx);
    MccGoal fhir2local(Goal in, Context ctx);
    GoalSummary fhir2summary(Goal in, Context ctx);
    GoalTarget fhir2local(Goal.GoalTargetComponent in, Context ctx);
    MccMedicationRecord fhir2local(MedicationRequest in, Context ctx);
    MccMedicationRecord fhir2local(MedicationStatement in, Context ctx);
    MedicationSummary fhir2summary(MedicationRequest in, Context ctx);
    MedicationSummary fhir2summary(MedicationStatement in, Context ctx);
    MccObservation fhir2local(Observation in, Context ctx);
    ObservationComponent fhir2local(Observation.ObservationComponentComponent in, Context ctx);
    Contact fhir2Contact(Organization in, Context ctx);
    List<Patient.ContactComponent> getActiveContactOfType(Patient in, String type);
    Contact fhir2Contact(Patient.ContactComponent in, Context ctx);
    Contact fhir2Contact(Patient in, Context ctx);
    MccPatient fhir2local(Patient in, Context ctx);
    Contact fhir2Contact(Practitioner in, Context ctx);
    String performerToString(List<Procedure.ProcedurePerformerComponent> performers, Context ctx);
    String[] performerToStringArray(List<Procedure.ProcedurePerformerComponent> performers, Context ctx);
    MccQuestionnaireResponse fhir2local(QuestionnaireResponse in, Context ctx);
    SimpleQuestionnaireItem fhir2SimpleItem(QuestionnaireResponse in, Context ctx, String linkId);
    QuestionnaireResponseItem findItem(QuestionnaireResponse in, String linkId, Context ctx);
    QuestionnaireResponseItem findItem(QuestionnaireResponse.QuestionnaireResponseItemComponent in, String linkId, Context ctx);
    QuestionnaireResponseItem fhir2local(QuestionnaireResponse.QuestionnaireResponseItemComponent in, Context ctx);
    QuestionnaireResponseItemAnswer fhir2local(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in, Context ctx);
    QuestionnaireResponseSummary fhir2summary(QuestionnaireResponse in, Context ctx);
    SimpleQuestionnaireItem fhir2SimpleItem(Observation in, Context ctx, String linkId);
    MccReference fhir2local(Reference in, Context ctx);
    MccReference[] fhir2local_referenceArray(List<Reference> in, Context ctx);
    Referral fhir2local(ServiceRequest in, Context ctx);
    ReferralSummary fhir2summary(ServiceRequest in, Context ctx);
    Contact fhir2Contact(RelatedPerson in, Context ctx);
    Contact fhir2Contact(PractitionerRole in, Context ctx);
    void updateContact(PractitionerRole in, Contact contact, Context ctx);

    // Types
    GenericType fhir2local(Type in, Context ctx);
    MccDate fhir2local(DateType in, Context ctx);
    MccId fhir2local(IdType in, Context ctx);

    MccDate fhir2local(Date in, Context ctx);
    MccCodeableConcept fhir2local(CodeableConcept in, Context ctx);
    MccCodeableConcept conceptFromCode(String code, String text);
    MccCodeableConcept[] fhir2local(List<CodeableConcept> in, Context ctx);
    MccCoding fhir2local(Coding in, Context ctx);
    MccCoding fhir2localUnnormalized(Coding in, Context ctx);

    MccIdentifer[] fhir2local_identifierArray(List<Identifier> in, Context ctx);
    MccDateTime[] fhir2local_dateTimeArray(List<DateTimeType> in, Context ctx);
    MccDateTime fhir2local(DateTimeType in, Context ctx);
    MccTime fhir2local(TimeType in, Context ctx);
    MccPeriod fhir2local(Period in, Context ctx);
    MccSampledData fhir2local(SampledData in, Context ctx);
    MccSimpleQuantity fhir2local(SimpleQuantity in, Context ctx);
    MccIdentifer fhir2local(Identifier in, Context ctx);
    MccQuantity fhir2local(Quantity in, Context ctx);
    MccRatio fhir2local(Ratio in, Context ctx);
    MccRange fhir2local(Range in, Context ctx);
    MccDuration fhir2local(Duration in, Context ctx);
    MccInstant fhir2local(InstantType in, Context ctx);
    MccTiming fhir2local(Timing in, Context ctx);
    MccDosage[] fhir2local_dosageList(List<Dosage> in, Context ctx);
    MccDosage fhir2local(Dosage in, Context ctx);

    IFHIRNormalizer getNormalizer();
}
