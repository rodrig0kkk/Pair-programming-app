package com.pairprogramming;

import java.time.LocalDateTime;

public class Task {
    private final String title;
    private final String description;
    private final Developer assignedTo;
    private final LocalDateTime createdAt;

    public Task(String title, String description, Developer assignedTo) {
        this.title = title == null ? "" : title.trim();
        this.description = description == null ? "" : description.trim();
        this.assignedTo = assignedTo;
        this.createdAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Developer getAssignedTo() {
        return assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return title + " (" + (assignedTo == null ? "sin asignar" : assignedTo.getName()) + ")";
    }
}
