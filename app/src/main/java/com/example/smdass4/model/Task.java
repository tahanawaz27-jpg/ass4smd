package com.example.smdass4.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Task implements Serializable {
    private String id;
    private String description;
    private String status; // "Overdue", "In Progress", "To Do", "Done"
    private String priority; // "High", "Normal", "Low"
    private String dueDate; // e.g., "15 May 2022" or "No due date"
    private boolean completed;

    public Task() {
        this.id = UUID.randomUUID().toString();
    }

    public Task(String id, String description, String status, String priority, String dueDate, boolean completed) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public Task copy() {
        return new Task(this.id, this.description, this.status, this.priority, this.dueDate, this.completed);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed &&
                Objects.equals(id, task.id) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                Objects.equals(priority, task.priority) &&
                Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, status, priority, dueDate, completed);
    }
}