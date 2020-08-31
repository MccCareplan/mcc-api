package com.cognitive.nih.niddk.mccapi.data;

import com.cognitive.nih.niddk.mccapi.data.primative.MccCodeableConcept;

public class GoalSummary {
    private String FHIRId;

    private String priority; //Extracted Code
    private String expressedByType;
    private String description;
    private MccCodeableConcept achievementStatus;
    private String lifecycleStatus;
    private GoalTarget[] targets;

    public String getFHIRId() {
        return FHIRId;
    }

    public void setFHIRId(String FHIRId) {
        this.FHIRId = FHIRId;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getExpressedByType() {
        return expressedByType;
    }

    public void setExpressedByType(String expressedByType) {
        this.expressedByType = expressedByType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MccCodeableConcept getAchievementStatus() {
        return achievementStatus;
    }

    public void setAchievementStatus(MccCodeableConcept achievementStatus) {
        this.achievementStatus = achievementStatus;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public GoalTarget[] getTargets() {
        return targets;
    }

    public void setTargets(GoalTarget[] targets) {
        this.targets = targets;
    }
}
