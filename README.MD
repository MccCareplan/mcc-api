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

 | Property Name | System Env. Variable eqv | Description | Dev | Prod |
 | -------------- | ---------------------- | ---------------- | --------- | ------ |
| server.port | SERVER_PORT | The port on which tomcat runs | 8081 | Tomcat Def (8080) |
| fhir.default.server.address | FHIR_DEFAULT_SERVER_ADDRESS | Default open fhir server| https://api.logicahealth.org/MCCeCarePlanTest/open | https://api.logicahealth.org/MCCeCarePlanTest/open |
| fhir.secure.server.address | FHIR_SECURE_SERVER_ADDRESS |Default secure fhir server | https://api.logicahealth.org/MCCeCarePlanTest/data | https://api.logicahealth.org/MCCeCarePlanTest/data |
| logging.level.org.springframework | | | WARN | WARN |
| mcc.patient.id.system | MCC_PATIENT_ID_SYSTEM | System used to determine the Patient Id| | | |
| mcc.provider.pullbyrole | MCC_PROVIDER_PULLBYROLE | Allow provider contact info to be fetched by practitioner role | TRUE | TRUE |
| mcc.careteam.use.active | MCC_CARETEAM_USE_ACTIVE | Enable Contacts to look for careteams if none are a part of the selected careplan | TRUE | TRUE |
| mcc.careteam.use.careplan | MCC_CARETEAM_USE_CAREPLAN | Enable Contacts to us the currently selected Care plan to determine teams | TRUE | TRUE |
| mcc.careplan.use.fallback | MCC_CAREPLAN_USE_FALLBACK | Enable fallback careplan matching | TRUE | TRUE |
| mcc.referral.require_performer | MCC_REFERRAL_REQUIRE_PERFORMER | If true a ServiceRequest must have a performer to be considered a Referral | TRUE | TRUE |
| mcc.codeableconcept.use.additive.normalization | MCC_CODEABLECONCEPT_USE_ADDITIVE_NORMALIZATION  | When true codings in codeable concepts that require normalization are preserved and a normalized copy is added | FALSE | FALSE |
#### Configuring the patient identifier

The default scheme for mapping to the display patient identifier is scan the identifier list based on use, using the identifier based on a rank (Official | Usual | Temp | Secondary) returning the first identifier of the highest rank. This behavior can be refined so that only identifier from a specific system will be selected.
Setting the property mcc.patient.id.system to the value of the required system will filter the list down matching identifier that match that system,
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


### Running with an FHIR Query Override file

Path to set the override file to: /usr/local/mcc-api/fhirqueries.properties using the -v parameter

$ docker run -v {Full Directory Path}:/usr/local/mcc-api -d -p 8080:8080 mcccareplan/mccapi

#### Confirming the configuration on a detached image
$ docker exec {image name} ls /usr/local/mcc-api
fhirqueries.properties

$ docker exec {image name} cat /usr/local/mcc-api/fhirqueries.properties


#### Worked example

$mkdir ~/mcc-api
$cp src/test/over
$docker run -v /Users/jerrygoodnough/mccapi:/usr/local/mcc-api -p 8081:8080 mcccareplan/mccapi

c.c.nih.niddk.mccapi.MccApiApplication   : The following profiles are active: prod
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
o.apache.catalina.core.StandardService   : Starting service [Tomcat]
org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.35]
o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 3319 ms
c.c.n.n.m.managers.FHIRServerManager     : Default FHIR Server = https://api.logicahealth.org/MCCeCarePlanTest/open
c.c.n.n.m.managers.FHIRServerManager     : Default FHIR Secure Server = https://api.logicahealth.org/MCCeCarePlanTest/data
c.c.n.n.mccapi.managers.QueryManager     : Using a FHIR Query Override file:/usr/local/mcc-api/fhirqueries.properties
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
c.c.nih.niddk.mccapi.MccApiApplication   : Started MccApiApplication in 7.955 seconds (JVM running for 9.071)


### Confirming the image is running

$ curl --location --request GET 'http://localhost:8080/conditionsummary?subject=cc-pat-pnoelle'

### Latest Images
The latest docker images are found on docker hub at https://hub.docker.com/repository/docker/mcccareplan/mccapi


#Recent Changes log


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



The full change log may be found and https://github.com/MccCareplan/mcc-api/blob/master/CHANGELOG.MD
