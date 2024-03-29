# Introduction
This application is the backend to a Multiple Chronic Condition CarePlan Application set.

It acts as the primary data analyses layer and relies on one or more FHIR servers as the source of primary information.

# Public Deployments
https://mcc-niddk-backend.wl.r.appspot.com/

deployed using:  $gcloud app deploy

# Open API
| | URL |
| -------------- | ---------------------- |
| Public UI | https://mcc-niddk-backend.wl.r.appspot.com/swagger-ui/index.html?configUrl=/api-docs/swagger-config |
| Public DOCS | https://mcc-niddk-backend.wl.r.appspot.com/api-docs |
| Local Dev UI | http://localhost:8081//swagger-ui/index.html?configUrl=/api-docs/swagger-config |
| Local Dev DOCS | http://localhost:8081/api-docs |

# Security
Currently, this version of the service is using fixed open endpoints. In the very near future all client requests will carry security information will be used by this tier to access the FHIR server,

# Issues
## Type mapping and FHIR
The FHIR native type system includes infinite recursion, as such many native tools that operate of the schema will break. Examples include Jackson Marshalling/UnMarshalling. For direct FHIR requests the HAPI Client library is being uses and it's embedded marshalling used. At this point variants of the core types asre used by this application with optimizations for the User interface. 
Preliminary testings of the effect of the use of Native FHIR types on OpenAPI/Swagger are unfavorable, detail below. In the event that OpenAPI issues can handled it would be desirable to update Jackson Object Mapper to use HAPI to Marshall/UnMashall FHIR types.
### Types requiring custom marshalling
- org.h7.fhir.r4.model.Base
### Notes
- Looking at the R4 tree it appears that both Types and Resources both inherit from Base.
- Preliminary investigation show that Swagger will break on Resources like Observation, but some types like Address work, but lead to wordy definitions.
### Types that Work with SWAGGER
- Address
- CodableConcept
- Coding
### Types that fail with SWAGGER
- Duration -  Conflicting setter definitions for property "value"
- Quantity - Conflicting setter definitions for property "value"
- Observation - Conflicting setter definitions for property "referenceElement"
- Reference - Conflicting setter definitions for property "referenceElement"

