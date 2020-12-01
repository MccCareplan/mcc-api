# Introduction
This applicatis the backend to a Multiple Chronic Condition CarePlan Application set.

It acts a the primary data anayslis layer and relies on one or more FHIR servers as the source of primary information.

# Public Deployments
https://mcc-niddk-backend.wl.r.appspot.com/

deployed using:  $gcloud app deploy

# Open API
UI: https://mcc-niddk-backend.wl.r.appspot.com/swagger-ui.html
DOCS: https://mcc-niddk-backend.wl.r.appspot.com/api-docs

# Security
Currently this version of the service is using fixed open endpoints. In the very near future all client requests will carry security information will be used by this tier to access the FHIR server,

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

### Running with environement variables 

The follow is an example of passing a new default FHIR server address to the API and running detached.

$ docker run -d -e FHIR_DEFAULT_SERVER_ADDRESS='http://myfhireServer' -p 8080:8080 mcccareplan/mccapi

### Confirming the image is running

$ curl --location --request GET 'http://localhost:8080/conditionsummary?subject=cc-pat-pnoelle'

### Latest Images
The latest docker images are found on docker hub at https://hub.docker.com/repository/docker/mcccareplan/mccapi

