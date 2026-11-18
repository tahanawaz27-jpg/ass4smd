package com.example.smdass4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Project implements Serializable {
    private String id;
    private String tag; // "User Interface", "Backend", "Frontend", "QA & Ops", "Design"
    private String name;
    private String description;
    private int commentsCount;
    private List<Task> tasks;
    private List<Attachment> attachments;

    public Project() {
        this.tasks = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    public Project(String id, String tag, String name, String description, int commentsCount, List<Task> tasks, List<Attachment> attachments) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.commentsCount = commentsCount;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }

    public Project copy() {
        List<Task> copiedTasks = new ArrayList<>();
        if (this.tasks != null) {
            for (Task t : this.tasks) {
                copiedTasks.add(t.copy());
            }
        }
        List<Attachment> copiedAttachments = new ArrayList<>();
        if (this.attachments != null) {
            for (Attachment a : this.attachments) {
                copiedAttachments.add(a.copy());
            }
        }
        return new Project(this.id, this.tag, this.name, this.description, this.commentsCount, copiedTasks, copiedAttachments);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public int getCompletedTaskCount() {
        int count = 0;
        if (tasks != null) {
            for (Task t : tasks) {
                if (t.isCompleted() || "Done".equalsIgnoreCase(t.getStatus())) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getTotalTaskCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public int getProgressPercentage() {
        int total = getTotalTaskCount();
        if (total == 0) return 0;
        return (int) Math.round(((double) getCompletedTaskCount() / total) * 100);
    }

    public int getAttachmentCount() {
        return attachments != null ? attachments.size() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return commentsCount == project.commentsCount &&
                Objects.equals(id, project.id) &&
                Objects.equals(tag, project.tag) &&
                Objects.equals(name, project.name) &&
                Objects.equals(description, project.description) &&
                Objects.equals(tasks, project.tasks) &&
                Objects.equals(attachments, project.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tag, name, description, commentsCount, tasks, attachments);
    }
}