##  Customizing FHIR Queries
All primary FHIR queries are now loaded from the file fhirqueries.properties (https://github.com/MccCareplan/mcc-api/blob/master/src/main/resources/fhirqueries.properties).
This file should be used as a reference to queries in use and the parameters accepted by these calls by default
If a file called fhirqueries.properties is placed in /usr/local/mcc-api any queries found in there will take precedence over the normal configuration. 
For implementations that are overriding these queries it is recommended that they examine any changes and additions in the main fhirqueries.properties file before upgrading to determine if any of their overrides need to be updates.

When overriding a specific query it is possible to disable it by set it to empty.

For example the MedicationStatement query normally looks like:

MedicationStatement.Query=/MedicationStatement?subject={subject}

to disable it the following would be placed in the override file:

MedicationStatement.Query=

### Notes on the Override system
The existing templates include the basic parameters passed to the rest endpoints in the API. 
There controllers have been engineered to accept open parameter list tht can be used by the templates system to allow custom consumers to meet their particular needs.

For example the medication statement query looks like:

MedicationStatement.Query=/MedicationStatement?subject={subject}

If the consumer wanted to restict the scope of of the query to pass in a date string the query might look like:

MedicationStatement.Query=/MedicationStatement?subject={subject}&date={date}

The call to medications summary might look like:

{Server}/medicationsummary/?subject=1122&date=ge2018-10-1

This should result in a FHIR query for MedicationStatements for patient 1122 that are after Oct 1st 2018. 
The medication summary would then include all MedicationRequests and MedicationStatements after 10/1/2018. 
In this example the MediciationRequest.Query could also be updated to take the date parameter if desired.  







## Environment Variables
All environment variables are stored in the applications.properties file. 
In the default configuration the profiles that are provided are: [default], dev and prod. 
The profile used may be overridden at runtime by either selecting a different profile 
or by providing and entirely different definition for the properties file. See the 
details on Spring boot.

### Core Operational Environment Variables

 | Property Name | System Env. Variable eqv | Description | Dev | Prod |
 | -------------- | ---------------------- | ---------------- | --------- | ------ |
 | fhir.default.server.address | FHIR_DEFAULT_SERVER_ADDRESS | Default open fhir server| https://api.logicahealth.org/MCCeCarePlanTest/open | https://api.logicahealth.org/MCCeCarePlanTest/open |
 | fhir.secure.server.address | FHIR_SECURE_SERVER_ADDRESS |Default secure fhir server | https://api.logicahealth.org/MCCeCarePlanTest/data | https://api.logicahealth.org/MCCeCarePlanTest/data |
 | logging.level.org.springframework | | | WARN | WARN |
 | server.port | SERVER_PORT | The port on which tomcat runs | 8081 | Tomcat Def (8080) |
 | fhir.request.timeout |  FHIR_REQUEST_TIMEOUT| FHIR Request TCP Timeout in Milliseconds | 30000 | 30000 |
 | fhir.connect.timeout | FHIR_CONNECT_TIMEOUT | FHIR Initial connection TCP Timeout in Milliseconds | 30000 | 30000 |

### Environment variables for API Behavior control
 | Property Name | System Env. Variable eqv | Description | Dev | Prod |
 | -------------- | ---------------------- | ---------------- | --------- | ------ |
 | mcc.careteam.use.active | MCC_CARETEAM_USE_ACTIVE | Enable Contacts to look for careteams if none are a part of the selected careplan | TRUE | TRUE |
 | mcc.careteam.use.careplan | MCC_CARETEAM_USE_CAREPLAN | Enable Contacts to us the currently selected Care plan to determine teams | TRUE | TRUE |
 | mcc.careplan.use.fallback | MCC_CAREPLAN_USE_FALLBACK | Enable fallback careplan matching | TRUE | TRUE |
 | mcc.codeableconcept.use.additive.normalization | MCC_CODEABLECONCEPT_USE_ADDITIVE_NORMALIZATION  | When true codings in codeable concepts that require normalization are preserved and a normalized copy is added | FALSE | FALSE |
 | mcc.observation.egfr.calculate | MCC_OBSERVATION_EGFR_CALCULATE | When true eGFR is calculated used Scr and the CKD-EPI | TRUE | FALSE |
 | mcc.observation.log.calls | MCC_OBSERVATION_LOG_CALLS | When true any extra logging around observations calls is enabled | TRUE | FALSE |
 | mcc.observation.log.unit.failure |MCC_OBSERVATION_LOG_UNIT_FAILURE | When true any observations that are rejected due to expected errors are loggged | TRUE | FALSE | 
 | mcc.patient.id.system | MCC_PATIENT_ID_SYSTEM | System used to determine the Patient Id| | | |
 | mcc.patient.reported.data.use.observations | MCC_PATIENT_REPORTED_DATA_USE_OBSERVATIONS | When true observations will be check for patient reported reported data | TRUE | TRUE |
 | mcc.patient.reported.data.use.questionnaires | MCC_PATIENT_REPORTED_DATA_USE_QUESTIONNAIRES | When true questionnaire responses will be check for patient reported data | TRUE | TRUE |
 | mcc.provider.pullbyrole | MCC_PROVIDER_PULLBYROLE | Allow provider contact info to be fetched by practitioner role | TRUE | TRUE |
 | mcc.referral.require_category | MCC_REFERRAL_REQUIRE_CATEGORY| If true a ServiceRequest must have a category to be considered a Referral | FALSE | FALSE |
 | mcc.referral.require_performer | MCC_REFERRAL_REQUIRE_PERFORMER | If true a ServiceRequest must have a performer to be considered a Referral | TRUE | TRUE |
 | mcc.referral.use_valueset| MCC_REFERRAL_USE_VALUESSET | If true a ServiceRequest in valueset to be considerred a Referral | TRUE | TRUE |
 | mcc.request.header.require.server | MCC_REQUEST_HEADER_REQUIRE_SERVER | If true only requests with the header mcc-fhir-server are considered valid | FALSE | TRUE | 
 | mcc.request.header.require.token | MCC_REQUEST_HEADER_REQUIRE_TOKEN | If true only requests with the header mcc-token are considered valid | FALSE | TRUE |
 | mcc.social_concern.use_category | MCC_SOCIAL_CONCERN_USE_CATEGORY | If true Social Concerns use the HealthConcern Category | TRUE | TRUE |
 | mcc.social_concern.use_valueset | MCC_SOCIAL_CONCERN_USE_VALUESET | If true Social Concerns are identified by membership in a valuse set | FALSE | FALSE |
 
#### Configuring the patient identifier

The default scheme for mapping to the display patient identifier is scan the identifier list based on use, using the identifier based on a rank (Official | Usual | Temp | Secondary) returning the first identifier of the highest rank. This behavior can be refined so that only identifier from a specific system will be selected.
Setting the property mcc.patient.id.system to the value of the required system will filter the list down by matching the identifiers that match that system,
For example if the property is set to urn:oid:2.16.840.1.113883.3.2076.2.100 then the OHSU medical record number would be used, while setting the value to urn:oid:2.16.840.1.113883.4.3.41 would search for Oregon Driver's Licenses.
Ideally identifiers could be select by type code (https://www.hl7.org/fhir/valueset-identifier-type.html). Current real world data does not seem to support this.


#### Logging related environment variables 
 | Property Name | System Env. Variable eqv | Description | Dev | Prod |
 | -------------- | ---------------------- | ---------------- | --------- | ------ |
| hapi.logging.enabled | HAPI_LOGGING_ENABLED | Enable logging  | true | false | 
| hapi.logging.request.summary | HAPI_LOGGING_REQUEST_SUMMARY | Log the request summary | true | true |
| hapi.logging.request.body | HAPI_LOGGING_REQUEST_BODY | Log the request body | false | false |
| hapi.logging.request.header | HAPI_LOGGING_REQUEST_HEADER | Log the request header | false | false |
| hapi.logging.response.summary | HAPI_LOGGING_RESPONSE_SUMMARY | Log the response summary | true | true
| hapi.logging.response.body | HAPI_LOGGING_RESPONSE_BODY | Log the response body | false | false |
| hapi.logging.response.header | HAPI_LOGGING_RESPONSE_HEADER | Log the response header | false | false |

### Overriding via command line

 $java -jar target/mcc-api-1.0.2Y-SNAPSHOT.jar --fhir.secure.server.address='testvalue'

### Overriding via ENVIRONMENT variable

 $export  FHIR_SECURE_SERVER_ADDRESS=https://localhost:9011
 

## Docker

### Building

$ mvn clean install -Pprod

$ docker build -t mcccareplan/mccapi .

### Pushing
$ docker push mcccareplan/mccapi


### Running

Running mcc-api with docker on port 8080

$ docker run -p 8080:8080 mcccareplan/mccapi

### Running Detached

$ docker run -d -p 8080:8080 mcccareplan/mccapi

### Running with environment variables 

The follow is an example of passing a new default FHIR server address to the API and running detached.

$ docker run -d -e FHIR_DEFAULT_SERVER_ADDRESS='http://myfhireServer' -p 8080:8080 mcccareplan/mccapi


### Running with Customization

All basis customization is done by adding files to a mapped area of the docker container. From the container perspective the root directory is /usr/local/mcc-api. 

#### General Directory Structure used
>
>
>       mcc-api (root  - on container file system this is /usr/local/mcc-api)
>         +----- valuesets
>                   +----------- override  
>                   +----------- supplement

#### Using FHIR Query Override file

Path to set the override file to: /usr/local/mcc-api/fhirqueries.properties using the -v parameter. Once you have established the customization file linkage for the docker container you just need to place your fhirqueries.properties in the root location.

> $ docker run -v {Full Directory Path}:/usr/local/mcc-api -d -p 8080:8080 mcccareplan/mccapi


##### Confirming the configuration on a detached image
>   $ docker exec {image name} ls /usr/local/mcc-api
> 
>   fhirqueries.properties
> 
>   $ docker exec {image name} cat /usr/local/mcc-api/fhirqueries.properties


##### Worked example

> $mkdir ~/mcc-api
> 
> $cp src/test/over
> 
> $docker run -v /Users/jerrygoodnough/mccapi:/usr/local/mcc-api -p 8081:8080 mcccareplan/mccapi
>
> c.c.nih.niddk.mccapi.MccApiApplication   : The following profiles are active: prod
> 
> o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
> 
> o.apache.catalina.core.StandardService   : Starting service [Tomcat]
> 
> org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.35]
> 
> o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
> 
> o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 3319 ms
> 
> c.c.n.n.m.managers.FHIRServerManager     : Default FHIR Server = https://api.logicahealth.org/MCCeCarePlanTest/open>
>
> c.c.n.n.m.managers.FHIRServerManager     : Default FHIR Secure Server = https://api.logicahealth.org/MCCeCarePlanTest/data
> 
> c.c.n.n.mccapi.managers.QueryManager     : Using a FHIR Query Override file:/usr/local/mcc-api/fhirqueries.properties
> 
> o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
> 
> c.c.nih.niddk.mccapi.MccApiApplication   : Started MccApiApplication in 7.955 seconds (JVM running for 9.071)

### Overriding and Supplementing Value Sets
In order to override or supplement contents on the value sets used the docker container must be configured with a customization directory. Blow that directory there must be a subdirectory named "valuesets".
Below this directory there should be two subsdirectorys "override" and "supplement". See the section above on general directory structure.

All values sets are stored as .cvs files - The exact set pof valuesets used and thier initial values can be fould by looking at the source code on github under /src/main/resources/valuesets. The file name is the same as the value set name.

The general form of the csv file is in the following example fragment:

>   System,Version,Code,Display
>
>   http://snomed.info/sct,2020-09,10725009,Benign hypertension (disorder)

To customize the value set contents deployers have two related options. First they may choose to totally replace an valueset being usef by the system. 
This is called overriding the valueset. Secondly a deployer may choose to extend a valueset with additional codes, this is called supplementing the valueset.
Generally deployers should choose to override a valueset if the stock value set contains value that you do not want to use, otherwise it is suggested that supplementing the valueset and extending with the addtional code is best.
The actions of overriding and supplementing a valueset are not exclusive, and is possible to do both for a single valueset.

As a simple example; suppose you want to add a code to the concept of the Labs what make up eGFR (Estimated Glomerular filtration rate). This value set is identified as "2.16.840.1.113762.1.4.1222.179". 
The name of the file for this valueset is therefore "2.16.840.1.113762.1.4.1222.179.csv".

If you wanted to add an additional code then you would create a .cvs file with this name with the additional code as the second line after the column headers. 
This file would then be placed under "mcc-api/valuseset/supplement" directory and on the container startup the file will be parsed and new code added to the value set. 

The log file on the server will indicate if a valueset is overriden and/or supplemented.

Overriding a value set is similar. It is suggested that deployers first copy the basic value set out of github, make their edits to it and then place it under "mcc-api/valuseset/override".


#### Value Sets - General Info

Most of the value sets referred are available on VSAC and what is included in the application is an expanded snapshot of the values sets. There are however as few application specific value sets that not maintained in a standard form. The table below decribes these.

|Name | File | Description |
|---------- | --------- | ------ |
| Referrals | Referrals.csv | List of those Service requests considered to be a referral. Based on the Implementation Guide|
| SocialConcerns | SocialConcerns.cvs | List of Conditions considered to be social concerns. Based on the Gravity project |

### Confirming the image is running

$ curl --location --request GET 'http://localhost:8080/conditionsummary?subject=cc-pat-pnoelle'

### Latest Images
The latest docker images are found on docker hub at https://hub.docker.com/repository/docker/mcccareplan/mccapi


#Recent Changes log
## Release 1.0.30
- Updated all VSAC Value sets to the current expansion as of 8/23/21 added
- Added mcc.observation.egfr.calculate to allow eGFR to be calculated based on Serum Creatinine-based using the formula (CKD-EPI)
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
  + ConditionHistory updated for recorded and recordedAsText
- Observation calls now support a list of required units via the optional parameter requiredunits. The list is comma seperated and the first entry is use as a default when no unit is specified. This parameter currently acts as a filter and no unit conversion is attempted.

## Release 1.0.23
  + Added observation related Diagnostics  


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
- fixed check on care team id
- Removed static call to QuestionnaireResponse Mapper.

## Release 1.0.16-RELEASE
- Upgraded to spring-boot-starter-parent 2.4.2
- Resolves issues with path parsing 



The full change log may be found and https://github.com/MccCareplan/mcc-api/blob/master/CHANGELOG.MD
