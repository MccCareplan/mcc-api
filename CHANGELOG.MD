## Release 1.0.30
- Updated all VSAC Value sets to the current expansion as of 8/23/21 added
- Added mcc.observation.egfr.calculate to allow eGFR to be calculated base on Serum Creatinine-based formula (CKD-EPI)
- updated /observationssegmented to support egfr calculation
- Added basic ucum support for conversions
- Added code to support mg/dL to umol/L since the java ucum library convert does not support conversion with mol and molecular weight
- updated /find/latest/observation to support egfr calculation
- update observationsbyvalueset to support egfr calculation
- General fix to regex expressions usong "|" fixed to properly escape the "|" as "\\|"

## Release 1.0.29
- Added mcc.observation.log.unit.failure to help track missing units
- Enhance logging around Pratitioner Role and organization lookup
- Added mcc.observation.log.calls to control extra logging arround observation calls

## Release 1.0.28
- Enhanced header check logging
- Security fix - Move to logback classic 1.2.0+

## Release 1.0.27
- Fix handling of mcc-token header

## Release 1.0.26
- ContextManager update to enforce the new header related requirement options
- Added new interanl exception classes to support header requirement exceptions
- Added mcc.request.header.require.token & mcc.request.header.require.server config flags
- Convert ContextManager from a Singleton to a Spring Component to support configuration.
- Fix the handling of Address selection when there is possible conflict
- Fix medication request status - support 'stopped' and 'draft'
- Fix medication summary drop through logging messages


## Release 1.0.25
- Added fhir.request.timeout to control the TCP/IP Socket timeout when making a FHIR Request.
- Added fhir.connect.timeout to control the TCP/IP Socket timeout when making an initial FHIR Connection.

## Release 1.0.24
- Revision of condition summary and history to support recorded vs onset
  + Condition structure changes to support firstOnset and firstRecord
  + Condition date removed
  + ConditionSummary updated for firstRecorded and firstRecordedAsText
  + ConditionHostory updated for recorded and recordedAsText 
- Observation calls now support a list of required units via the optional paramter requiredunits. The list is comma seperated and the first entry is use as a default when no unit is specified. This parameter currently acts as a filter and no unit conversion is attempted.

## Release 1.0.23
+ Added obdsrvation related Diagnostics

## Release 1.0.22
- More Date Conversion Fixes
- Updated Referrals, additional configuration options and support for valueset
- Support valueset overrides using the directory /usr/local/mcc-api/valuesets/override
- Support valueset supplements using the directory /usr/local/mcc-api/valuesets/supplement
- New properties to control referrals
  + mcc.referral.use_valueset
  + mcc.referral.require_category
- Updated the Referral valueset to reflect the Implementation Guide
- Changed the defaults for referrals so that is Category use now defaults to false, while value set use not defaults to true.
- New support for using a value set to control what conditions are social concerns
- Updated FHIR Queries around health concerns
  + Removed: Condition.QueryHealthConcerns
  + Added Condition.QueryHealthConcerns.ByCategory=/Condition/?subject={subject}&category=http%3A%2F%2Fhl7.org%2Ffhir%2Fus%2Fcore%2FCodeSystem%2Fcondition-category%7Chealth-concern
  + Added Condition.QueryHealthConcerns.ForValueSet=/Condition/?subject={subject}
- Added very basic social concerns value set (One code for Food insecurity)

## Release 1.0.21
- Move the mapping of Observations to SimpleQuestionnaireItems to the ObservationMapper
- Fix data handling Observation Mapping to SimpleQuestionnaireItems

## Release 1.0.20
- Support for Observation based Patient Reported Data
- New Property: mcc.patient.reported.data.use.observations controls if PRD/Survey uses Observations
- New Property: mcc.patient.reported.data.use.questionnaires controls if PRD/Survey use QuestionnaireResponses


## Release 1.0.19
- Added coding Normalization for Systems (Support ICD9/10, Snomed, Loinc, RxNorm)
- Added mcc.referral.require_performer to allow referral selection logic to be customized
- Added mcc.careplan.use.fallback to control use fallback mechanisms for finding the best careplan
- Default the CarePlan Title if it is not present
- Added mcc.codeableconcept.use.additive.normalization to control is codeable concepts that are have codings that require normalization will retain a copy of the un-normalized coding.


