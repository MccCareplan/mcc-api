package com.cognitive.nih.niddk.mccapi.data;

public class MccPatient {
    private String FHIRId;
    private String Name;
    private String DateOfBirth;
    private String Gender;
    private String Race;
    private String Ethnicity;
    private String Id;

    public String getFHIRId() {
        return FHIRId;
    }

    public void setFHIRId(String FHIRId) {
        this.FHIRId = FHIRId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getRace() {
        return Race;
    }

    public void setRace(String race) {
        Race = race;
    }

    public String getEthnicity() {
        return Ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        Ethnicity = ethnicity;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

