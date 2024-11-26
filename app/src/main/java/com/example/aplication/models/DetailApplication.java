package com.example.aplication.models;

public class DetailApplication {
    private String applicationId;
    private String motivation;
    private String experience;
    private String availability;
    private String location;

    public DetailApplication() {}

    public DetailApplication(String applicationId, String motivation, String experience, String availability, String location) {
        this.applicationId = applicationId;
        this.motivation = motivation;
        this.experience = experience;
        this.availability = availability;
        this.location = location;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}