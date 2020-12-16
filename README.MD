# Introduction
This application is the backend to a Multiple Chronic Condition CarePlan Application set.

It acts a the primary data anayslis layer and relies on one or more FHIR servers as the source of primary information.

# Public Deployments
https://mcc-niddk-backend.wl.r.appspot.com/

deployed using:  $gcloud app deploy

# Open API
UI: https://mcc-niddk-backend.wl.r.appspot.com/swagger-ui.html
DOCS: https://mcc-niddk-backend.wl.r.appspot.com/api-docs

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

