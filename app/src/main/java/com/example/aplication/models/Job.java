package com.example.aplication.models;

public class Job {
    private String jobId;
    private String companyImage;
    private String companyEmail;
    private String title;
    private String description;
    private String expirationDate;
    private int vacancies;
    private String jobMode;
    private String salary;

    public Job() {}

    public Job(String jobId, String companyImage, String companyEmail, String title, String description, String expirationDate, int vacancies, String jobMode, String salary) {
        this.jobId = jobId;
        this.companyImage = companyImage;
        this.companyEmail = companyEmail;
        this.title = title;
        this.description = description;
        this.expirationDate = expirationDate;
        this.vacancies = vacancies;
        this.jobMode = jobMode;
        this.salary = salary;
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getCompanyImage() { return companyImage; }
    public void setCompanyImage(String companyImage) { this.companyImage = companyImage; }

    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public int getVacancies() { return vacancies; }
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }

    public String getJobMode() { return jobMode; }
    public void setJobMode(String jobMode) { this.jobMode = jobMode; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
}