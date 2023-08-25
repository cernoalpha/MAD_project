package com.example.mad.task;

import java.io.Serializable;

public class Task implements Serializable{
    private String taskId;
    private String userId; // Added field for user ID
    private String title;
    private String description;
    private int progress;
    private String startDate;
    private String endDate;
    private String category;

    // Default constructor (required for Firebase)
    public Task() {
    }

    public Task(String userId, String title, String description, int progress, String startDate, String endDate) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = "";
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
