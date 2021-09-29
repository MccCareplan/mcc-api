/*Copyright 2021 Cognitive Medical Systems*/
package com.cognitive.nih.niddk.mccapi.data.primative;

import org.hl7.fhir.r4.model.*;

public class SwaggerTypeTest {

    private Address address;
    private CodeableConcept codeableConcept;
    private Coding coding;
    //private Duration duration;
    //private Quantity quantity;
    //private Observation observation;
    //private Reference reference;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CodeableConcept getCodeableConcept() {
        return codeableConcept;
    }

    public void setCodeableConcept(CodeableConcept codeableConcept) {
        this.codeableConcept = codeableConcept;
    }

    public Coding getCoding() {
        return coding;
    }

    public void setCoding(Coding coding) {
        this.coding = coding;
    }



}
