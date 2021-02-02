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


The full change log may be found and https://github.com/MccCareplan/mcc-api/blob/master/CHANGELOG.MD