## Release 1.0.18-RELEASE
- Support /observationssegmented query by value set for a segemented valueset retrieval - Added initial mappers for PractitionerRole
- Added ObservationCollection and ObservationList to support /observationssegmented query
- Added property mcc.provider.pullbyrole
- Added properties to control contact fetch (mcc.careteam.use.active, mcc.careteam.use.careplan)
- Updated contact mapping to make a best attempt to get Telephone, Email and Address, includes scanning Provider Role and Enhanced organization use.
- Added new Query Key: PractitionerRole.Query=/PractitionerRole/?practitioner={id}&active=true
- Added new Query Key: CareTeam.Query.ActiveList=/CareTeam/?subject={subject}&status=active
- Updated ParctitionerRole query to use internal overridable system.
- Enhanced CareTeam mapping to do a first cut on ParctitionerRole.
- Enhanced CareTeam mapping to log unknown member types.

## Release 1.0.17-RELEASE
- Refactored all mapping to use beans
- Removed PATIENT_ID_CHECK code
- Avoid null pointer error for goals without a status
- Update FHIRHelpers for Identifier Selection
- Updated Context to include the current mapper
- Updated the Patient mapping to support the mcc.patient.id.system to allow deployers to define the system to match for patent Id - see the section "Configuring the patient identifier" in the README.MD
- Updated the logic for context setup
- changed mapper naming,
- fixed check on careteam id
- Removed static call to QuestionnaireResponse Mapper.

## Release 1.0.16-RELEASE
- Upgraded to spring-boot-starter-parent 2.4.2
- Resolves issues with path parsing

## Release 1.0.15-RELEASE
- Formal release

## Release 1.0.14-SNAPSHOT
- Added /find/all/questionnaireresponseitems
- Added QueryKey: QuestionnaireResponse.QueryWithOptions

## Release 1.0.13-SNAPSHOT
- General QuestionnaireResponse Support
- General type updates to transmit less empty data
- GenericType supports coding and decimal
- Added initial rest endpoints to clear caches
  - delete /cache
  - delete /cache/questionnaires
  - delete /cache/references
  - delete /cache/valuesets
  - delete /cache/querymappings
- add endpoints to support QuestionnaireResponse retrieval
  - /find/latest/questionnaireresponse/?subject={subject}&code={code}
  - /find/latest/questionnaireresponseitem/?subject={subject}&code={code}
  - /summary/questionnaireresponses/?subject={subject}
  - /questionnaireresponse/{id}
  - /questionnaireresponse/?subject={subject}
- added new fhir query keys
  - Questionnaire.Lookup=/Questionnaire/{id}
  - Questionnaire.FindForCode=/Questionnaire/?code={code}&_summary=true
  - QuestionnaireResponse.Query=/QuestionnaireResponse/?questionnaire={ids}&subject={subject} 
  - QuestionnaireResponse.OpenQuery=/QuestionnaireResponse/?subject={subject}
  - QuestionnaireResponse.QueryLatest=/QuestionnaireResponse/?questionnaire={ids}&subject={subject}&status=completed,amended,in-progress&_sort=-authored&_count=1
  - QuestionnaireResponse.Summary.Query=/QuestionnaireResponse/?questionnaire={ids}&subject={subject}&_summary=true
  - QuestionnaireResponse.Summary.QueryLatest=/QuestionnaireResponse/?questionnaire={ids}&subject={subject}&_sort=-authored&_count=1&_summary=true
  - QuestionnaireResponse.Lookup=/QuestionnaireResponse/{id}
  
## Release 1.0.12-SNAPSHOT
- Updated Goals and Goal Summary for Acceptance
- Added translate option "/find/latest/observation", so that recognized codes can translate to more complex queries, e.g. a Value set or containing parent observation.
- Updated query parameter handling to have the first found instance take precedence.
- fix value set loading to only load a value set once in the event the load list had duplicates
- started support on procedures.

## Release 1.0.11-SNAPSHOT
- Updated /find/latest/observation to return empty observation when not found, status = "notfound"
- Add hasImage to contact
- Renamed util.Helper to FHIRHelper, added MccHelper
- Added Age to Patient
- Added Performer to Education and Counselling
- Added Reason to Education and Counselling
- Refactored and extended internal helpers.

