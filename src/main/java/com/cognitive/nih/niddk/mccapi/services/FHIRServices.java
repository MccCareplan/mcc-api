package com.cognitive.nih.niddk.mccapi.services;

import ca.uhn.fhir.context.FhirContext;

public class FHIRServices {

 private final FhirContext stu4Context;

 private static FHIRServices singleon = new FHIRServices();

 public static FHIRServices getFhirServices() { return singleon;}

 public FHIRServices()
 {
     stu4Context = FhirContext.forR4();
 }

 public FhirContext getR4Context()
 {
     return stu4Context;
 }
}
