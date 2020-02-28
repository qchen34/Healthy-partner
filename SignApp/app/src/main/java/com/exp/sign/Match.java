package com.exp.sign;

import java.util.List;

public class Match {

    private List<Integer> day;
    private String availableTime;
    private String purpose;
    private String interests;
    private String ageGroup;

    public Match() {
    }

    public Match(List<Integer> day, String availableTime, String purpose, String interests, String ageGroup) {
        this.day = day;
        this.availableTime = availableTime;
        this.purpose = purpose;
        this.interests = interests;
        this.ageGroup = ageGroup;
    }

    public List<Integer> getDay() {
        return day;
    }

    public void setDay(List<Integer> day) {
        this.day = day;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
