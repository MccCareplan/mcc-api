package com.cognitive.nih.niddk.mccapi.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Data
public class Contact {

    public static String TYPE_PERSON = "person";
    public static String TYPE_ORGANIZATION = "organization";

    public static String ROLE_PATIENT = "Patient";
    public static String ROLE_ALTERNATE_CONTACT = "Alternate Contatc";
    public static String ROLE_PROVIDER = "Provider";
    public static String ROLE_PRIMARY_CARE = "PCP - Primary care physician";
    public static String ROLE_INSURANCE = "Insurance";
    public static String ROLE_EMERGENCY = "Emergency Contact";

    @NotBlank
    private String type;
    @NotBlank
    private String role;
    @NotBlank
    private String name;
    private boolean hasImage = false;
    private String phone;
    private String email;
    private String address;
    private String organizationName; //The the of the Name of the organization the person is associated with.
    private String relFhirId;   //FHIR Id for full resoures
    private String teamId;    //Care Team Id if the member come from a care team
    private String teamName;   //Name of the care team (if any)

}
