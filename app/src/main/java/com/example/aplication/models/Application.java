package com.example.aplication.models;

public class Application {
    private String applicationId;
    private String jobId;
    private String workerImage;
    private String companyImage;
    private String jobTitle;
    private String workerEmail;
    private String companyEmail;
    private String applicationDate;
    private String status;

    public Application() {}

    public Application(String applicationId, String jobId, String workerImage, String companyImage, String jobTitle, String workerEmail, String companyEmail, String applicationDate, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.workerImage = workerImage;
        this.companyImage = companyImage;
        this.jobTitle = jobTitle;
        this.workerEmail = workerEmail;
        this.companyEmail = companyEmail;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getWorkerImage() { return workerImage; }
    public void setWorkerImage(String workerImage) { this.workerImage = workerImage; }

    public String getCompanyImage() { return companyImage; }
    public void setCompanyImage(String companyImage) { this.companyImage = companyImage; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getWorkerEmail() { return workerEmail; }
    public void setWorkerEmail(String workerEmail) { this.workerEmail = workerEmail; }

    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}