## Release 1.0.10-SNAPSHOT
- Fix careplan lookup during medication summary
- Extend the education query to include the code 311401005

## Release 1.0.9-SNAPSHOT
- Provide text for all codeable concepts
- Update basic types to annotate when fields are required
- Updated Readme to include the change log

## Release 1.0.8-SNAPSHOT
- Added Condition Consolidation.
- Preliminary Fuzzy Date Comparison (Missing Age and Range for now)
- Finalize and Sort Condition History.
- Fixed errors with the building Condition History


## Release 1.0.7-SNAPSHOT
- Added new endpoint for Referrals at /summary/referrals - Prelimiary referral support implemented.
- Added new endpoints for Education & Counseling /summary/educations and /summary/counselings
- Added /summary/goals, /summary/medications, /summary/condtions
- Deprecated  /goalsumarry, /medicationsumary, /condintionsummary
- Extended reference handling and name resolution for additional type
- New fhirquery keys
    - Education.Procedure.Query
    - Education.ServiceRequest.Query
    - Counseling.Procedure.Query
    - Counseling.ServiceRequest.Query
    - Referral.ServiceRequest.Query
- Extended Type support to include MccId
- GenericType valueTypes normalized to match Hapi library FHIR Types
    - String is now string
    - Date is now date
    - DateTime is now dateTime
    - Time is now time
    - Boolean is now boolean
    - Integer is now integer
    - Instant is now instant

## Release 1.0.6-SNAPSHOT
- Added new support for finding the "best" and "supported" careplans. New calls at /find/best/careplan/?Subject=xxxx & /find/supported/careplans/?subject=xxxx
- New type added MccCarePlanSummary
- All observation related fhir query keys and templates have changed to have mode specific versions. Consult fhirqueries.properties for the
  new keys and templates.
- All observation related queries now support a mode parameter which controls the specific observation code used for the
  query. Prior versions used the combo-code parameter to interact with the fhir server, but universal support for this parameter
  has been found to be lacking. The default mode for all searches is now "code", which will search only the top level of an
  observation. Three additional modes are present: "component", "combo", "panel".
- "component"  mode search will try to search only the component codes. Currently, this capability is not used by
  any client, and it is known not to be universally implemented. It is also used as a fall back when combo searches are requested
  and must be simulated by the api.
- "combo" mode will search both code and components for matches. For fhir implementations that do not support the combo
  parameter the API can simulated. The API will make a best effort to fulfill the callers request, so if the combo mode is disabled for
  a specific query the server will first search for matches by code and then by a component. The operation is safe such thar all modes may be disabled.
-  "panel" mode is used when the intent is to search for a grouping of values united by a single code. It is mainly for
   client documentation and handling purposes and use a "code" based FHIR query for the observation.
- New fhirquery keys
    - Observation.Query.code=/Observation?subject={subject}&code={code}&_sort=-date&_count={count}
    - Observation.Query.combo=/Observation?subject={subject}&combo-code={code}&_sort=-date&_count={count}
    - Observation.Query.panel=/Observation?subject={subject}&code={code}&_sort=-date&_count={count}
    - Observation.Query.component=/Observation?subject={subject}&component-code={code}&_sort=-date&_count={count}
    - Observation.QueryLatest.code=/Observation?subject={subject}&code={code}&_sort=-date&_count=1
    - Observation.QueryLatest.component=/Observation?subject={subject}&component-code={code}&_sort=-date&_count=1
    - Observation.QueryLatest.combo=/Observation?subject={subject}&combo-code={code}&_sort=-date&_count=1
    - Observation.QueryLatest.panel=/Observation?subject={subject}&code={code}&_sort=-date&_count=1
    - Observation.QueryValueSetExpanded.code=/Observation?subject={subject}&code={codes}&_sort=-date&_count={count}
    - Observation.QueryValueSetExpanded.combo=/Observation?subject={subject}&combo-code={codes}&_sort=-date&_count={count}
    - Observation.QueryValueSetExpanded.component=/Observation?subject={subject}&component-code={codes}&_sort=-date&_count={count}
    - Observation.QueryValueSetExpanded.panel=/Observation?subject={subject}&code={codes}&_sort=-date&_count={count}
- Retired fhirquery keys
    - Observation.Query
    - Observation.QueryLatest
    - Observation.QueryValueSetExpanded
