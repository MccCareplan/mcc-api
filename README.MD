# Introduction
This applicationis the backend to a Multiple Chronic Condition CarePlan Application set.

It acts a the primary data anayslis layer and relies on one or more FHIR servers as the source of primary information.

# Public Deployments
https://mcc-niddk-backend.wl.r.appspot.com/

# Open API
UI: https://mcc-niddk-backend.wl.r.appspot.com/swagger-ui/index.html?configUrl=/api-docs/swagger-config
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
