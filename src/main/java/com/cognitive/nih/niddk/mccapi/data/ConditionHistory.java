package com.cognitive.nih.niddk.mccapi.data;

public class ConditionHistory {
    private String onset;
    private String abatement;
    private String FHIRid;
    private String clinicalStatus;
    private String verificationStatus;

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public String getAbatement() {
        return abatement;
    }

    public void setAbatement(String abatement) {
        this.abatement = abatement;
    }

    public String getFHIRid() {
        return FHIRid;
    }

    public void setFHIRid(String FHIRid) {
        this.FHIRid = FHIRid;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
