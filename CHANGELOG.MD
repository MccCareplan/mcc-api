
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
